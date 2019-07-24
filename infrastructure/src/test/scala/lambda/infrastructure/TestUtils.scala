package lambda.infrastructure

import org.scalatest._
import com.github.writethemfirst.approvals.approvers.Approver
import cats.effect.IO

object TestUtils {

  // Useful to limit stack traces to first traces so we ramain agnostic of
  // the underlying JDK when we run tests across multiple platforms
  def limitLines(string: String, limit: Int = 10): String =
    string.split("\n").take(limit).mkString("\n")

  def normalizeEndings(string: String) =
    string.replaceAll("\r\n", "\n").replaceAll("\r", "\n")

  def serializeResponse(response: org.http4s.Response[IO]): String = {
    val body = response.as[String].unsafeRunSync()
    s"""
    |Status: ${response.status.code}
    |Body: $body
    """.stripMargin.trim()
  }

  trait Approbation extends org.scalatest.fixture.AsyncFunSpecLike with Matchers {
    override type FixtureParam = Approver
    protected val approvals = (new Approver).testing(getClass)
    override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
      val approver = approvals.writeTo(test.name)
      super.withFixture(test.toNoArgAsyncTest(approver))
    }
  }
}
