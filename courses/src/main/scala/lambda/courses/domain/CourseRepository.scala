package lambda.courses.domain

import lambda.courses.domain.Course._

trait CourseRepository[F[_]] {
  
  def getCourses(): F[List[CourseManifest]]

  def getCourse(id: CourseId): F[Course]

}