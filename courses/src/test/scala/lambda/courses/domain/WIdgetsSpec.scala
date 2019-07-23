package lambda.courses.domain

import org.scalatest._
import lambda.courses.domain.widgets._
import lambda.courses.domain.widgets.WidgetInput.AnswerId
import lambda.courses.domain.widgets.InteractiveCodeWidget._

class WidgetsSpec extends FunSpec with Matchers {

  describe("Widget type") {
    it("Should be 'scala2CodeWidget' for Scala2CodeWidget") {
      Scala2CodeWidget(
        WidgetId(""),
        Nil,
        "Main"
      ).widgetType shouldBe "interactiveCodeWidget"
    }

    it("Should be 'multipleChoices' for MultipleChoices") {
      MultipleChoices(
        WidgetId(""),
        required = false,
        question = MultipleChoices.Question(
          "What is love ?",
          MultipleChoices.Answer(AnswerId(0), "Baby don't hurt me"),
          Nil
        )
      ).widgetType shouldBe "multipleChoices"
    }
  }
}
