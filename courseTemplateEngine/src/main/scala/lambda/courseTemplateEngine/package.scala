package lambda

package object courseTemplateEngine {
  def parse(file: java.io.File, widgetIdPrefix: String) = TemplateEngine.parse(file, widgetIdPrefix)
  def parse(file: String, widgetIdPrefix: String) = TemplateEngine.parse(file, widgetIdPrefix)
}
