package lambda.coderunner

import java.io.File
import cats.data.EitherT
import Utils.ProcessResult
import scala.concurrent.duration._

trait CodeRunner[F[_]] {
  def run(files: List[File], timeout: FiniteDuration = 30 seconds): ProcessResult[F] 
}
