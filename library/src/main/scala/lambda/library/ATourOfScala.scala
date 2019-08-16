package lambda.library

import lambda.domain.courses.Page.SimplePage
import lambda.domain.courses.Page.PageId
import lambda.domain.courses.Course
import lambda.domain.courses.Course.CourseId
import Utils._

object ATourOfScala {

  private val id: String = "a-tour-of-scala"

  val course = Course(
    CourseId(id),
    "A tour of Scala",
    "Scala is an expressive, statically typed programming language that is both functional and object oriented.",
    Nil,
    List(
      SimplePage(
        pageId(1),
        "What is Scala ?",
        lambda.courseTemplateEngine.parse(unsafeTextFromResource("a-tour-of-scala/1-intro/content.md"), s"$id-intro").right.get
      ),
      SimplePage(
        pageId(2),
        "Expressions",
        lambda.courseTemplateEngine.parse(unsafeTextFromResource("a-tour-of-scala/2-expressions/content.md"), s"$id-expressions").right.get
      )
    )
  )

  private def pageId(nb: Int): PageId = PageId(s"$id-page-$nb")
}
