package lambda.domain.code

import java.io.File

trait SourceFileHandler[F[_]] {
  def apply(file: SourceFile): F[File]
}