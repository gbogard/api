package lambda.coursetemplateengine

import lambda.domain.courses.widgets._
import io.circe.yaml.parser
import io.circe._
import cats.instances.list._
import cats.instances.either._
import cats.syntax.traverse._
import org.apache.commons.io.FileUtils
import java.nio.charset.StandardCharsets

object TemplateEngine {
  val widgetDelimiter = "----"

  def parse(template: String, idPrefix: String): Either[Error, List[Widget]] = {
    val widgets: List[Either[Error, Widget]] = template
      .split(widgetDelimiter)
      .map(_.trim)
      .zipWithIndex
      .map({
        case (substring, index) => {
          val id = WidgetId(s"$idPrefix--widget-$index")
          parser.parse(substring) match {
            case Right(json) if json.isObject => json.as[Widget](Decoders.widgetDecoder(id))
            case Right(json) if json.isString => Right(MarkdownText(id, substring))
            case Left(_) => Right(MarkdownText(id, substring))
          }
        }
      })
      .toList

    widgets.sequence
  }

  def parse(template: java.io.File, idPrefix: String): Either[Error, List[Widget]] =
    parse(FileUtils.readFileToString(template, StandardCharsets.UTF_8), idPrefix)

}
