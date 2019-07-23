package lambda.courses.domain

import lambda.courses.domain.Course._
import lambda.courses.domain.widgets.`package`.WidgetId
import lambda.courses.domain.widgets.`package`.Widget

trait CourseRepository[F[_]] {

  def getCourses(): F[List[CourseManifest]]

  def getCourse(id: CourseId): F[Course]

  def getWidget(id: WidgetId): F[Widget]

}
