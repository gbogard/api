package lambda.dsl.courses

import lambda.domain.code.ScalaCodeRunner.ScalaDependency
import lambda.domain.code.SourceFile
import lambda.domain.courses.{MultipleChoices, SimpleScala2CodeWidget, TabbedScala2CodeWidget}

trait WidgetOps {
  implicit class MultipleChoicesOps(widget: MultipleChoices) {
    def required: MultipleChoices = widget.copy(required = true)
  }

  implicit class SimpleScala2Ops(widget: SimpleScala2CodeWidget) {
    def required: SimpleScala2CodeWidget = widget.copy(required = true)
    def withMainClass(mainClass: String): SimpleScala2CodeWidget = widget.copy(mainClass = mainClass)
    def withBaseFiles(files: SourceFile*): SimpleScala2CodeWidget = widget.copy(baseFiles = (widget.baseFiles ++ files).distinct)
    def withBaseFile(file: SourceFile): SimpleScala2CodeWidget = withBaseFiles(file)
    def withDependencies(deps: ScalaDependency*): SimpleScala2CodeWidget = widget.copy(dependencies = (widget.dependencies ++ deps).distinct)
    def withDependency(dep: ScalaDependency): SimpleScala2CodeWidget = withDependencies(dep)
    def withDefaultValue(value: String): SimpleScala2CodeWidget = widget.copy(defaultValue = value)
  }

  implicit class TabbedScala2Ops(widget: TabbedScala2CodeWidget) {
    def required: TabbedScala2CodeWidget = widget.copy(required = true)
    def withMainClass(mainClass: String): TabbedScala2CodeWidget = widget.copy(mainClass = mainClass)
    def withBaseFiles(files: SourceFile*): TabbedScala2CodeWidget = widget.copy(baseFiles = (widget.baseFiles ++ files).distinct)
    def withBaseFile(file: SourceFile): TabbedScala2CodeWidget = withBaseFiles(file)
    def withDependencies(deps: ScalaDependency*): TabbedScala2CodeWidget = widget.copy(dependencies = (widget.dependencies ++ deps).distinct)
    def withDependency(dep: ScalaDependency): TabbedScala2CodeWidget = withDependencies(dep)
    def withTabs(tabs: SourceFile*): TabbedScala2CodeWidget = widget.copy(tabs = (widget.tabs ++ tabs).distinct)
    def withTab(tab: SourceFile): TabbedScala2CodeWidget = withTabs(tab)
  }
}
