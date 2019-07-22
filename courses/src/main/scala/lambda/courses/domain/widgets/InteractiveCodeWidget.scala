package lambda.courses.domain.widgets

import lambda.coderunner.domain._

case class InteractiveCodeWidget(
    id: WidgetId,
    language: Language,
    defaultValue: String,
    baseFiles: List[SourceFile],
    required: Boolean
) extends InteractiveWidget
