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
import lambda.domain.code.Language._
import lambda.domain.code.Language
import lambda.domain.Media
import lambda.domain.MediaHandler

object Serialization {
  object Encoders {

    implicit val courseIdEncoder: Encoder[CourseId] = deriveUnwrappedEncoder
    implicit val pageIdEncoder: Encoder[PageId] = deriveUnwrappedEncoder
    implicit val widgetIdEncoder: Encoder[WidgetId] = deriveUnwrappedEncoder
    implicit val unwrappedAnswerIdEncoder: Encoder[AnswerId] = deriveUnwrappedEncoder

    implicit val interactiveCodeWidgetEncoder: ObjectEncoder[InteractiveCodeWidget] =
      ObjectEncoder.instance {
        case w: InteractiveCodeWidget.Scala2CodeWidget =>
          w.asJsonObject.remove("baseFiles").remove("mainClass")
      }

    implicit val widgetEncoder: Encoder[Widget] =
      Encoder.instance {
        case w: MultipleChoices => withType(w.widgetType, w)
        case w: InteractiveCodeWidget =>
          withType(w.widgetType, w).asObject.get.add("language", Json.fromString(w.language.id)).asJson
        case w: MarkdownText => withType(w.widgetType, w)
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

    implicit def mediaUrlEncoder(implicit mediaHandler: MediaHandler): Encoder[Media] = media => mediaHandler.toUrl(media).asJson 
    implicit def courseEncoder(implicit mediaHandler: MediaHandler): Encoder[Course] = course => {
      course.asJsonObject.add("image", course.image.fold(Json.Null)(mediaUrlEncoder.apply)).asJson
    }
    implicit def courseManifestEncoder(implicit mediaHandler: MediaHandler): Encoder[CourseManifest] = courseManifest => {
      courseManifest.asJsonObject.add("image", courseManifest.image.fold(Json.Null)(mediaUrlEncoder.apply)).asJson
    } 
  }

  object Decoders {

    implicit val courseIdDecoder: Decoder[CourseId] = deriveUnwrappedDecoder
    implicit val pageIdDecoder: Decoder[PageId] = deriveUnwrappedDecoder
    implicit val widgetIdDecoder: Decoder[WidgetId] = deriveUnwrappedDecoder
    implicit val unwrappedAnswerIdDecoder: Decoder[AnswerId] = deriveUnwrappedDecoder
    val answerIdDecoder: Decoder[AnswerId] = deriveDecoder[AnswerId]

    implicit val languageDecoder: Decoder[Language] = Decoder.decodeString.flatMap {
      case Scala2.id  => Decoder.const(Scala2)
      case Clojure.id => Decoder.const(Clojure)
      case _          => Decoder.failedWithMessage("Invalid language type")
    }

    implicit val widgetInputDecoder: Decoder[WidgetInput] = List[Decoder[WidgetInput]](
      answerIdDecoder.widen,
      Decoder[CodeInput].widen
    ).reduce(_ or _)
  }

  private def error(msg: String): Json = Json.obj("error" -> Json.fromString(msg))
  private def success(msg: String): Json = Json.obj("success" -> Json.fromString(msg))
  private def withType[A: ObjectEncoder](`type`: String, a: A): Json =
    a.asJsonObject.add("type", Json.fromString(`type`)).asJson
}
