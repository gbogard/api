package lambda.courses.domain

import org.scalatest._
import lambda.courses.domain.widgets._
import lambda.coderunner.domain.Language.Scala2
import lambda.courses.domain.widgets.WidgetInput.AnswerId

class WidgetsSpec extends FunSpec with Matchers {

  describe("Widget type") {
    it("Should be 'interactiveCodeWidget' for InteractiveCodeWidget") {
      InteractiveCodeWidget(
        WidgetId(""),
        language = Scala2,
        defaultValue = "",
        baseFiles = Nil,
        required = false
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
