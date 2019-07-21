package lambda.courses.domain

import cats.data.EitherT

package object widgets {
  case class WidgetId(underlying: String) extends AnyVal

  trait Widget {
    val widgetType: String = this.getClass().getSimpleName().toLowerCase()
    def id: WidgetId
  }

  trait StaticWidget extends Widget

  trait InteractiveWidget extends Widget {
    def required: Boolean
  }
}