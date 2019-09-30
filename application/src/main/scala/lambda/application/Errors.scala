package lambda.application

import lambda.domain.courses.WidgetId

sealed trait WidgetError extends Throwable

object WidgetError {
  case object WrongAnswer extends WidgetError
  case object WrongInputForWidget extends WidgetError
  case class WidgetNotFound(id: WidgetId) extends WidgetError
  case class WidgetNotInteractive(id: WidgetId) extends WidgetError
  case class CodeError(output: String) extends WidgetError
}