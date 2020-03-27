package lambda.api.infrastructure

import cats.data.OptionT
import cats.effect.IO
import lambda.domain.courses._

class CourseRepositoryInterpreter extends CourseRepository[IO] {

  private val courses = lambda.library.courses[IO]
  private val widgets: IO[List[Widget]] = lambda.library.widgets[IO]

  def getCourse(id: lambda.domain.courses.Course.CourseId): OptionT[IO, lambda.domain.courses.Course] =
    OptionT(courses.map(_.find(_.id == id)))

  def getCourses(): cats.effect.IO[List[lambda.domain.courses.Course.CourseManifest]] =
    courses.map(_.map(_.manifest))

  def getWidget(
      id: WidgetId
  ): OptionT[IO, Widget] =
    OptionT(widgets.map(_.find(_.id == id)))

}
