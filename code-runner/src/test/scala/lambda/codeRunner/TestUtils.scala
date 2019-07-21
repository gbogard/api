package lambda.coderunner

import cats.implicits._
import cats.effect._
import org.scalatest._
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import com.github.writethemfirst.approvals.approvers.Approver
import lambda.core.Utils
import lambda.coderunner.domain.CodeRunner
import lambda.coderunner.domain.Language

object TestUtils {
  implicit private val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def testCodeRunner[L <: Language](
    codeRuner: CodeRunner[IO, L],
    resources: List[String],
    assertion: Either[String, String] => IO[Assertion],
    timeout: FiniteDuration = 30 seconds
  ): Future[Assertion] =
    resources.parTraverse(Utils.readResource[IO](_)).flatMap(files =>
      codeRuner.run(files, timeout).leftMap((normalizeEndings _) andThen (limitLines(_))).value
    ).flatMap(assertion).unsafeToFuture()

  // Useful to limit stack traces to first traces so we ramain agnostic of
  // the underlying JDK when we run tests across multiple platforms
  def limitLines(string: String, limit: Int = 10): String =
    string.split("\n").take(limit).mkString("\n")

  private def normalizeEndings(string: String) =
    string.replaceAll("\r\n", "\n").replaceAll("\r", "\n")

  trait Approbation extends org.scalatest.fixture.AsyncFunSpecLike with Matchers {
    override type FixtureParam = Approver
    protected val approvals = (new Approver).testing(getClass)
    override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
      val approver = approvals.writeTo(test.name)
      super.withFixture(test.toNoArgAsyncTest(approver))
    }
  }
}