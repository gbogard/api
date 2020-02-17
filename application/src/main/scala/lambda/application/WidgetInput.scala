package lambda.application

import lambda.domain.code.Language

sealed trait WidgetInput

object WidgetInput {
  case class SimpleCodeInput(code: String, language: Language) extends WidgetInput
  case class TabbedCodeInput(code: List[String], language: Language) extends WidgetInput
  case class AnswerId(answerId: Int) extends WidgetInput
}