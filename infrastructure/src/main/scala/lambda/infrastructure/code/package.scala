package lambda.infrastructure

import lambda.domain.code.SourceFileHandler
import cats.effect._
import lambda.domain.code.SourceFile._

package object code {
  val sourceFileHandler: SourceFileHandler[IO] = {
    case ClasspathResource(name) => Utils.readResource[IO](name)
  }
}
