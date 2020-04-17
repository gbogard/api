package lambda.api.infrastructure

import cats.effect.IO
import lambda.domain.courses._

class CourseRepositoryInterpreter extends CourseRepository[IO] {

  private val courses = lambda.library.courses[IO]
  private val widgets: IO[List[Widget]] = lambda.library.widgets[IO]

  def getCourse(id: lambda.domain.courses.Course.CourseId): IO[Option[Course]] = courses.map(_.find(_.id == id))

  def getCourses(): cats.effect.IO[List[lambda.domain.courses.Course.CourseManifest]] =
    courses.map(_.map(_.manifest))

  def getCourseManifest(
    id: lambda.domain.courses.Course.CourseId
  ): IO[Option[lambda.domain.courses.Course.CourseManifest]] = getCourses().map(_.find(_.id == id))

  def getWidget(
      id: WidgetId
  ): IO[Option[Widget]] =
    widgets.map(_.find(_.id == id))

}
