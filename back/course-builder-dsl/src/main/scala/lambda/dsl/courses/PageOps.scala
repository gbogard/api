package lambda.dsl.courses

import lambda.domain.courses.Page.{CodePage, SimplePage}
import lambda.domain.courses.Widget

trait PageOps {
  implicit class SimplePageOps(page: SimplePage) {
    def withWidgets(widgets: Widget*): SimplePage = page.copy(widgets = page.widgets ++ widgets)
    def withWidget(widget: Widget): SimplePage = withWidgets(widget)
  }

  implicit class CodePageOps(page: CodePage) {
    def withWidgets(widgets: Widget*): CodePage = page.copy(widgets = page.widgets ++ widgets)
    def withWidget(widget: Widget): CodePage = withWidgets(widget)
  }
}
