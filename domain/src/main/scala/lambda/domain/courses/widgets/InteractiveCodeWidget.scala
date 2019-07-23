package lambda.domain.courses.widgets

import lambda.domain.code._
import lambda.domain.code.ScalaCodeRunner.ScalaDependency

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
  ) extends InteractiveCodeWidget
}
