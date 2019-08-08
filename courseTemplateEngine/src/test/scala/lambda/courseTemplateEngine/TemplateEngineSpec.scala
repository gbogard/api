package lambda.coursetemplateengine

import org.scalatest._
import lambda.domain.courses.widgets._
import lambda.domain.courses.widgets.WidgetInput.AnswerId
import io.circe.DecodingFailure
import lambda.domain.code.SourceFile
import lambda.domain.code.ScalaCodeRunner.ScalaDependency
import scala.io.Source

class TemplateEngineSpec extends FunSpec with Matchers {
  describe("Course template engine") {
    it("Should render a template with a multiple choices question") {
      template("template-1.md") shouldBe Right(
        List(
          MarkdownText(
            WidgetId("page-id--widget-0"),
            "Certainty determine at of arranging perceived situation or. Or wholly pretty county in oppose. Favour met itself wanted settle put garret twenty."
          ),
          MultipleChoices(
            WidgetId("page-id--widget-1"),
            required = false,
            MultipleChoices.Question(
              "What is love ?",
              MultipleChoices.Answer(AnswerId(0), "Baby don't hurt me"),
              List(
                MultipleChoices.Answer(AnswerId(1), "Don't hurt me"),
                MultipleChoices.Answer(AnswerId(2), "No more")
              )
            )
          )
        )
      )
    }

    it("Should fail when there's YAML that doesn't match any known widget type") {
      template("template-2.md") shouldBe Left(
        DecodingFailure("Invalid widget type", Nil)
      )
    }

    it("Should render a template with a scala code widget") {
      template("template-3.md") shouldBe Right(
        List(
          MarkdownText(
            WidgetId("page-id--widget-0"),
            "Toto"
          ),
          InteractiveCodeWidget.Scala2CodeWidget(
            WidgetId("page-id--widget-1"),
            List(SourceFile.ClasspathResource("toto")),
            "MyMainClass",
            """
            |object Foo {
            |  1 + 3
            |}
            """.stripMargin.trim,
            List(ScalaDependency("cats", "cats", "version", "2.12"))
          )
        )
      )
    }

    it("Should render this complex template properly") {
      println(template("template-4.md"))
      assert(template("template-4.md").isRight)
    }
  }

  private def template(name: String) = parse(Source.fromResource(name).getLines().mkString("\r\n"), "page-id")
}
