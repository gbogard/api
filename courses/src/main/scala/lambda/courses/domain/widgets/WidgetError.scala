package lambda.courses.domain.widgets

sealed trait WidgetError

object WidgetError {
  case object WrongInputForWidget extends WidgetError
  case object WrongAnswer extends WidgetError
  case class CodeError(reason: String) extends WidgetError
  case object WrongLanguageForWidget extends WidgetError
  case object LanguageIsNotSupported extends WidgetError
}