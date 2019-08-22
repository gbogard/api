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
    """
    |Scala is a modern, expressive, statically typed programming language that is both functional and object oriented.
    |This course is aimed at people who already have some experience of programming and would like to learn the basics
    |of functional programming in Scala.
    |""".stripMargin.trim,
    Nil,
    List(
      SimplePage(
        pageId(1),
        "What is Scala ?",
        lambda.courseTemplateEngine.parse(unsafeTextFromResource("a-tour-of-scala/1-intro/content.md"), s"$id-intro").right.get
      ),
      /*SimplePage(
        pageId(2),
        "Installing Scala locally",
        lambda.courseTemplateEngine.parse(unsafeTextFromResource("a-tour-of-scala/2-writing-scala-locally/content.md"), s"$id-local-dev").right.get
      ),*/
      SimplePage(
        pageId(3),
        "Expressions",
        lambda.courseTemplateEngine.parse(unsafeTextFromResource("a-tour-of-scala/3-expressions/content.md"), s"$id-expressions").right.get
      )
    )
  )

  private def pageId(nb: Int): PageId = PageId(s"$id-page-$nb")
}
