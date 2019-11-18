package lambda.dsl.courses

import lambda.domain.Image
import lambda.domain.courses.Course
import lambda.domain.courses.Course.CourseId

trait CourseBuilders extends Ids {

  def course(
      id: String,
      title: String,
      description: String,
      tags: List[String],
      image: Image
  ): Course = Course(
    id = CourseId(id),
    title = title,
    description = description,
    tags = tags,
    image = Some(image),
    pages = Nil
  )

  def course(
      id: String,
      title: String,
      description: String
  ) = Course(
    id = CourseId(id),
    title = title,
    description = description,
    tags = Nil,
    image = None,
    pages = Nil
  )

  def course(id: String, title: String) = Course(
    id = CourseId(id),
    title = title,
    description = "",
    tags = Nil,
    image = None,
    pages = Nil
  )

  def course(title: String) = course(id = slug(title), title)
}
