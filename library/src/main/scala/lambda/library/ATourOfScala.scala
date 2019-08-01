package lambda.library

import lambda.domain.courses.Page.SimplePage
import lambda.domain.courses.Page.PageId
import lambda.domain.courses.Course
import lambda.domain.courses.Course.CourseId
import lambda.domain.courses.widgets._
import lambda.domain.courses.widgets.MultipleChoices._
import lambda.domain.courses.widgets.InteractiveCodeWidget._
import lambda.domain.courses.widgets.WidgetInput.AnswerId

object ATourOfScala {

  private val id: String = "a-tour-of-scala"

  private val firstPage = SimplePage(
    pageId(1),
    "What is Scala ?",
    List(
      MarkdownText(
        widgetId(1, 1),
        """
        Hello there
        """
      ),
      MultipleChoices(
        widgetId(1, 2),
        required = true,
        Question(
          "What is Scala ?",
          Answer(
            AnswerId(1),
            "An object-oriented AND functional programming language"
          ),
          List(
            Answer(AnswerId(2), "A general-purpose, object-oriented language only"),
            Answer(AnswerId(3), "A functional programming language only"),
            Answer(AnswerId(3), "A low-level, system programming language")
          )
        )
      ),
      Scala2CodeWidget(
        widgetId(1, 3),
        baseFiles = Nil,
        mainClass = "Main"
      )
    )
  )

  val course = Course(
    CourseId(id),
    "A tour of Scala",
    "Scala is an expressive, statically typed programming language that is both functional and object oriented.",
    Nil,
    List(
      firstPage
    )
  )

  private def pageId(nb: Int): PageId = PageId(s"$id-page-$nb")
  private def widgetId(pageNb: Int, widgetNb: Int): WidgetId = WidgetId(s"$id-widget-$pageNb$widgetNb")
}
