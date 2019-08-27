package lambda.infrastructure.courseTemplateEngine

import lambda.domain.courses._
import io.circe.yaml.parser
import io.circe._
import cats.instances.list._
import cats.syntax.traverse._
import cats.effect.IO

object CourseTemplateEngineInterpreter extends CourseTemplateEngine[IO] {
  val widgetDelimiter = "----"

  def parse(template: String, idPrefix: String): IO[List[Widget]] = {
    template
      .split(widgetDelimiter)
      .map(_.trim)
      .zipWithIndex
      .toList
      .traverse({
        case (substring, index) =>
          IO {
            val id = WidgetId(s"$idPrefix--widget-$index")
            parser.parse(substring) match {
              case Right(json) if json.isObject => json.as[Widget](Decoders.widgetDecoder(id)).right.get
              case Right(json) if json.isString => MarkdownText(id, substring)
              case Left(_)                      => MarkdownText(id, substring)
            }
          }
      })
  }
}
