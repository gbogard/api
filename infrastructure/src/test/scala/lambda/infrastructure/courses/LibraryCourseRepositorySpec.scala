package lambda.infrastructure.courses

import org.scalatest._
import lambda.library._
import lambda.domain.courses.Course.CourseId
import lambda.domain.courses.widgets._
import lambda.domain.courses.Page.SimplePage

class LibraryCourseRepositorySpec extends FunSpec with Matchers {
  describe("LibraryCourseRepository") {
    it("Should return course manifests from the library") {
      LibraryCourseRepository.getCourses().unsafeRunSync() shouldBe courses.map(_.manifest)
    }

    it("Should return a course when it exists in the library") {
      LibraryCourseRepository.getCourse(CourseId("a-tour-of-scala")).value.unsafeRunSync() shouldBe
        Some(ATourOfScala.course)
    }

    it("Should return a None when the course does not exist") {
      LibraryCourseRepository.getCourse(CourseId("toto")).value.unsafeRunSync() shouldBe
        None
    }

    it("Should return a widget when it exists in the library") {
      LibraryCourseRepository.getWidget(WidgetId("a-tour-of-scala-widget-11")).value.unsafeRunSync() shouldBe
        Some(ATourOfScala.course.pages.head.asInstanceOf[SimplePage].widgets.head)
    }

    it("Should return a None when the widget does not exist") {
      LibraryCourseRepository.getWidget(WidgetId("toto")).value.unsafeRunSync() shouldBe
        None
    }

  }
}
