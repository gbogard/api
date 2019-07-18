package lambda.courses.domain.widgets

import cats.syntax.all._
import cats.effect.Sync
import lambda.coderunner.domain.CodeRunner
import cats.data.EitherT
import lambda.coderunner.domain.TemplateEngine
import java.io.File

case class InteractiveCodeWidget[F[_]: Sync](
    id: WidgetId,
    codeRunner: CodeRunner[F],
    defaultValue: String,
    baseFiles: F[List[File]],
    required: Boolean,
)(implicit templateEngine: TemplateEngine[F])
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
