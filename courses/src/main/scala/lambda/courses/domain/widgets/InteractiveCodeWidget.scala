package lambda.courses.domain.widgets

import cats.syntax.all._
import cats.effect.Sync
import lambda.coderunner.domain.CodeRunner
import cats.data.EitherT
import lambda.coderunner.domain.TemplateEngine
import java.io.File
import lambda.coderunner.domain.Language
import lambda.coderunner.domain.Language.Scala2
import cats.effect.IO
import lambda.coderunner.domain.SourceFile

case class InteractiveCodeWidget(
    id: WidgetId,
    language: Language,
    defaultValue: String,
    baseFiles: List[SourceFile],
    required: Boolean
) extends InteractiveWidget
