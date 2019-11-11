package lambda.infrastructure.gateway

import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import lambda.application._
import lambda.application.WidgetError._
import lambda.domain.courses.WidgetId

object ErrorsEncoders {
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
}
