package lambda.infrastructure.courses

import org.scalatest._
import lambda.library._
import lambda.domain.courses.Course.CourseId
import lambda.domain.courses._
import lambda.domain.courses.Page.SimplePage
import cats.effect.IO
import lambda.infrastructure.CourseRepositoryInterpreter

class CourseRepositoryInterpreterSpec extends FunSpec with Matchers {

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
