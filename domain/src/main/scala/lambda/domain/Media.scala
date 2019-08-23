package lambda.domain

sealed trait Media
sealed trait Image extends Media

object Media {
  case class ClasspathResource(path: String) extends Image
}