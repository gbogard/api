package lambda.courses.domain.widgets

sealed trait WidgetOutput

object WidgetOutput {
  case object RightAnswer extends WidgetOutput
  case class CodeOutput(value: String) extends WidgetOutput
}
