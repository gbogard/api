package lambda.api.domain

import java.io.File

import cats.data.EitherT
import cats.effect._
import cats.implicits._
import com.colisweb.tracing.TracingContext
import lambda.api.domain.WidgetError._
import lambda.api.domain.WidgetInput._
import lambda.api.domain.WidgetOutput._
import lambda.domain.code._
import lambda.domain.courses.InteractiveCodeWidget.{SimpleScala2CodeWidget, TabbedScala2CodeWidget}
import lambda.domain.courses._
import lambda.api.commons._

class InteractiveWidgetsService[F[_]: Sync](
    implicit scala2CodeRunner: ScalaCodeRunner[F],
    templateEngine: CodeTemplateEngine[F],
    sourceFileHandler: SourceFileHandler[F]
) {
  import InteractiveWidgetsService._

  /**
    * Given an interactive widget and a widget input, tries to execute
    * the widget. Will return a left if the execution fails or if the given input does not
    * match the expected input for the given widget
    */
  def run(
      widget: InteractiveWidget,
      input: WidgetInput
  )(implicit tracingContext: TracingContext[F]): Result[F] = {
    val result: Result[F] = (widget, input) match {
      case (w: MultipleChoices, i: AnswerId) => EitherT.fromEither(checkMultipleChoices(w, i))
      case (w: SimpleScala2CodeWidget, i: SimpleCodeInput) =>
        val pr: ProcessResult[F] =
          renderTemplateFiles(w.baseFiles, i.code).useEither(scala2CodeRunner.runFiles(_, w.dependencies))
        processResultToWidgetResult(pr)
      case (w: TabbedScala2CodeWidget, i: TabbedCodeInput) =>
        val pr: ProcessResult[F] = (i.code.map(SourceFile.RawText(_)) ++ w.baseFiles)
          .traverse(sourceFileHandler.apply)
          .useEither(scala2CodeRunner.runFiles(_, w.dependencies))
        processResultToWidgetResult(pr)
      case _ => EitherT.leftT(WrongInputForWidget)
    }

    tracingContext
      .childSpan("Running interactive widget", Map("widgetId" -> widget.id.underlying))
      .useEither(_ => result)
  }

  private def checkMultipleChoices(
      widget: MultipleChoices,
      input: AnswerId
  ): Either[WidgetError, WidgetOutput] =
    if (input.answerId == widget.question.rightAnswer.id) Right(RightAnswer) else Left(WrongAnswer)

  private def renderTemplateFiles(
      files: List[SourceFile],
      userInput: String
  ): Resource[F, List[File]] = if (files.nonEmpty) {
    for {
      baseFiles <- files.traverse(sourceFileHandler(_))
      output <- templateEngine.render(baseFiles, Some(userInput))
    } yield output
  } else {
    sourceFileHandler(SourceFile.RawText(userInput)).map(List(_))
  }
}

object InteractiveWidgetsService {

  type Result[F[_]] = EitherT[F, WidgetError, WidgetOutput]

  def processResultToWidgetResult[F[_]: Sync](pr: ProcessResult[F]): Result[F] =
    EitherT {
      pr.value map {
        case Left(output)  => Left(CodeError(output))
        case Right(output) => Right(CodeOutput(output))
      }
    }
}
