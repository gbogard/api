package lambda.infrastructure.courses

import org.scalatest._
import lambda.library._
import lambda.domain.courses.Course.CourseId
import lambda.domain.courses._
import lambda.domain.courses.Page.SimplePage
import cats.effect.IO

class LibraryCourseRepositorySpec extends FunSpec with Matchers {
  describe("LibraryCourseRepository") {
    it("Should return course manifests from the library") {
      (for {
        result <- repo.getCourses()
        courses <- courses[IO]
      } yield result shouldBe courses.map(_.manifest))
        .unsafeRunSync()
    }

    it("Should return a course when it exists in the library") {
      (for {
        result <- repo.getCourse(CourseId("a-tour-of-scala")).value
        course <- ATourOfScala.course[IO]
      } yield result shouldBe Some(course))
      .unsafeRunSync()
    }

    it("Should return a None when the course does not exist") {
      repo.getCourse(CourseId("toto")).value.unsafeRunSync() shouldBe
        None
    }

    it("Should return a widget when it exists in the library") {
      (for {
        result <- repo.getWidget(WidgetId("a-tour-of-scala-intro--widget-0")).value
        course <- ATourOfScala.course[IO]
        widget = course.pages.head.asInstanceOf[SimplePage].widgets.head
      } yield result shouldBe Some(widget))
      repo.getWidget(WidgetId("a-tour-of-scala-intro--widget-0")).value.unsafeRunSync()
    }

    it("Should return a None when the widget does not exist") {
      repo.getWidget(WidgetId("toto")).value.unsafeRunSync() shouldBe
        None
    }
  }

  implicit val courseTemplateEngine = CourseTemplateEngineInterpreter
  private val repo = new LibraryCourseRepository
}
