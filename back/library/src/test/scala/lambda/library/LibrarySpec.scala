package lambda.library

import org.scalatest._
import lambda.domain.courses.MultipleChoices
import lambda.domain.courses.CourseTemplateEngine
import cats.effect.IO
import lambda.domain.courses.Widget

class LibrarySpec extends FunSpec with Matchers {

  implicit private val courseTemplateEngine = new CourseTemplateEngine[IO] {
    def parse(templateString: String, idPrefix: String): IO[List[Widget]] = IO.pure(Nil)
  }

  describe("Courses library") {
    it("Should have unique courses ids") {
      courses[IO].map(courses => courses.map(_.id).distinct shouldEqual courses.map(_.id)).unsafeRunSync()
    }

    it("Should have unique widgets ids") {
      widgets[IO].map(widgets => widgets.map(_.id).distinct shouldEqual widgets.map(_.id)).unsafeRunSync()
    }

    it("Should have unique proposition ids for each Multiple choices widgets") {
      widgets[IO]
        .map(
          widgets =>
            widgets.foreach({
              case MultipleChoices(_, _, question) =>
                val propositionsIds = (question.otherPropositions
                  .map(_.id) :+ question.rightAnswer.id)
                propositionsIds.distinct shouldEqual propositionsIds
              case _ => succeed
            })
        )
        .unsafeRunSync()
    }
  }
}
