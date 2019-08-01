package lambda.infrastructure.gateway

import cats.syntax.functor._
import io.circe._
import io.circe.generic.extras.semiauto._
import io.circe.generic.extras.auto._
import io.circe.generic.extras.defaults._
import io.circe.syntax._
import lambda.domain.courses._
import lambda.domain.courses.Page._
import lambda.domain.courses.widgets._
import lambda.domain.courses.Course._
import lambda.domain.courses.widgets.WidgetInput._
import lambda.domain.courses.widgets.WidgetOutput._
import lambda.domain.courses.widgets.WidgetError._

object Serialization {

  implicit val courseIdDecoder: Decoder[CourseId] = deriveUnwrappedDecoder
  implicit val courseIdEncoder: Encoder[CourseId] = deriveUnwrappedEncoder

  implicit val pageIdDecoder: Decoder[PageId] = deriveUnwrappedDecoder
  implicit val pageIdEncoder: Encoder[PageId] = deriveUnwrappedEncoder

  implicit val widgetIdDecoder: Decoder[WidgetId] = deriveUnwrappedDecoder
  implicit val widgetIdEncoder: Encoder[WidgetId] = deriveUnwrappedEncoder

  implicit val widgetEncoder: Encoder[Widget] =
    Encoder.instance {
      case w: MultipleChoices       => withType(w.widgetType, w)
      case w: InteractiveCodeWidget => withType(w.widgetType, w)
      case w: MarkdownText          => withType(w.widgetType, w)
    }

  implicit val widgetOutputEncoder: Encoder[WidgetOutput] = Encoder.instance {
    case o: CodeOutput => o.asJson
    case RightAnswer   => success("Right answer")
  }

  implicit val widgetErrorEncoder: Encoder[WidgetError] = Encoder.instance {
    case e: CodeError        => e.asJson
    case WrongInputForWidget => error("Wrong input for widget")
    case WrongAnswer         => error("Wrong answer")
  }

  implicit val PageEncoder: Encoder[Page] = Encoder.instance {
    case p: SimplePage => withType("simplePage", p)
    case p: CodePage   => withType("codePage", p)
  }

  implicit val courseEncoder: Encoder[Course] = deriveEncoder[Course]
  implicit val courseManifestEncoder: Encoder[CourseManifest] = deriveEncoder[CourseManifest]

  implicit val widgetInputDecoder: Decoder[WidgetInput] = List[Decoder[WidgetInput]](
    Decoder[AnswerId].widen,
    Decoder[CodeInput].widen
  ).reduce(_ or _)

  private def error(msg: String): Json = Json.obj("error" -> Json.fromString(msg))
  private def success(msg: String): Json = Json.obj("success" -> Json.fromString(msg))
  private def withType[A: ObjectEncoder](`type`: String, a: A): Json =
    a.asJsonObject.add("type", Json.fromString(`type`)).asJson
}
