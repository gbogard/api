package lambda.infrastructure.courses

import lambda.domain.courses.CourseRepository
import cats.effect.IO
import cats.data.OptionT
import lambda.domain.courses.widgets._

object LibraryCourseRepository extends CourseRepository[IO] {

  private val courses = lambda.library.courses
  private val widgets: List[Widget] = lambda.library.widgets

  def getCourse(id: lambda.domain.courses.Course.CourseId): OptionT[IO, lambda.domain.courses.Course] =
    OptionT.fromOption(courses.find(_.id == id))

  def getCourses(): cats.effect.IO[List[lambda.domain.courses.Course.CourseManifest]] =
    IO.pure(courses.map(_.manifest))

  def getWidget(
      id: lambda.domain.courses.widgets.WidgetId
  ): OptionT[IO, lambda.domain.courses.widgets.Widget] =
    OptionT.fromOption(widgets.find(_.id == id))

}
