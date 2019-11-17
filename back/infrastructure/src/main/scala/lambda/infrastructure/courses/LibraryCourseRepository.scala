package lambda.infrastructure.courses

import lambda.domain.courses.CourseRepository
import cats.effect.IO
import cats.data.OptionT
import lambda.domain.courses._

class LibraryCourseRepository(implicit cte: CourseTemplateEngine[IO]) extends CourseRepository[IO] {

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
