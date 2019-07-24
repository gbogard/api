package lambda.domain.courses

import Course._
case class Course(
    id: CourseId,
    title: String,
    description: String,
    tags: List[String],
    pages: List[Page]
) {
  def manifest = CourseManifest(id, title, description, tags)
}

object Course {
  case class CourseId(underlying: String) extends AnyVal
  case class CourseManifest(
      id: CourseId,
      title: String,
      description: String,
      tags: List[String]
  )
}
