package lambda.dsl.courses

import lambda.domain.Media
import lambda.domain.code.SourceFile

trait ResourceBuilders {
  def mediaResource(resource: String) = Media.ClasspathResource(resource)
  def sourceFileResource(resource: String): SourceFile = SourceFile.ClasspathResource(resource)
  def code(rawCode: String): SourceFile = SourceFile.RawText(rawCode)
}
