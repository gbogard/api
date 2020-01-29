package lambda.domain.code

sealed trait  SourceFile

object SourceFile {
  case class ClasspathResource(name: String) extends SourceFile
  case class RawText(content: String) extends SourceFile
}