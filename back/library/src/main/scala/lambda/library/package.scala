package lambda

import cats.Monad
import cats.implicits._
import lambda.domain.courses.Course
import lambda.domain.courses.Page._
import lambda.domain.courses.Widget

package object library {
  import cats.effect.Async
  import cats.Functor
  import cats.Traverse

  def courses[F[_]: Monad: CourseTemplateEngine: Async]: F[List[Course]] = {
    val data = Traverse[List].sequence(
      List(
        ATourOfScala.course[F],
        TamingCats.course[F]
      )
    )

    Async.memoize(data).flatten
  }

  def widgets[F[_]: Monad: CourseTemplateEngine: Async](): F[List[Widget]] =
    Async
      .memoize(
        Functor[F].map(courses[F])(_.flatMap(_.pages).flatMap {
          case p: SimplePage => p.widgets
          case p: CodePage   => p.widgets :+ p.code
        })
      )
      .flatten

}
