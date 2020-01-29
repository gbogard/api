package lambda.application

import cats.effect._
import cats.implicits._
import cats.data.EitherT
import lambda.domain.code._
import lambda.domain.code.Language.Scala2
import lambda.domain.courses._
import lambda.application.WidgetInput._
import lambda.application.WidgetOutput._
import lambda.application.WidgetError._
import lambda.domain.code.SourceFileHandler
import java.io.File

import com.colisweb.tracing.TracingContext
import lambda.domain.code.SourceFile.RawText

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
      tracingContext: TracingContext[F]
  ): Result[F] = (widget, input) match {
    case (w: MultipleChoices, i: AnswerId)              => EitherT.fromEither(checkMultipleChoices(w, i))
    case (w: InteractiveCodeWidget, i: SimpleCodeInput) => executeSimpleCode(w, i)
    case (w: InteractiveCodeWidget, i: TabbedCodeInput) => executeTabbedCode(w, i)
    case _                                              => EitherT.leftT(WrongInputForWidget)
  }

  private def checkMultipleChoices(
      widget: MultipleChoices,
      input: AnswerId
  ): Either[WidgetError, WidgetOutput] =
    if (input.answerId == widget.question.rightAnswer.id) Right(RightAnswer) else Left(WrongAnswer)

  private def executeSimpleCode[F[_]: Sync, Par[_]](
      widget: InteractiveCodeWidget,
      input: SimpleCodeInput
  )(implicit ctx: WidgetHandlerContext[F], tracingContext: TracingContext[F]): Result[F] =
    widget match {
      case s: SimpleScala2CodeWidget if input.language == Scala2 =>
        EitherT(renderTemplateFiles(s.baseFiles, input.code) use { renderedFiles =>
          processResultToWidgetResult(ctx.scala2CodeRunner.run(renderedFiles, s.mainClass, s.dependencies)).value
      case s: TabbedScala2CodeWidget if input.language == Scala2 =>
        val files = s.baseFiles.traverse(ctx.sourceFileHandler(_))
        EitherT(files use { f =>
          processResultToWidgetResult(ctx.scala2CodeRunner.run(f, s.mainClass, s.dependencies)).value
        })
    }

  private def executeTabbedCode[F[_]: Sync, Par[_]](
      widget: InteractiveCodeWidget,
      input: TabbedCodeInput
  )(implicit ctx: WidgetHandlerContext[F], tracingContext: TracingContext[F]): Result[F] =
    widget match {
      case s: TabbedScala2CodeWidget if input.language == Scala2 =>
        val files = (s.baseFiles ++ input.code.map(RawText)).traverse(ctx.sourceFileHandler(_))
        EitherT(files use { f =>
          processResultToWidgetResult(ctx.scala2CodeRunner.run(f, s.mainClass, s.dependencies)).value
        })
    }

  def processResultToWidgetResult[F[_]: Sync](pr: ProcessResult[F]): Result[F] =
    EitherT {
      pr.value map {
        case Left(output)  => Left(CodeError(output))
        case Right(output) => Right(CodeOutput(output))
      }
    }

  private def renderTemplateFiles[F[_]: Sync, Par[_]](
      files: List[SourceFile],
      userInput: String
  )(implicit ctx: WidgetHandlerContext[F]): Resource[F, List[File]] =
    for {
      baseFiles <- files.traverse(ctx.sourceFileHandler(_))
      (templateFiles, _) = baseFiles.partition(ctx.templateEngine.canRender)
      params = Map("userInput" -> userInput)
      output <- ctx.templateEngine.render(templateFiles, params)
    } yield output
}
