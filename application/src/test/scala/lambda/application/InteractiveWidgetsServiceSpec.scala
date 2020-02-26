package lambda.application

import org.scalatest._
import cats.effect._
import cats.data.EitherT
import java.io.File

import scala.concurrent.duration.FiniteDuration
import lambda.domain.code._
import lambda.domain.courses._
import lambda.domain.code.Language._
import lambda.application.WidgetInput._
import lambda.application.WidgetOutput._
import lambda.application.WidgetError._

import scala.concurrent.ExecutionContext
import cats.effect.concurrent.Deferred
import lambda.domain.code.TemplateEngine
import lambda.domain.code.ScalaCodeRunner.ScalaDependency
import com.colisweb.tracing._
import lambda.domain.courses.InteractiveCodeWidget.SimpleScala2CodeWidget
import org.scalamock.scalatest.MockFactory

class InteractiveWidgetsServiceSpec extends AsyncFunSpec with Matchers with MockFactory {

  describe("InteractiveWidgetHandler") {

    describe("Code Runner Widget") {

      it("Should return a valid result when the execution succeeds") {
        val output = "Output from code runner"

        new InteractiveWidgetsService[IO]().run(
          scalaCodeWidget,
          SimpleCodeInput("", Scala2)
        ).value.map(_.right.get shouldBe CodeOutput(output)).unsafeToFuture()
      }

      it("Should return an error when the execution fails") {
        val output = "Error from code runner"
        new InteractiveWidgetsService[IO]().run(
          scalaCodeWidget,
          SimpleCodeInput("", Scala2)
        ).value.map(_.left.get shouldBe CodeError(output)).unsafeToFuture()
      }

      it("Should pass the user input to the template engine") {
        val userInput = "def toto(): Int = 42"

        new InteractiveWidgetsService[IO]().run(
          scalaCodeWidget,
          SimpleCodeInput(userInput, Scala2)
        )

        succeed
      }

      describe("The code gets passed to the appropriate code runner for execution") {
        it("Should send the rendered template files to the scala2 CodeRunner when the language is Scala2") {
          (createTempFiles() use { mockedOutputFiles =>
             IO(succeed)
          }).unsafeToFuture()
        }

      }
    }

    describe("Multiple Choices Widget") {
      it("Should return a success when the given answer is correct") {
        implicit val context = mockContext()
        InteractiveWidgetsService[IO, IO.Par](
          multipleChoicesWidget(rightAnswerId = AnswerId(2)),
          input = AnswerId(2)
        ).value.map(_.right.get shouldBe RightAnswer).unsafeToFuture()
      }

      it("Should return an error when the given answer is wrong") {
        implicit val context = mockContext()
        InteractiveWidgetsService[IO, IO.Par](
          multipleChoicesWidget(rightAnswerId = AnswerId(2)),
          input = AnswerId(3)
        ).value.map(_.left.get shouldBe WrongAnswer).unsafeToFuture()
      }
    }

    describe("The given input does not match the widget") {
      it("Should return an error when the widget is MultipleChoices and the input is Code") {
        implicit val context = mockContext()
        InteractiveWidgetsService[IO, IO.Par](
          multipleChoicesWidget(AnswerId(1)),
          CodeInput("toto", Scala2)
        ).value.map(_.left.get shouldBe WrongInputForWidget).unsafeToFuture()
      }

      it("Should return an error when the widget is Code and the input is an AnswerId") {
        implicit val context = mockContext()
        InteractiveWidgetHandler[IO, IO.Par](
          scalaCodeWidget,
          AnswerId(1)
        ).value.map(_.left.get shouldBe WrongInputForWidget).unsafeToFuture()
      }
    }
  }

  /**
    * Mocks
    */
  private def multipleChoicesWidget(
      rightAnswerId: AnswerId,
      question: String = "What is love ?",
      answers: List[MultipleChoices.Answer] = List(
        MultipleChoices.Answer(1, "Baby don't hurt me"),
        MultipleChoices.Answer(2, "Don't hurt me"),
        MultipleChoices.Answer(3, "No more")
      )
  ) = {
    val (rightAnswer, otherPropositions) = answers.partition(_.id == rightAnswerId.answerId)
    MultipleChoices(
      WidgetId("id"),
      required = false,
      MultipleChoices.Question(
        question,
        rightAnswer.head,
        otherPropositions
      )
    )
  }

  private def scalaCodeWidget = SimpleScala2CodeWidget(
    WidgetId("foo"),
    defaultValue = "",
    baseFiles = Nil,
    required = false
  )

  implicit private lazy val scalaCodeRunner: ScalaCodeRunner[IO] = stub[ScalaCodeRunner[IO]]
  implicit private lazy val templateEngine: TemplateEngine[IO] = stub[TemplateEngine[IO]]
  implicit private lazy val sourceFileHandler: SourceFileHandler[IO] = (_: SourceFile) => ???

  /**
    * Test utils
    */
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
  implicit private def tracingContext: TracingContext[IO] = NoOpTracingContext[IO]()

  private def createTempFiles(): Resource[IO, List[File]] = {
    def acquire = IO {
      (1 to 10)
        .map(
          _ =>
            File.createTempFile(
              java.util.UUID.randomUUID().toString(),
              ".out"
            )
        )
        .toList
    }
    def release(files: List[File]) = IO {
      files.foreach(_.delete())
    }
    Resource.make(acquire)(release)
  }
}
