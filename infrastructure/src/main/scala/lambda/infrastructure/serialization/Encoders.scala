package lambda.infrastructure.serialization

import io.circe.{Encoder, Json, ObjectEncoder}
import io.circe.generic.extras.semiauto.deriveUnwrappedEncoder
import io.circe.syntax._
import io.circe.generic.extras.semiauto._
import io.circe.generic.extras.auto._
import io.circe.generic.extras.defaults._
import lambda.application.WidgetInput.AnswerId
import lambda.application.WidgetOutput
import lambda.application.WidgetOutput.{CodeOutput, RightAnswer}
import lambda.domain.{Media, MediaHandler}
import lambda.domain.courses.Course.{CourseId, CourseManifest}
import lambda.domain.courses.InteractiveCodeWidget.{SimpleScala2CodeWidget, TabbedScala2CodeWidget}
import lambda.domain.courses.Page.{CodePage, PageId, SimplePage}
import lambda.domain.courses.{Course, InteractiveCodeWidget, MarkdownText, MultipleChoices, Page, Widget, WidgetId}

trait Encoders {

  implicit val courseIdEncoder: Encoder[CourseId] = deriveUnwrappedEncoder
  implicit val pageIdEncoder: Encoder[PageId] = deriveUnwrappedEncoder
  implicit val widgetIdEncoder: Encoder[WidgetId] = deriveUnwrappedEncoder
  implicit val unwrappedAnswerIdEncoder: Encoder[AnswerId] = deriveUnwrappedEncoder

  implicit val interactiveCodeWidgetEncoder: ObjectEncoder[InteractiveCodeWidget] =
    ObjectEncoder.instance {
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

  implicit def mediaUrlEncoder(implicit mediaHandler: MediaHandler): Encoder[Media] = media => mediaHandler.toUrl(media).asJson
  implicit def courseEncoder(implicit mediaHandler: MediaHandler): Encoder[Course] = course => {
    course.asJsonObject.add("image", course.image.fold(Json.Null)(mediaUrlEncoder.apply)).asJson
  }
  implicit def courseManifestEncoder(implicit mediaHandler: MediaHandler): Encoder[CourseManifest] = courseManifest => {
    courseManifest.asJsonObject.add("image", courseManifest.image.fold(Json.Null)(mediaUrlEncoder.apply)).asJson
  }

  private def success(msg: String): Json = Json.obj("success" -> Json.fromString(msg))
  private def withType[A: ObjectEncoder](`type`: String, a: A): Json =
    a.asJsonObject.add("type", Json.fromString(`type`)).asJson
}
