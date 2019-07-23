package lambda.application

import cats.effect._
import cats.implicits._
import cats.Parallel
import cats.data.EitherT
import lambda.domain.code._
import lambda.domain.code.Language.Scala2
import lambda.domain.courses.widgets._
import lambda.domain.courses.widgets.InteractiveCodeWidget._
import lambda.domain.courses.widgets.WidgetInput._
import lambda.domain.courses.widgets.WidgetOutput._
import lambda.domain.courses.widgets.WidgetError._
import lambda.domain.code.SourceFileHandler
import java.io.File

object InteractiveWidgetHandler {

  type Result[F[_]] = EitherT[F, WidgetError, WidgetOutput]

  case class WidgetHandlerContext[F[_]: Sync](
      scala2CodeRunner: ScalaCodeRunner[F],
      templateEngine: TemplateEngine[F],
      sourceFileHandler: SourceFileHandler[F]
  )

  /**
    * Given an interactive widget and a widget input, tries to execute
    * the widget. Will return a left if the execution fails or if the given input does not
    * match the expected input for the given widget
    */
  def apply[F[_]: Sync, Par[_]](
      widget: InteractiveWidget,
      input: WidgetInput
  )(
      implicit ctx: WidgetHandlerContext[F],
      par: Parallel[F, Par]
  ): Result[F] = (widget, input) match {
    case (w: MultipleChoices, i: AnswerId)        => EitherT.fromEither(checkMultipleChoices(w, i))
    case (w: InteractiveCodeWidget, i: CodeInput) => executeInteractiveCode(w, i)
    case _                                        => EitherT.leftT(WrongInputForWidget)
  }

  private def checkMultipleChoices(
      widget: MultipleChoices,
      input: AnswerId
  ): Either[WidgetError, WidgetOutput] =
    if (input == widget.question.rightAnswer.id) Right(RightAnswer) else Left(WrongAnswer)

  private def renderFiles[F[_]: Sync, Par[_]](
      files: List[SourceFile],
      userInput: String
  )(implicit parallel: Parallel[F, Par], ctx: WidgetHandlerContext[F]): Resource[F, List[File]] =
    for {
      baseFiles <- Resource.liftF(files.parTraverse(ctx.sourceFileHandler(_)))
      (templateFiles, basicFiles) = baseFiles.partition(ctx.templateEngine.canRender)
      params = Map("userInput" -> userInput)
      output <- ctx.templateEngine.render(templateFiles, params)
    } yield output

  private def executeInteractiveCode[F[_]: Sync, Par[_]](
      widget: InteractiveCodeWidget,
      input: CodeInput
  )(implicit parallel: Parallel[F, Par], ctx: WidgetHandlerContext[F]): Result[F] =
    widget match {
      case s: Scala2CodeWidget if input.language == Scala2 =>
        EitherT(renderFiles(s.baseFiles, input.code) use { renderedFiles =>
          processResultToWidgetResult(ctx.scala2CodeRunner.run(renderedFiles, s.mainClass, s.dependencies)).value
        })
    }

  def processResultToWidgetResult[F[_]: Sync](pr: ProcessResult[F]): Result[F] =
    EitherT {
      pr.value map {
        case Left(output)  => Left(CodeError(output))
        case Right(output) => Right(CodeOutput(output))
      }
    }
}
