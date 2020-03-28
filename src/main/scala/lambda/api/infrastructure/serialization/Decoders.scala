package lambda.api.infrastructure.serialization

import cats.implicits._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.generic.extras.semiauto.deriveUnwrappedDecoder
import lambda.api.domain.WidgetInput
import lambda.api.domain.WidgetInput._
import lambda.domain.code.Language
import lambda.domain.code.Language.Scala2
import lambda.domain.courses.Course.CourseId
import lambda.domain.courses.Page.PageId
import lambda.domain.courses.WidgetId

trait Decoders {

  implicit lazy val courseIdDecoder: Decoder[CourseId] = deriveUnwrappedDecoder
  implicit lazy val pageIdDecoder: Decoder[PageId] = deriveUnwrappedDecoder
  implicit lazy val widgetIdDecoder: Decoder[WidgetId] = deriveUnwrappedDecoder
  implicit lazy val unwrappedAnswerIdDecoder: Decoder[AnswerId] = deriveUnwrappedDecoder
  lazy val answerIdDecoder: Decoder[AnswerId] = deriveDecoder[AnswerId]

  implicit lazy val languageDecoder: Decoder[Language] = Decoder.decodeString.flatMap {
    case Scala2.id  => Decoder.const(Scala2)
    case _          => Decoder.failedWithMessage("Invalid language type")
  }

  implicit lazy val widgetInputDecoder: Decoder[WidgetInput] = List[Decoder[WidgetInput]](
    answerIdDecoder.widen,
    Decoder[SimpleCodeInput].widen,
    Decoder[TabbedCodeInput].widen
  ).reduce(_ or _)
}
