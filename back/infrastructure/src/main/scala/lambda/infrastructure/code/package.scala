package lambda.infrastructure

import lambda.domain.code.SourceFileHandler
import cats.effect._
import lambda.domain.code.SourceFile._

package object code {
  def sourceFileHandler(implicit config: Configuration): SourceFileHandler[IO] = {
    case ClasspathResource(name) => Utils.readResource[IO](name)
    case RawText(content) => Utils.createTemporaryFile[IO](content)
  }
}
