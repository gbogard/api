package lambda.api.domain

sealed trait WidgetOutput

object WidgetOutput {
  case object RightAnswer extends WidgetOutput
  case class CodeOutput(output: String) extends WidgetOutput
}
