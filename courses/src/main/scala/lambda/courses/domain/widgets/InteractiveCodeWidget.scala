package lambda.courses.domain.widgets

import lambda.coderunner.domain._
import lambda.coderunner.domain.ScalaCodeRunner.ScalaDependency
import lambda.coderunner.domain.Language.Scala2

sealed trait InteractiveCodeWidget extends InteractiveWidget {
  def defaultValue: String
  def language: Language
}

object InteractiveCodeWidget {
  case class Scala2CodeWidget(
      id: String,
      baseFiles: List[SourceFile],
      mainClass: String,
      dependencies: List[ScalaDependency] = Nil,
      required: Boolean = false
  ) extends InteractiveCodeWidget {
      val language = Scala2
  }
}
