package lambda.coderunner.domain

import java.io.File
import cats.data.EitherT
import scala.concurrent.duration._

object CodeRunner {

  type ProcessResult[F[_]] = EitherT[F, String, String]
}

trait CodeRunner[F[_]] {
  def run(
      files: List[File],
      timeout: FiniteDuration = 30 seconds
  ): CodeRunner.ProcessResult[F]
}
