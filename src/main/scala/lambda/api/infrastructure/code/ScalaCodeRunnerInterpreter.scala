package lambda.api.infrastructure.code

import java.io.File

import cats.effect._
import com.typesafe.scalalogging.StrictLogging
import lambda.domain.code.ScalaCodeRunner._
import lambda.domain.code.{ProcessResult, ScalaCodeRunner}
import lambda.runners.scala.client.ScalaRunnerClient
import lambda.runners.scala.messages.Dependency

class ScalaCodeRunnerInterpreter(client: ScalaRunnerClient) extends ScalaCodeRunner[IO] with StrictLogging {

  def runFiles(files: List[File], dependencies: List[ScalaDependency]): ProcessResult[IO] = {
    println(("runFile", files))
    lambda.programexecutor.toEitherT(
      client.runFiles(files, dependencies.map(d => Dependency(d.org, d.name, d.version)))
    )
  }

}
