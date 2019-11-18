package lambda.dsl.courses

import lambda.domain.courses.{Answer, MarkdownText, MultipleChoices, Question, SimpleScala2CodeWidget, TabbedScala2CodeWidget, WidgetId}

trait WidgetBuilders {
  def markdown(id: String, content: String): MarkdownText =
    MarkdownText(WidgetId(id), content)

  def mk(id: String, content: String): MarkdownText = markdown(id, content)

  def multipleChoices(id: String, question: String, rightAnswer: String, otherPropositions: String*): MultipleChoices = MultipleChoices(
    WidgetId(id),
    required = false,
    question = Question(
      question,
      rightAnswer = Answer(0, rightAnswer),
      otherPropositions = otherPropositions.zipWithIndex.map({
        case (p, i) => Answer(i + 1, p)
      }).toList
    )
  )

  def simpleScala(id: String): SimpleScala2CodeWidget = SimpleScala2CodeWidget(
    WidgetId(id),
    "Main",
    baseFiles = Nil,
    defaultValue = ""
  )

  def scala(id: String): SimpleScala2CodeWidget = simpleScala(id)

  def tabbedScaa(id: String): TabbedScala2CodeWidget = TabbedScala2CodeWidget(
    WidgetId(id),
    Nil,
    Nil,
    "Main",
  )
}
