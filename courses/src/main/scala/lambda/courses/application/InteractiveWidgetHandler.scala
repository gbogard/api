package lambda.courses.application

import cats.effect._
import cats.implicits._
import cats.Parallel
import cats.data.EitherT
import lambda.coderunner.domain._
import lambda.coderunner.domain.Language.Scala2
import lambda.courses.domain.widgets._
import lambda.courses.domain.widgets.WidgetInput._
import lambda.courses.domain.widgets.WidgetOutput._
import lambda.courses.domain.widgets.WidgetError._
import lambda.coderunner.application.SourceFileHandler
import java.io.File

object InteractiveWidgetHandler {

  type Result[F[_]] = EitherT[F, WidgetError, WidgetOutput]
  private type CodeRunnerFn[F[_]] = (List[File]) => CodeRunner.ProcessResult[F]

  case class WidgetHandlerContext[F[_]: Sync](
      scala2CodeRunner: CodeRunner[F, Scala2.type],
      templateEngine: TemplateEngine[F]
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
    case (w: MultipleChoices, i: AnswerId) => EitherT.fromEither(checkMultipleChoices(w, i))
    case (w: InteractiveCodeWidget, i: CodeInput) =>
      if (w.language == i.language) executeInteractiveCode(w, i)
      else EitherT.leftT(WrongLanguageForWidget)
    case _ => EitherT.leftT(WrongInputForWidget)
  }

  private def checkMultipleChoices(
      widget: MultipleChoices,
      input: AnswerId
  ): Either[WidgetError, WidgetOutput] =
    if (input == widget.question.rightAnswer.id) Right(RightAnswer) else Left(WrongAnswer)

  private def selectCodeRunner[F[_]: Sync](
      language: Language
  )(implicit ctx: WidgetHandlerContext[F]): EitherT[F, WidgetError, CodeRunnerFn[F]] =
    language match {
      case Scala2 => EitherT.rightT(ctx.scala2CodeRunner.run(_))
      case _      => EitherT.leftT(LanguageIsNotSupported)
    }

  private def executeInteractiveCode[F[_]: Sync, Par[_]](
      widget: InteractiveCodeWidget,
      input: CodeInput
  )(implicit parallel: Parallel[F, Par], ctx: WidgetHandlerContext[F]): Result[F] =
    selectCodeRunner(widget.language) flatMap { codeRunner =>
      EitherT {
        for {
          baseFiles <- widget.baseFiles.parTraverse(SourceFileHandler.toFile(_))
          (templateFiles, basicFiles) = baseFiles.partition(ctx.templateEngine.canRender)
          params = Map("userInput" -> input.code)
          output <- ctx.templateEngine.render(templateFiles, params) use { renderedTemplateFiles =>
            val filesToExecute = basicFiles ++ renderedTemplateFiles
            codeRunner(filesToExecute).map(CodeOutput(_)).leftMap(CodeError(_)).value
          }
        } yield output
      }
    }
}
