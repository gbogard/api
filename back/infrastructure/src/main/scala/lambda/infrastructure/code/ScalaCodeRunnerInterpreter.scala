package lambda.infrastructure.code

import java.io.File
import java.util.UUID

import cats.data.EitherT
import cats.effect._
import cats.effect.concurrent.Semaphore
import com.typesafe.scalalogging.StrictLogging
import coursier._
import coursier.cache._
import coursier.interop.cats._
import lambda.domain.code._
import lambda.domain.code.ScalaCodeRunner
import lambda.domain.code.ScalaCodeRunner._
import lambda.infrastructure.Utils._

import scala.tools.nsc._
import scala.sys.process._
import scala.concurrent.duration._
import scala.tools.nsc.reporters.StoreReporter
import lambda.infrastructure.{Configuration, Docker, ExecutionContexts}
import com.colisweb.tracing._
import com.colisweb.tracing.implicits._

class ScalaCodeRunnerInterpreter(dockerPermits: Semaphore[IO])(implicit config: Configuration)
    extends ScalaCodeRunner[IO]
    with StrictLogging {

  def run(
      files: List[File],
      mainClass: String,
      dependencies: List[ScalaDependency] = Nil,
      timeout: FiniteDuration = 15 seconds,
  )(implicit tracingContext: TracingContext[IO]): ProcessResult[IO] =
    wrapEitherInResource(
      createTemporaryFolder[IO](),
      (folder: File) => {
        for {
          dependencies <- tracingContext.childSpan("Fetching dependencies") either { _ =>
            fetchDependencies(dependencies)
          }
          _ <- tracingContext.childSpan("Compiling Scala sources") either { _ =>
            compileFiles(files, dependencies, folder)
          }
          output <- tracingContext.childSpan("Running Scala program in container") either { childCtx =>
            withTimeout(run(folder, mainClass, childCtx), timeout)
          }
        } yield output
      }
    )

  private def fetchDependencies(dependencies: List[ScalaDependency]): EitherT[IO, String, Seq[File]] =
    if (dependencies.nonEmpty) {
      logger.debug(
        "Fetching scala dependencies using Coursier : {}",
        dependencies
      )
      val coursierIO =
        Fetch(cache)
          .addDependencies(dependencies.map(toCoursierDependency(_)): _*)
          .io
      EitherT.right(coursierIO)
    } else EitherT.rightT(Nil)

  private val extractPosRegex = ".*?,(.*)".r

  private def compileFiles(
      files: Seq[File],
      dependenciesClasses: Seq[File],
      destFolder: File
  ): ProcessResult[IO] = {

    EitherT[IO, String, String] {
      IO {
        val reporter = new StoreReporter
        val settings = new Settings
        settings.embeddedDefaults[ScalaCodeRunnerInterpreter]

        /*
        When the app is packaged in a JAR by assembly, this flag is needed
        for scala code execution. Otherwise we get "object scala in compiler mirror not found"
        error.

        But we use this flag with sbt, in dev mode, another error occurs. So we need this flag to
        be conditional for the runner to work in all cases. Not exactly sure why.
         */
        settings.usejavacp.value_=(!config.isDev)

        val global = new Global(settings, reporter)
        val run = new global.Run
        dependenciesClasses.foreach(f => settings.classpath.append(f.getAbsolutePath()))
        settings.outdir.value_=(destFolder.getAbsolutePath())
        run.compile(files.toList.map(_.getAbsolutePath()))

        val infos = reporter.infos.toList
        if (infos.exists(_.severity == reporter.ERROR)) {
          Left(
            infos
              .map(info => {
                val extractPosRegex(pos) = info.pos.toString
                val path = s"sourceFile.scala,$pos"
                s"${info.severity} in $path : ${info.msg}"
              })
              .mkString("\r\n\r\n")
          )
        } else {
          Right("Everything is fine")
        }
      }
    }
  }

  private def run(
      compiledClassesFolder: File,
      mainClass: String,
      tracingContext: TracingContext[IO]
  ): EitherT[IO, String, String] = EitherT {

    val classPath = List(
      compiledClassesFolder.getAbsolutePath
        .replace(config.temporaryFoldersBase.containerPath, config.temporaryFoldersBase.hostPath),
      config.scalaUtilsClassPath.hostPath
    )
    val securityPolicyFlag =
      s"-Djava.security.policy==${config.sharedFiles.containerPath}/${Security.Jvm.securityPolicyFileName}"

    dockerPermits withPermit (for {
      containerName <- IO { "scala-" + UUID.randomUUID().toString }
      cpVolumes <- Docker.createVolumeNames[IO](classPath, Docker.scalaHomeDirectory)
      sharedFoldersVolume = s" -v ${config.sharedFiles.hostPath}:${config.sharedFiles.containerPath}"
      volumesFlag = Docker.volumeNamesToFlags(cpVolumes) + sharedFoldersVolume
      cpFlag = "-cp " + cpVolumes.values.mkString(":")
      cpusFlag = s"--cpus ${config.defaultCpusLimit}"
      maxHeapSizeFlag = "-J-Xmx100m"
      containerNameFlag = s"--name $containerName"
      scalaCmd = s"${Docker.scalaPath} $maxHeapSizeFlag $cpFlag ${Security.Jvm.securityManagerFlag} $securityPolicyFlag $mainClass"
      cmd = s"docker run $cpusFlag $volumesFlag $containerNameFlag --rm ${Docker.scalaImage} $scalaCmd"
      killContainer = tracingContext.childSpan("Killing container") wrap IO {
        s"docker kill $containerName".!!
        ()
      }
      result <- tracingContext.childSpan("Docker run") wrap {
        StringProcessLogger.run(Process(cmd), killContainer)(ExecutionContexts.codeRunnerEc).value
      }
    } yield result)
  }

  implicit private val cs: ContextShift[IO] =
    IO.contextShift(scala.concurrent.ExecutionContext.global)
  implicit private val timer: Timer[IO] =
    IO.timer(scala.concurrent.ExecutionContext.global)

  private val cache = FileCache[IO]()

  private def toCoursierDependency(dep: ScalaDependency) = Dependency.of(
    Module(Organization(dep.org), ModuleName(s"${dep.name}_${dep.scalaVersion}")),
    dep.version
  )
}