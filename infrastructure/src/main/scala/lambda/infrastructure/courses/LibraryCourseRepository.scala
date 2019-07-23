package lambda.infrastructure.courses

import lambda.domain.courses.CourseRepository
import cats.effect.IO
import cats.data.OptionT

object LibraryCourseRepository extends CourseRepository[IO] {

  def getCourse(id: lambda.domain.courses.Course.CourseId): OptionT[IO, lambda.domain.courses.Course] = OptionT.none
  def getCourses(): cats.effect.IO[List[lambda.domain.courses.Course.CourseManifest]] = IO.pure(Nil)
  def getWidget(id: lambda.domain.courses.widgets.WidgetId): OptionT[IO, lambda.domain.courses.widgets.Widget] = OptionT.none

}