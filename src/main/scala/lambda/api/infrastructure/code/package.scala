package lambda.api.infrastructure

import lambda.domain.code.SourceFileHandler
import cats.effect._
import lambda.domain.code.SourceFile._

package object code {
  def sourceFileHandler(implicit config: Configuration): SourceFileHandler[IO] = {
    case ClasspathResource(name) => Utils.readResource[IO](name)
    case RawText(content, _) => Utils.createTemporaryFile[IO](content)
  }
}
