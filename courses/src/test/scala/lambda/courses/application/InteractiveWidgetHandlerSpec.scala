package lambda.courses.application

import org.scalatest._
import cats.Monad
import cats.effect._
import cats.data.EitherT
import java.io.File
import scala.concurrent.duration.FiniteDuration
import lambda.coderunner.infrastructure.SSPTemplateEngine
import lambda.coderunner.domain._
import lambda.courses.application.InteractiveWidgetHandler.WidgetHandlerContext
import lambda.courses.domain.widgets.InteractiveCodeWidget
import lambda.courses.domain.widgets.`package`.WidgetId
import lambda.coderunner.domain.Language._
import lambda.courses.domain.widgets._
import lambda.courses.domain.widgets.WidgetInput._
import lambda.courses.domain.widgets.WidgetOutput._
import lambda.courses.domain.widgets.WidgetError._

import scala.concurrent.ExecutionContext
import cats.effect.concurrent.Deferred
import lambda.coderunner.domain.TemplateEngine

class InteractiveWidgetHandlerSpec extends AsyncFunSpec with Matchers {

  describe("InteractiveWidgetHandler") {

    describe("Code Runner Widget") {

      it("Should return a valid result when the execution succeeds") {
        val output = "Output from code runner"
        implicit val context = mockContext(EitherT.rightT(output))
        InteractiveWidgetHandler[IO, IO.Par](
          codeWidget(Scala2),
          CodeInput("", Scala2)
        ).value.map(_.right.get shouldBe CodeOutput(output)).unsafeToFuture()
      }

      it("Should return an error when the execution fails") {
        val output = "Error from code runner"
        implicit val context = mockContext(EitherT.leftT(output))
        InteractiveWidgetHandler[IO, IO.Par](
          codeWidget(Scala2),
          CodeInput("", Scala2)
        ).value.map(_.left.get shouldBe CodeError(output)).unsafeToFuture()
      }

      describe("The language of the code does not match the expected language of the widget") {
        it("Should return an error if the language of the code is Scala and the widget expects Clojure") {
          implicit val context = mockContext()
          InteractiveWidgetHandler[IO, IO.Par](
            codeWidget(Clojure),
            CodeInput("", Scala2)
          ).value.map(result => result.left.get shouldBe WrongLanguageForWidget).unsafeToFuture()
        }

        it("Should return an error if the language of the code is Clojure and the widget expects Scala") {
          implicit val context = mockContext()
          InteractiveWidgetHandler[IO, IO.Par](
            codeWidget(Scala2),
            CodeInput("", Clojure)
          ).value.map(_.left.get shouldBe WrongLanguageForWidget).unsafeToFuture()
        }
      }

      it("Should pass the user input to the template engine") {
        (for {
          receivedUserInputDeferred <- Deferred[IO, String]
          userInput = "def toto(): Int = 42"
          ctx = mockContext().copy(templateEngine = new TemplateEngine[IO] {
            def canRender(file: File) = true
            def render(file: File, data: Map[String, Any]) = Resource.pure(file)
            override def render(
                files: List[File],
                data: Map[String, Any]
            )(implicit m: Monad[IO]): Resource[IO, List[File]] = {
              Resource
                .liftF(receivedUserInputDeferred.complete(data("userInput").asInstanceOf[String]))
                .map(_ => files)
            }
          })
          _ <- {
            implicit val context = ctx
            InteractiveWidgetHandler[IO, IO.Par](
              codeWidget(Scala2),
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
              ctx = WidgetHandlerContext(
                // We create a template engine that always return our mocked files
                templateEngine = mockTemplateEngine(mockedOutputFiles),
                // We assert that the code runner receives our mocked files as input
                scala2CodeRunner = new CodeRunner[IO, Scala2.type] {
                  def run(files: List[java.io.File], timeout: scala.concurrent.duration.FiniteDuration) =
                    EitherT.liftF(receivedFilesDeferred.complete(files)).map(_ => "")
                }
              )
              _ <- {
                implicit val context = ctx
                InteractiveWidgetHandler[IO, IO.Par](
                  codeWidget(Scala2),
                  CodeInput("", Scala2)
                ).value
              }
              receivedUserInput <- receivedFilesDeferred.get
            } yield receivedUserInput shouldBe mockedOutputFiles)
          }).unsafeToFuture()
        }

        it("Should return an error when the given CodeInput language is Clojure") {
          implicit val context = mockContext()
          InteractiveWidgetHandler[IO, IO.Par](
            codeWidget(Clojure),
            CodeInput("", Clojure)
          ).value.map(_.left.get shouldBe LanguageIsNotSupported).unsafeToFuture()
        }
      }
    }

    describe("Multiple Choices Widget") {
      it("Should return a success when the given answer is correct") {
        implicit val context = mockContext()
        InteractiveWidgetHandler[IO, IO.Par](
          multipleChoicesWidget(rightAnswerId = AnswerId(2)),
          input = AnswerId(2)
        ).value.map(_.right.get shouldBe RightAnswer).unsafeToFuture()
      }

      it("Should return an error when the given answer is wrong") {
        implicit val context = mockContext()
        InteractiveWidgetHandler[IO, IO.Par](
          multipleChoicesWidget(rightAnswerId = AnswerId(2)),
          input = AnswerId(3)
        ).value.map(_.left.get shouldBe WrongAnswer).unsafeToFuture()
      }
    }

    describe("The given input does not match the widget") {
      it("Should return an error when the widget is MultipleChoices and the input is Code") {
        implicit val context = mockContext()
        InteractiveWidgetHandler[IO, IO.Par](
          multipleChoicesWidget(AnswerId(1)),
          CodeInput("toto", Scala2)
        ).value.map(_.left.get shouldBe WrongInputForWidget).unsafeToFuture()
      }

      it("Should return an error when the widget is Code and the input is an AnswerId") {
        implicit val context = mockContext()
        InteractiveWidgetHandler[IO, IO.Par](
          codeWidget(Scala2),
          AnswerId(1)
        ).value.map(_.left.get shouldBe WrongInputForWidget).unsafeToFuture()
      }
    }
  }

  /**
    * Mocks
    */
  private def codeWidget(language: Language) = InteractiveCodeWidget(
    WidgetId("foo"),
    language,
    defaultValue = "",
    baseFiles = Nil,
    required = false
  )

  private def multipleChoicesWidget(
      rightAnswerId: AnswerId,
      question: String = "What is love ?",
      answers: List[MultipleChoices.Answer] = List(
        MultipleChoices.Answer(AnswerId(1), "Baby don't hurt me"),
        MultipleChoices.Answer(AnswerId(2), "Don't hurt me"),
        MultipleChoices.Answer(AnswerId(3), "No more")
      )
  ) = {
    val (rightAnswer, otherPropositions) = answers.partition(_.id == rightAnswerId)
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

  private def mockTemplateEngine(output: List[File]) = new TemplateEngine[IO] {
    def canRender(file: File) = true
    def render(file: File, data: Map[String, Any]) = Resource.pure(file)
    override def render(
        files: List[File],
        data: Map[String, Any]
    )(implicit m: Monad[IO]) = Resource.pure(output)
  }

  private def mockContext(
      scala2CodeRunnerResult: ProcessResult[IO] = EitherT.rightT("")
  ) = WidgetHandlerContext[IO](
    scala2CodeRunner = new ScalaCodeRunner[IO] {
      def run(
          files: List[File],
          mainClass: String,
          dependencies: List[ScalaCodeRunner.ScalaDependency],
          timeout: FiniteDuration
      ): lambda.coderunner.domain.ProcessResult[IO] = scala2CodeRunnerResult
    },
    templateEngine = new SSPTemplateEngine[IO]
  )

  /**
    * Test utils
    */
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

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
