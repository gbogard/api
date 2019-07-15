package lambda.coderunner

import java.io.File
import cats.data.EitherT
import Utils.ProcessResult

trait CodeRunner[F[_]] {
  def run(files: List[File]): ProcessResult[F] 
}
