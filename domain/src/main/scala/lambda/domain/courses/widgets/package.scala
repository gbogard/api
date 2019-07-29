package lambda.domain.courses

import lambda.domain.courses.widgets.WidgetInput._
import lambda.domain.code.ScalaCodeRunner.ScalaDependency
import lambda.domain.code.SourceFile

package object widgets {
  case class MarkdownText(
      id: WidgetId,
      content: String
  ) extends StaticWidget {
    val widgetType: String = "markdownText"
  }
  case class MultipleChoices(
      id: WidgetId,
      required: Boolean,
      question: MultipleChoices.Question
  ) extends InteractiveWidget {
    val widgetType: String = "multipleChoices"
  }

  object MultipleChoices {

    case class Answer(id: AnswerId, value: String)
    case class Question(
        value: String,
        rightAnswer: Answer,
        otherPropositions: List[Answer]
    )
  }

  sealed trait InteractiveCodeWidget extends InteractiveWidget {
    def defaultValue: String
  }

  object InteractiveCodeWidget {
    case class Scala2CodeWidget(
        id: WidgetId,
        baseFiles: List[SourceFile],
        mainClass: String,
        defaultValue: String = "",
        dependencies: List[ScalaDependency] = Nil,
        required: Boolean = false
    ) extends InteractiveCodeWidget {
      val widgetType = "scala2CodeWidget"
    }
  }

  case class WidgetId(underlying: String) extends AnyVal

  sealed trait Widget {
    val widgetType: String
    def id: WidgetId
  }

  sealed trait StaticWidget extends Widget

  sealed trait InteractiveWidget extends Widget {
    def required: Boolean
  }

}
