package lambda.infrastructure

import org.scalatest._
import com.github.writethemfirst.approvals.approvers.Approver
import cats.effect.IO

object TestUtils {

  trait Approbation extends org.scalatest.fixture.AsyncFunSpecLike with Matchers {
    override type FixtureParam = Approver
    protected val approvals = (new Approver).testing(getClass)
    override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
      val approver = approvals.writeTo(test.name)
      super.withFixture(test.toNoArgAsyncTest(approver))
    }
  }
}
