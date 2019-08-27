package lambda.library

import lambda.domain.courses.Course
import lambda.domain.courses.Course.CourseId
import cats.Monad

object TamingCats {

  private val id: String = "taming-cats"

  def course[F[_]: Monad] = Monad[F].pure(
    Course(
      CourseId(id),
      "Taming Cats",
      "",
      tags = Nil,
      pages = Nil
    )
  )

}
