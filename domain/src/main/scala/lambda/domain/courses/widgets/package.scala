package lambda.domain.courses.widgets

import cats.data.EitherT

package object widgets {
  case class WidgetId(underlying: String) extends AnyVal

  trait Widget {
    val widgetType: String = this.getClass().getSimpleName().toLowerCase()
    def id: WidgetId
  }

  trait StaticWidget extends Widget

  trait InteractiveWidget[F[_], Input, OutputErr, OutputSuccess] extends Widget {
    def required: Boolean
    def execute(input: Input): EitherT[F, OutputErr, OutputSuccess]
  }
}