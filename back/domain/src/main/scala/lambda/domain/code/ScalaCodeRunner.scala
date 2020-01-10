package lambda.domain.code

import java.io.File
import scala.concurrent.duration._
import ScalaCodeRunner._
import com.colisweb.tracing.TracingContext

trait ScalaCodeRunner[F[_]] {
  def run(
      files: List[File],
      mainClass: String,
      dependencies: List[ScalaDependency] = Nil,
      timeout: FiniteDuration = 30 seconds
  )(implicit tracingContext: TracingContext[F]): ProcessResult[F]

  def run(
    code: String,
    mainClass: String,
    dependencies: List[ScalaDependency] = Nil,
    timeout: FiniteDuration = 30 seconds
  )(implicit tracingContext: TracingContext[F]): ProcessResult[F]
}

object ScalaCodeRunner {
  case class ScalaDependency(
      org: String,
      name: String,
      version: String,
      scalaVersion: String = "2.12"
  )
}