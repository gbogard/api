package lambda.infrastructure.code

import java.io.File

import cats.data.EitherT
import cats.effect._
import com.typesafe.scalalogging.StrictLogging
import coursier._
import coursier.cache._
import coursier.interop.cats._
import lambda.domain.code._
import lambda.domain.code.ScalaCodeRunner
import lambda.domain.code.ScalaCodeRunner._
import lambda.infrastructure.Utils._
import lambda.infrastructure.ExternalDependencies.Scala2
import scala.tools.nsc._
import scala.sys.process._
import scala.concurrent.duration._
import scala.tools.nsc.reporters.StoreReporter

object ScalaCodeRunnerImpl extends ScalaCodeRunner[IO] with StrictLogging {

  def run(
      files: List[File],
      mainClass: String,
      dependencies: List[ScalaDependency] = Nil,
      timeout: FiniteDuration = 30 seconds
  ): ProcessResult[IO] =
    wrapEitherInResource(
      createTemporaryFolder[IO](),
      (folder: File) => {
        for {
          dependencies <- fetchDependencies(dependencies)
          _ <- compileFiles(files, dependencies, folder)
          output <- withTimeout(run(folder, mainClass), timeout)
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

  private def compileFiles(
      files: Seq[File],
      dependenciesClasses: Seq[File],
      destFolder: File
  ): ProcessResult[IO] = {

    EitherT[IO, String, String] {
      IO {
        val reporter = new StoreReporter
        val settings = new Settings
        settings.embeddedDefaults(getClass.getClassLoader)
        settings.usejavacp.value_=(true)
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
                val pathRegex = "\\/.*\\/".r
                val path = pathRegex.replaceAllIn(info.pos.toString(), "lambda/")
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
      mainClass: String
  ): EitherT[IO, String, String] = EitherT {
    Security.securityPolicyFile[IO] use { securityPolicyFile =>
      val securityPolicyFlag = s"-Djava.security.policy==${securityPolicyFile.getAbsolutePath()}"
      val cpFlag = s"-cp ${compiledClassesFolder.getAbsolutePath()}"
      val cmd = s"${Scala2.scala} $cpFlag ${Security.securityMangerFlag} $securityPolicyFlag $mainClass"
      StringProcessLogger.run(Process(cmd)).value
    }
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

  private object Security {
    val securityMangerFlag = "-Djava.security.manager"
    def securityPolicyFile[F[_]: Sync] =
      readResource[F]("/security/jvm-security.policy")
  }

}
