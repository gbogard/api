package lambda.infrastructure.serialization

import cats.implicits._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.generic.extras.semiauto.deriveUnwrappedDecoder
import lambda.application.WidgetInput
import lambda.application.WidgetInput.{AnswerId, SimpleCodeInput, TabbedCodeInput}
import lambda.domain.code.Language
import lambda.domain.code.Language.Scala2
import lambda.domain.courses.Course.CourseId
import lambda.domain.courses.Page.PageId
import lambda.domain.courses.WidgetId

trait Decoders {

  implicit val courseIdDecoder: Decoder[CourseId] = deriveUnwrappedDecoder
  implicit val pageIdDecoder: Decoder[PageId] = deriveUnwrappedDecoder
  implicit val widgetIdDecoder: Decoder[WidgetId] = deriveUnwrappedDecoder
  implicit val unwrappedAnswerIdDecoder: Decoder[AnswerId] = deriveUnwrappedDecoder
  val answerIdDecoder: Decoder[AnswerId] = deriveDecoder[AnswerId]

  implicit val languageDecoder: Decoder[Language] = Decoder.decodeString.flatMap {
    case Scala2.id  => Decoder.const(Scala2)
    case _          => Decoder.failedWithMessage("Invalid language type")
  }

  implicit val widgetInputDecoder: Decoder[WidgetInput] = List[Decoder[WidgetInput]](
    answerIdDecoder.widen,
    Decoder[SimpleCodeInput].widen,
    Decoder[TabbedCodeInput].widen
  ).reduce(_ or _)
}
