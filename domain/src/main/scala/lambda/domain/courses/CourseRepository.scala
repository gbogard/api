package lambda.domain.courses

import lambda.domain.courses.Course._
import lambda.domain.courses.widgets.`package`.WidgetId
import lambda.domain.courses.widgets.`package`.Widget

trait CourseRepository[F[_]] {

  def getCourses(): F[List[CourseManifest]]

  def getCourse(id: CourseId): F[Course]

  def getWidget(id: WidgetId): F[Widget]

}
