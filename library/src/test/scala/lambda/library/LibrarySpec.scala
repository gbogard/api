package lambda.library

import org.scalatest._
import lambda.domain.courses.widgets.MultipleChoices

class LibrarySpec extends FunSpec with Matchers {
  describe("Courses library") {
    it("Should have unique courses ids") {
      courses.map(_.id).distinct shouldEqual courses.map(_.id)
    } 

    it("Should have unique widgets ids") {
      widgets.map(_.id).distinct shouldEqual widgets.map(_.id)
    }

    it("Should have unique proposition ids for each Multiple choices widgets") {
      widgets.foreach({
        case MultipleChoices(_, _, question) =>
          val propositionsIds = (question.otherPropositions.map(_.id.answerId) :+ question.rightAnswer.id.answerId)
          propositionsIds.distinct shouldEqual propositionsIds
        case _ => succeed
      })
    }
  }
}