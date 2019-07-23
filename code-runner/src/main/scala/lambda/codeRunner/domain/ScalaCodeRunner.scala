package lambda.coderunner.domain

import java.io.File
import scala.concurrent.duration._
import ScalaCodeRunner._

trait ScalaCodeRunner[F[_]] {
  def run(
      files: List[File],
      mainClass: String,
      dependencies: List[ScalaDependency] = Nil,
      timeout: FiniteDuration = 30 seconds
  ): ProcessResult[F]
}

object ScalaCodeRunner {
  case class ScalaDependency(
      org: String,
      name: String,
      version: String,
      scalaVersion: String = "2.12"
  )
}