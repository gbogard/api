package lambda.api.infrastructure.serialization

import io.circe.generic.auto._
import io.circe.generic.extras.semiauto.deriveUnwrappedEncoder
import io.circe.syntax._
import io.circe.{Encoder, Json, JsonObject}
import lambda.api.domain.WidgetError._
import lambda.api.domain.WidgetInput._
import lambda.api.domain.WidgetOutput._
import lambda.api.domain.{WidgetError, WidgetOutput}
import lambda.domain.courses.Course.{CourseId, CourseManifest}
import lambda.domain.courses.InteractiveCodeWidget._
import lambda.domain.courses.Page.{CodePage, PageId, SimplePage}
import lambda.domain.courses._
import lambda.domain.{Media, MediaHandler}

trait Encoders {

  implicit val courseIdEncoder: Encoder[CourseId] = deriveUnwrappedEncoder
  implicit val pageIdEncoder: Encoder[PageId] = deriveUnwrappedEncoder
  implicit val widgetIdEncoder: Encoder[WidgetId] = deriveUnwrappedEncoder
  implicit val unwrappedAnswerIdEncoder: Encoder[AnswerId] = deriveUnwrappedEncoder

  implicit val interactiveCodeWidgetEncoder: Encoder.AsObject[InteractiveCodeWidget] =
    Encoder.AsObject.instance {
      case w: SimpleScala2CodeWidget =>
        w.asJsonObject.remove("baseFiles").remove("mainClass")
      case w: TabbedScala2CodeWidget => w.asJsonObject
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

  implicit val PageEncoder: Encoder[Page] = Encoder.instance {
    case p: SimplePage => withType("simplePage", p)
    case p: CodePage   => withType("codePage", p)
  }

  private val NOT_FOUND = "NOT_FOUND"
  private val BAD_REQUEST = "BAD_REQUEST"
  private val WRONG_ANSWER = "WRONG_ANSWER"

  implicit val widgetErrorEncoder: Encoder[WidgetError] = Encoder.instance {
    case e: CodeError                 => mergeObjects(e.asJsonObject, error(WRONG_ANSWER))
    case WrongInputForWidget          => error(BAD_REQUEST, "Wrong input for widget").asJson
    case WrongAnswer                  => error(WRONG_ANSWER, "Wrong answer").asJson
    case WidgetNotFound(WidgetId(id)) => error(NOT_FOUND, s"Widget with id $id does not exist").asJson
    case WidgetNotInteractive(WidgetId(id)) =>
      error(BAD_REQUEST, s"Widget with id $id is not interactive").asJson
  }

  private def error(
      code: String,
      msg: String = ""
  ): JsonObject = JsonObject("error" -> msg.asJson, "code" -> code.asJson)

  private def mergeObjects(a: JsonObject, b: JsonObject) = Json.fromFields(a.toIterable ++ b.toIterable)

  implicit def mediaUrlEncoder(implicit mediaHandler: MediaHandler): Encoder[Media] = media => mediaHandler.toUrl(media).asJson
  implicit def courseEncoder(implicit mediaHandler: MediaHandler): Encoder[Course] = course => {
    course.asJsonObject.add("image", course.image.fold(Json.Null)(mediaUrlEncoder.apply)).asJson
  }
  implicit def courseManifestEncoder(implicit mediaHandler: MediaHandler): Encoder[CourseManifest] = courseManifest => {
    courseManifest.asJsonObject.add("image", courseManifest.image.fold(Json.Null)(mediaUrlEncoder.apply)).asJson
  }

  private def success(msg: String): Json = Json.obj("success" -> Json.fromString(msg))
  private def withType[A: Encoder.AsObject](`type`: String, a: A): Json =
    a.asJsonObject.add("type", Json.fromString(`type`)).asJson
}
