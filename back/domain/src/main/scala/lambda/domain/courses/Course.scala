package lambda.domain.courses

import Course._
import lambda.domain.Image

case class Course(
    id: CourseId,
    title: String,
    description: String,
    tags: List[String],
    image: Option[Image] = None,
    pages: List[Page]
) {
  def manifest = CourseManifest(id, title, description, image, tags)
}

object Course {
  case class CourseId(underlying: String) extends AnyVal
  case class CourseManifest(
      id: CourseId,
      title: String,
      description: String,
      image: Option[Image],
      tags: List[String]
  )
}
