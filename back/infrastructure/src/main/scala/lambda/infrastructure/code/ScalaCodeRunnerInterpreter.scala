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
      timeout: FiniteDuration = 15 seconds
  )(implicit tracingContext: TracingContext[IO]): ProcessResult[IO] =
    execute(None, files, mainClass, dependencies, timeout)

  def run(
      code: String,
      mainClass: String,
      dependencies: List[ScalaDependency],
      timeout: FiniteDuration = 15 seconds
  )(implicit tracingContext: TracingContext[IO]): ProcessResult[IO] =
    execute(code, Nil, mainClass, dependencies, timeout)

  // TODO : handle deps
  // TODO : handle main class
  private def execute(
      code: Option[String],
      files: List[File],
      mainClass: String,
      dependencies: List[ScalaDependency],
      timeout: FiniteDuration
  )(implicit tracingContext: TracingContext[IO]): ProcessResult[IO] =
    EitherT(dockerPermits.withPermit {
      val runnerPath = config.runners.scala2.path.containerPath

      for {
        containerName <- IO(UUID.randomUUID())
        baseVolume = config.runners.scala2.path.toMap
        filesVolumes <- Docker.createVolumeNames[IO](files.map(_.getAbsolutePath), runnerPath + "/src")
        volumesFlag = Docker.volumeNamesToFlags(baseVolume ++ filesVolumes)
        containerNameFlag = s"--name $containerName"
        cpusFlag = s"--cpus ${config.defaultCpusLimit}"
        workDirFlag = s"-w $runnerPath"
        cmd = List(
          "docker",
          "run",
          cpusFlag,
          volumesFlag,
          containerNameFlag,
          "--rm",
          workDirFlag,
          config.runners.scala2.dockerImage,
          "./run.sh",
          code.getOrElse("")
        )
        killContainer = tracingContext.childSpan("Killing container") wrap IO {
          s"docker kill $containerName".!!
          ()
        }
        result <- tracingContext.childSpan("Docker run") wrap {
          StringProcessLogger.run(Process(cmd), killContainer)(ExecutionContexts.codeRunnerEc).value
        }
      } yield result
    })

  implicit private val cs: ContextShift[IO] =
    IO.contextShift(scala.concurrent.ExecutionContext.global)
  implicit private val timer: Timer[IO] =
    IO.timer(scala.concurrent.ExecutionContext.global)

}
