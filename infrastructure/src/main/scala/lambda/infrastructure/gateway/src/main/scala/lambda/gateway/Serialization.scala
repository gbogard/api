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

  implicit val codeWidgetEncoder: Encoder[InteractiveCodeWidget] = deriveEncoder
  implicit val multipleChoicesWidgetEncoder: Encoder[MultipleChoices] = deriveEncoder

  implicit val widgetEncoder: Encoder[Widget] = Encoder.instance {
    case w: MultipleChoices       => w.asJson
    case w: InteractiveCodeWidget => w.asJson
    case w: MarkdownText          => w.asJson
  }

  implicit val widgetOutputEncoder: Encoder[WidgetOutput] = Encoder.instance {
    case o: CodeOutput => o.asJson
    case RightAnswer   => success("Right answer")
  }

  implicit val widgetErrorEncoder: Encoder[WidgetError] = Encoder.instance {
    case e: CodeError           => e.asJson
    case WrongInputForWidget    => error("Wrong input for widget")
    case WrongAnswer            => error("Wrong answer")
    case WrongLanguageForWidget => error("Wrong language for widget")
    case LanguageIsNotSupported => error("Language is not supported")
  }

  implicit val courseEncoder: Encoder[Course] = deriveEncoder[Course]
  implicit val courseManifestEncoder: Encoder[CourseManifest] = deriveEncoder[CourseManifest]
  implicit val SimplePageEncoder: Encoder[SimplePage] = deriveEncoder[SimplePage]
  implicit val CodePageEncoder: Encoder[CodePage] = deriveEncoder[CodePage]

  implicit val widgetInputDecoder: Decoder[WidgetInput] = List[Decoder[WidgetInput]](
    Decoder[AnswerId].widen,
    Decoder[CodeInput].widen
  ).reduce(_ or _)

  private def error(msg: String): Json = Json.obj("error" -> Json.fromString(msg))
  private def success(msg: String): Json = Json.obj("success" -> Json.fromString(msg))
}
