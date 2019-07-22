package lambda.courses.application

import org.scalatest._
import lambda.coderunner.domain.CodeRunner
import cats.effect._
import lambda.courses.application.InteractiveWidgetHandler.WidgetHandlerContext
import java.io.File
import scala.concurrent.duration.FiniteDuration
import lambda.coderunner.infrastructure.SSPTemplateEngine
import lambda.coderunner.domain.Language
import cats.data.EitherT
import lambda.courses.domain.widgets.InteractiveCodeWidget
import lambda.courses.domain.widgets.`package`.WidgetId
import lambda.coderunner.domain.Language.Scala2
import lambda.courses.domain.widgets.WidgetInput.CodeInput
import scala.concurrent.ExecutionContext

class InteractiveWidgetHandlerSpec extends AsyncFunSpec {

  describe("InteractiveWidgetHandler") {

    describe("Code Runner Widget") {

      it("Should return a valid result when the execution succeeds") {
        val output = "Output from code runner"
        implicit val context = mockContext(EitherT.rightT(output))
        (InteractiveWidgetHandler[IO, IO.Par](codeWidget(Scala2), CodeInput("", Scala2)))
      }

      it("Should return an error if the language of the code does not match the language of the widget") {

      }

      it("Should return an error when the execution fails") {}
    }

    describe("Multiple Choices Widget") {
      it("Should return a success when the given answwer is correct") {}

      it("Should return an error when the given answer is wrong") {}
    }

    it("Should return an error when the given input does not match the widget") {}
  }

  private def codeWidget(language: Language) = InteractiveCodeWidget(
      WidgetId("foo"),
      language,
      defaultValue = "",
      baseFiles = Nil,
      required = false
    )

  private def mockCodeRunner[L <: Language](result: CodeRunner.ProcessResult[IO]) =
    new CodeRunner[IO, L] {
      def run(files: List[File], timeout: FiniteDuration) = result
    }

  private def mockContext(scala2CodeRunnerResult: CodeRunner.ProcessResult[IO]) = WidgetHandlerContext(
    scala2CodeRunner = mockCodeRunner(scala2CodeRunnerResult),
    templateEngine = new SSPTemplateEngine[IO]
  )

  implicit val cs = IO.contextShift(ExecutionContext.global)
  implicit val timer = IO.timer(ExecutionContext.global)
}
