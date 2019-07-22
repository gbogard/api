package lambda.courses.domain.widgets


import MultipleChoices._
import WidgetInput._

case class MultipleChoices(
    id: WidgetId,
    required: Boolean,
    question: Question
) extends InteractiveWidget

object MultipleChoices {

  case class Answer(id: AnswerId, value: String)
  case class Question(
      value: String,
      rightAnswer: Answer,
      otherPropositions: List[Answer]
  )
}
