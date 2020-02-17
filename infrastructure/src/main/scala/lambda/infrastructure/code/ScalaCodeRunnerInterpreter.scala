package lambda.infrastructure.code

import java.io.File

import cats.effect._
import com.typesafe.scalalogging.StrictLogging
import lambda.domain.code.ScalaCodeRunner._
import lambda.domain.code.{ScalaCodeRunner, _}

class ScalaCodeRunnerInterpreter
    extends ScalaCodeRunner[IO]
    with StrictLogging {
  def runFiles(files: List[File], dependencies: List[ScalaDependency]): ProcessResult[IO] = ???
}
