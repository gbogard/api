package lambda.domain.courses

import lambda.domain.courses.Course._
import lambda.domain.courses.widgets.`package`.WidgetId
import lambda.domain.courses.widgets.`package`.Widget
import cats.data.OptionT

trait CourseRepository[F[_]] {

  def getCourses(): F[List[CourseManifest]]

  def getCourse(id: CourseId): OptionT[F, Course]

  def getWidget(id: WidgetId): OptionT[F, Widget]

}
