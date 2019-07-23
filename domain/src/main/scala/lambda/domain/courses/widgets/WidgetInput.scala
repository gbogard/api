package lambda.domain.courses.widgets

import lambda.domain.code.Language

sealed trait WidgetInput

object WidgetInput {
  case class CodeInput(code: String, language: Language) extends WidgetInput
  case class AnswerId(answerId: Int) extends WidgetInput
}