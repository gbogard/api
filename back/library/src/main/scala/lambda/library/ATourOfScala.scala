package lambda.library

import lambda.domain.courses.Page.SimplePage
import lambda.domain.courses.Page.PageId
import lambda.domain.courses.Course
import lambda.domain.courses.Course.CourseId
import Utils._
import lambda.domain.Media
import lambda.domain.courses.CourseTemplateEngine
import cats.Monad
import cats.implicits._

object ATourOfScala {

  private val id: String = "a-tour-of-scala"

  def course[F[_]: Monad](implicit templateEngine: CourseTemplateEngine[F]): F[Course] =
    for {
      introContent <- templateEngine.parse(
        unsafeTextFromResource("a-tour-of-scala/1-intro/content.md"),
        s"$id-intro"
      )
      installingScalaLocallyContent <- templateEngine.parse(
        unsafeTextFromResource("a-tour-of-scala/2-installing-scala-locally/content.md"),
        s"$id-installing-scala-locally"
      )
      expressionsContent <- templateEngine.parse(
        unsafeTextFromResource("a-tour-of-scala/3-expressions/content.md"),
        s"$id-expressions"
      )
      moreOnEpxressionsContent <- templateEngine.parse(
        unsafeTextFromResource("a-tour-of-scala/4-more-on-expressions/content.md"),
        s"$id-more-on-expressions"
      )
    } yield
      Course(
        CourseId(id),
        "A tour of Scala",
        """
        |Scala is a modern, expressive, statically typed programming language that is both functional and object oriented.
        |This course is aimed at people who already have some experience of programming and would like to learn the basics
        |of functional programming in Scala.
        |
        """.stripMargin.trim,
        Nil,
        image = Some(Media.ClasspathResource("/public/a-tour-of-scala.png")),
        pages = List(
          SimplePage(
            pageId(1),
            "What is Scala ?",
            introContent
          ),
          SimplePage(
            pageId(2),
            "Installing Scala locally",
            installingScalaLocallyContent
          ),
          SimplePage(
            pageId(3),
            "Expressions",
            expressionsContent
          ),
          SimplePage(
            pageId(4),
            "More on expressions",
            moreOnEpxressionsContent
          )
        )
      )

  private def pageId(nb: Int): PageId = PageId(s"$id-page-$nb")
}
