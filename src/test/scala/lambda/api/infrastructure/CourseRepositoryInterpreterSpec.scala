package lambda.api.infrastructure

import cats.effect.IO
import lambda.domain.courses.Course.CourseId
import lambda.domain.courses.Page.SimplePage
import lambda.domain.courses._
import lambda.library._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers


class CourseRepositoryInterpreterSpec extends AnyFunSpec with Matchers {

  private val repo = new CourseRepositoryInterpreter

  describe("LibraryCourseRepository") {
    it("Should return course manifests from the library") {
      (for {
        result <- repo.getCourses()
        courses <- courses[IO]
      } yield result shouldBe courses.map(_.manifest))
        .unsafeRunSync()
    }

    it("Should return a course when it exists in the library") {
      val course = atasteofscala.apply()
      repo.getCourse(course.id).value.map(_ shouldBe Some(course)).unsafeRunSync()
    }

    it("Should return a None when the course does not exist") {
      repo.getCourse(CourseId("toto")).value.unsafeRunSync() shouldBe None
    }

    it("Should return a widget when it exists in the library") {
      val course = atasteofscala.apply()
      val widget = course.pages.head.asInstanceOf[SimplePage].widgets.head
      repo.getWidget(widget.id).value.map(_ shouldBe Some(widget)).unsafeRunSync()
    }

    it("Should return a None when the widget does not exist") {
      repo.getWidget(WidgetId("toto")).value.unsafeRunSync() shouldBe
        None
    }
  }

}
