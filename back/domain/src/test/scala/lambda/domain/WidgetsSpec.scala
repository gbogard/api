package lambda.domain.courses

import org.scalatest._
import lambda.domain.courses.InteractiveCodeWidget._

class WidgetsSpec extends FunSpec with Matchers {

  describe("Widget type") {
    it("Should be 'scala2Code' for Scala2CodeWidget") {
      Scala2CodeWidget(
        WidgetId(""),
        Nil,
        "Main"
      ).widgetType shouldBe "scala2Code"
    }

    it("Should be 'markdownText' for MarkdownText") {
      MarkdownText(
        WidgetId(""),
        ""
      ).widgetType shouldBe "markdownText"
    }

    it("Should be 'multipleChoices' for MultipleChoices") {
      MultipleChoices(
        WidgetId(""),
        required = false,
        question = MultipleChoices.Question(
          "What is love ?",
          MultipleChoices.Answer(0, "Baby don't hurt me"),
          Nil
        )
      ).widgetType shouldBe "multipleChoices"
    }
  }
}
