package lambda.gateway

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.generic.semiauto._
import lambda.courses.domain._
import lambda.courses.domain.Page._
import lambda.courses.domain.widgets._

object Serialization {

  implicit val codeWidgetEncoder: Encoder[InteractiveCodeWidget] = deriveEncoder
  implicit val multipleChoicesWidgetEncoder: Encoder[MultipleChoices] = deriveEncoder

  implicit val widgetEncoder: Encoder[Widget] = Encoder.instance {
    case w: MultipleChoices => w.asJson
    case w: InteractiveCodeWidget => w.asJson
  }

  implicit val courseEncoder: Encoder[Course] = deriveEncoder[Course]
  implicit val SimplePageEncoder: Encoder[SimplePage] = deriveEncoder[SimplePage] 
  implicit val CodePageEncoder: Encoder[CodePage] = deriveEncoder[CodePage]
}