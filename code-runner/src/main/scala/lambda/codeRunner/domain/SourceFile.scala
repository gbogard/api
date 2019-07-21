package lambda.coderunner.domain

sealed trait  SourceFile

object SourceFile {
  case class ClasspathResource(name: String) extends SourceFile
}