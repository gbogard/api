package lambda.coursetemplateengine.widgets
import lambda.domain.code.ScalaCodeRunner.ScalaDependency

case class ScalaCodeWidget(
  baseFiles: Option[List[String]],
  mainClass: Option[String],
  dependencies: Option[List[ScalaDependency]],
  required: Option[Boolean],
  defaultValue: String,
)