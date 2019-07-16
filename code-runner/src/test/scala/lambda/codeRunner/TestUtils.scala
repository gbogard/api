package lambda.coderunner

import cats.implicits._
import cats.effect._
import org.scalatest._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object TestUtils {
  implicit private val cs = IO.contextShift(ExecutionContext.global)

  def testCodeRunner(
    codeRuner: CodeRunner[IO],
    resources: List[String],
    assertion: Either[String, String] => IO[Assertion]
  ): Future[Assertion] =
    resources.parTraverse(Utils.readResource[IO](_)).flatMap(files =>
      codeRuner.run(files).value
    ).flatMap(assertion).unsafeToFuture()
}