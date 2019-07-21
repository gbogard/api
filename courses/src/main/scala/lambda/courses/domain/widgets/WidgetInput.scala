package lambda.courses.domain.widgets

import lambda.coderunner.domain.Language

sealed trait WidgetInput

object WidgetInput {
  case class CodeInput(code: String, language: Language) extends WidgetInput
  case class AnswerId(value: Int) extends WidgetInput
}