package lambda.domain.courses

package object widgets {
  case class WidgetId(underlying: String) extends AnyVal

  trait Widget {
    val widgetType: String = {
      val className = this.getClass().getSimpleName()
      s"${className.head.toLower}${className.tail}"
    }
    def id: WidgetId
  }

  trait StaticWidget extends Widget

  trait InteractiveWidget extends Widget {
    def required: Boolean
  }
}