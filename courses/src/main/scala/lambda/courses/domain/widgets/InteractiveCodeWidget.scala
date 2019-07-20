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

case class InteractiveCodeWidget[F[_]: Sync, L <: Language](
    id: WidgetId,
    defaultValue: String,
    baseFiles: F[List[File]],
    required: Boolean
)(implicit templateEngine: TemplateEngine[F], codeRunner: CodeRunner[F, L])
    extends InteractiveWidget[F, String, String, String] {

  def execute(input: String): EitherT[F, String, String] = {
    EitherT {
      for {
        files <- baseFiles
        params = Map("userInput" -> input)
        output <- (templateEngine.render(files, params) use { renderedFiles =>
          codeRunner.run(renderedFiles).value
        })
      } yield output
    }
  }
}
