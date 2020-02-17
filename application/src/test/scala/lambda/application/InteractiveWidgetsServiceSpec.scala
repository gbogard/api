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

class InteractiveWidgetsServiceSpec extends AsyncFunSpec with Matchers {

  describe("InteractiveWidgetHandler") {

    describe("Code Runner Widget") {

      it("Should return a valid result when the execution succeeds") {
        val output = "Output from code runner"

        (new InteractiveWidgetsService[IO]()).run(
          scalaCodeWidget,
          SimpleCodeInput("", Scala2)
        ).value.map(_.right.get shouldBe CodeOutput(output)).unsafeToFuture()
      }

      it("Should return an error when the execution fails") {
        val output = "Error from code runner"
        implicit val context = mockContext(EitherT.leftT(output))
        InteractiveWidgetHandler[IO, IO.Par](
          scalaCodeWidget,
          CodeInput("", Scala2)
        ).value.map(_.left.get shouldBe CodeError(output)).unsafeToFuture()
      }

      it("Should pass the user input to the template engine") {
        (for {
          receivedUserInputDeferred <- Deferred[IO, String]
          userInput = "def toto(): Int = 42"
          ctx = mockContext().copy(templateEngine = new TemplateEngine[IO] {
            def canRender(file: File) = true
            def render(
                files: List[File],
                data: Map[String, Any]
            ): Resource[IO, List[File]] = {
              Resource
                .liftF(receivedUserInputDeferred.complete(data("userInput").asInstanceOf[String]))
                .map(_ => files)
            }
          })
          _ <- {
            implicit val context = ctx
            InteractiveWidgetHandler[IO, IO.Par](
              scalaCodeWidget,
              CodeInput(userInput, Scala2)
            ).value
          }
          receivedUserInput <- receivedUserInputDeferred.get
        } yield receivedUserInput shouldBe userInput).unsafeToFuture()
      }

      describe("The code gets passed to the appropriate code runner for execution") {
        it("Should send the rendered template files to the scala2 CodeRunner when the language is Scala2") {
          (createTempFiles() use { mockedOutputFiles =>
            (for {
              receivedFilesDeferred <- Deferred[IO, List[File]]
              ctx = WidgetHandlerContext[IO](
                // We create a template engine that always return our mocked files
                templateEngine = mockTemplateEngine(mockedOutputFiles),
                // We assert that the code runner receives our mocked files as input
                scala2CodeRunner = new ScalaCodeRunner[IO] {
                  def run(
                      files: List[java.io.File],
                      mainClass: String,
                      dependencies: List[ScalaDependency],
                      timeout: scala.concurrent.duration.FiniteDuration
                  )(implicit tracingContext: TracingContext[IO]) =
                    EitherT.liftF(receivedFilesDeferred.complete(files)).map(_ => "")
                },
                sourceFileHandler = (_: SourceFile) => ???
              )
              _ <- {
                implicit val context = ctx
                InteractiveWidgetHandler[IO, IO.Par](
                  scalaCodeWidget,
                  CodeInput("", Scala2)
                ).value
              }
              receivedUserInput <- receivedFilesDeferred.get
            } yield receivedUserInput shouldBe mockedOutputFiles)
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
  private def scalaCodeWidget = Scala2CodeWidget(
    WidgetId("foo"),
    mainClass = "Main",
    defaultValue = "",
    baseFiles = Nil,
    required = false
  )

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


  private def mockScalaCodeRunner(result: ProcessResult[IO]) = new ScalaCodeRunner[IO] {
    def run(
      files: List[File],
      mainClass: String,
      dependencies: List[ScalaCodeRunner.ScalaDependency],
      timeout: FiniteDuration
    )(implicit tracingContext: TracingContext[IO]): ProcessResult[IO] = result
  }

  private def mockTemplateEngine(output: List[File]) = new TemplateEngine[IO] {
    def canRender(file: File) = true
    def render(
        files: List[File],
        data: Map[String, Any]
    ) = Resource.pure(output)
  }

  private def mockSourceFileHandler: SourceFileHandler[IO] = (_: SourceFile) => ???

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
