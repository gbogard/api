package lambda.coderunner.application

import cats.effect.Sync
import lambda.coderunner.domain.SourceFile
import lambda.core.Utils
import lambda.coderunner.domain.SourceFile.ClasspathResource
import java.io.File

object SourceFileHandler {

  def toFile[F[_]: Sync](sourceFile: SourceFile): F[File] = sourceFile match {
    case ClasspathResource(name) => Utils.readResource(name)
  }
}