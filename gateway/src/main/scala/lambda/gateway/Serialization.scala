package lambda.gateway

import cats.syntax.functor._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import io.circe.syntax._
import lambda.courses.domain._
import lambda.courses.domain.Page._
import lambda.courses.domain.widgets._
import lambda.courses.domain.Course.CourseManifest
import lambda.courses.domain.widgets.WidgetInput._
import lambda.courses.domain.widgets.WidgetOutput._
import lambda.courses.domain.widgets.WidgetError._

object Serialization {

  implicit val codeWidgetEncoder: Encoder[InteractiveCodeWidget] = deriveEncoder
  implicit val multipleChoicesWidgetEncoder: Encoder[MultipleChoices] = deriveEncoder

  implicit val widgetEncoder: Encoder[Widget] = Encoder.instance {
    case w: MultipleChoices       => w.asJson
    case w: InteractiveCodeWidget => w.asJson
  }

  implicit val widgetOutputEncoder: Encoder[WidgetOutput] = Encoder.instance {
    case o: CodeOutput => o.asJson
    case RightAnswer   => RightAnswer.asJson
  }

  implicit val widgetErrorEncoder: Encoder[WidgetError] = Encoder.instance {
    case e: CodeError           => e.asJson
    case WrongInputForWidget    => WrongInputForWidget.asJson
    case WrongAnswer            => WrongAnswer.asJson
    case WrongLanguageForWidget => WrongLanguageForWidget.asJson
    case LanguageIsNotSupported => LanguageIsNotSupported.asJson
  }

  implicit val courseEncoder: Encoder[Course] = deriveEncoder[Course]
  implicit val courseManifestEncoder: Encoder[CourseManifest] = deriveEncoder[CourseManifest]
  implicit val SimplePageEncoder: Encoder[SimplePage] = deriveEncoder[SimplePage]
  implicit val CodePageEncoder: Encoder[CodePage] = deriveEncoder[CodePage]

  implicit val widgetInputDecoder: Decoder[WidgetInput] = List[Decoder[WidgetInput]](
    Decoder[AnswerId].widen,
    Decoder[CodeInput].widen
  ).reduce(_ or _)
}
