package lambda.courses.domain.widgets

import cats.effect.Sync

import MultipleChoices._
import cats.data.EitherT

case class MultipleChoices[F[_]: Sync](
    id: WidgetId,
    required: Boolean,
    question: Question
) extends InteractiveWidget[F, AnswerId, WrongAnswer.type, RightAnswer.type] {

  def execute(input: AnswerId): EitherT[F, WrongAnswer.type, RightAnswer.type] =
    if (input == question.rightAnswer.id) EitherT.rightT(RightAnswer)
    else EitherT.leftT(WrongAnswer)
}

object MultipleChoices {

  case class AnswerId(underlying: Int) extends AnyVal
  case class Answer(id: AnswerId, value: String)
  case class Question(
      value: String,
      rightAnswer: Answer,
      otherPropositions: List[Answer]
  )
  case object WrongAnswer
  case object RightAnswer
}
