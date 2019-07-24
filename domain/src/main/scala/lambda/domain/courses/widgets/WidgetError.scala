package lambda.domain.courses.widgets

sealed trait WidgetError

object WidgetError {
  case object WrongInputForWidget extends WidgetError
  case object WrongAnswer extends WidgetError
  case class CodeError(reason: String) extends WidgetError
}