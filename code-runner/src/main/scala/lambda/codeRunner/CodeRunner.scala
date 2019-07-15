package lambda.codeRunner

import java.io.File
import cats.data.EitherT

trait CodeRunner[F[_]] {
  def run(files: List[File]): EitherT[F, String, String]
}
