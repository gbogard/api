package lambda.domain.code

import java.io.File
import cats.effect.Resource

trait SourceFileHandler[F[_]] {
  def apply(file: SourceFile): Resource[F, File] 
}