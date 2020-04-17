package lambda.api.application

import java.util.UUID

import cats.data.EitherT
import cats.effect.IO
import com.colisweb.tracing._
import io.circe._
import io.circe.syntax._
import lambda.api.domain.{CoursesService, InteractiveWidgetsService, WidgetInput}
import lambda.api.domain.WidgetError._
import lambda.api.domain.WidgetOutput.RightAnswer
import lambda.api.infrastructure.serialization._
import lambda.domain.MediaHandler
import lambda.domain.courses._
import org.http4s._
import org.scalamock.scalatest._
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers

class CoursesControllerSpec extends AsyncFunSpec with Matchers with AsyncMockFactory {

  describe("Course service") {
    describe("GET /courses") {
      it("Should return a list of course manifests when the repository has courses") {
        val expectedCourses = lambda.library.courses[IO].unsafeRunSync().map(_.manifest)

        (coursesService.getCoursesManifests _).when().returns(IO.pure(expectedCourses))
        (mediaHandler.toUrl _).when(*).returns("")

        val request = Request[IO](uri = Uri.unsafeFromString("/courses"))
        controller
          .run(request)
          .value
          .flatMap(_.get.as[Json])
          .map(body => {
            body shouldBe expectedCourses.asJson
          })
          .unsafeToFuture()
      }

      it("Should return an empty list when the repository has no course") {

        (coursesService.getCoursesManifests _).when().returns(IO.pure(Nil))

        val request = Request[IO](uri = Uri.unsafeFromString("/courses"))
        controller.run(request)
          .value
          .flatMap(_.get.as[Json])
          .map(body => {
            body shouldBe Json.arr()
          })
          .unsafeToFuture()
      }
    }

    describe("GET /courses/id") {


      it("Should return the matching course if any") {

        val course = lambda.library.courses[IO].unsafeRunSync().head

        (coursesService.getCourseById _).when(course.id).returns(IO.pure(Some(course)))
        (mediaHandler.toUrl _).when(*).returns("")

        val request = Request[IO](uri = Uri.unsafeFromString(s"/courses/${course.id.underlying}"))
        controller.run(request)
          .value
          .flatMap(_.get.as[Json])
          .map(body => {
            body shouldBe course.asJson
          })
          .unsafeToFuture()
      }

      it("Should return a 404 when no course is found") {

        (coursesService.getCourseById _).when(*).returns(IO.pure(None))

        val request = Request[IO](uri = Uri.unsafeFromString(s"/courses/toto"))
        controller.run(request)
          .value
          .map(_.get.status.code shouldBe 404)
          .unsafeToFuture()
      }
    }

    describe("POST /checkWidget/id") {

      it("Should return a 400 if the input is not a valid widget input") {

        val request = Request[IO](method = Method.POST, uri = Uri.unsafeFromString("/checkWidget/toto"))

        controller.run(request)
          .value
          .map(_.get.status.code shouldBe 400)
          .unsafeToFuture()
      }

      it("Should return a 404 when the widget does not exist") {

       val id = WidgetId(UUID.randomUUID().toString)
        (coursesService.checkWidget(_: WidgetId, _: WidgetInput)(_: TracingContext[IO]))
          .when(id, *, *).returns(EitherT.leftT(WidgetNotFound(id)))

        val request = Request[IO](method = Method.POST, uri = Uri.unsafeFromString(s"/checkWidget/${id.underlying}"))
          .withEntity(Json.obj("answerId" -> Json.fromInt(2)))

        controller.run(request)
          .value
          .map(_.get.status.code shouldBe 404)
          .unsafeToFuture()
      }


      it("Should return a 400 when the input is not right for the widget") {

        val id = WidgetId(UUID.randomUUID().toString)
        (coursesService.checkWidget(_: WidgetId, _: WidgetInput)(_: TracingContext[IO]))
          .when(id, *, *).returns(EitherT.leftT(WrongInputForWidget))

        val request = Request[IO](method = Method.POST, uri = Uri.unsafeFromString(s"/checkWidget/${id.underlying}"))
          .withEntity(Json.obj("answerId" -> Json.fromInt(2)))

        controller.run(request)
          .value
          .map(_.get.status.code shouldBe 400)
          .unsafeToFuture()
      }

      it("Should return a 400 when the widget is not interactive") {

        val id = WidgetId(UUID.randomUUID().toString)

        (coursesService.checkWidget(_: WidgetId, _: WidgetInput)(_: TracingContext[IO]))
          .when(id, *, *).returns(EitherT.leftT(WidgetNotInteractive(id)))

        val request = Request[IO](method = Method.POST, uri = Uri.unsafeFromString(s"/checkWidget/${id.underlying}"))
          .withEntity(Json.obj("answerId" -> Json.fromInt(2)))

        controller.run(request)
          .value
          .map(_.get.status.code shouldBe 400)
          .unsafeToFuture()
      }

      it("Should return a 400 when the answer is wrong") {

        val id = WidgetId(UUID.randomUUID().toString)
        (coursesService.checkWidget(_: WidgetId, _: WidgetInput)(_: TracingContext[IO]))
          .when(id, *, *).returns(EitherT.leftT(WrongAnswer))

        val request = Request[IO](method = Method.POST, uri = Uri.unsafeFromString(s"/checkWidget/${id.underlying}"))
          .withEntity(Json.obj("answerId" -> Json.fromInt(2)))

        controller.run(request)
          .value
          .map(_.get.status.code shouldBe 400)
          .unsafeToFuture()
      }


      it("Should return a 200 when the answer is right") {

        val id = WidgetId(UUID.randomUUID().toString)
        (coursesService.checkWidget(_: WidgetId, _: WidgetInput)(_: TracingContext[IO]))
          .when(id, *, *).returns(EitherT.rightT(RightAnswer))

        val request = Request[IO](method = Method.POST, uri = Uri.unsafeFromString(s"/checkWidget/${id.underlying}"))
          .withEntity(Json.obj("answerId" -> Json.fromInt(2)))

        controller.run(request)
          .value
          .map(_.get.status.code shouldBe 200)
          .unsafeToFuture()
      }
    }
  }

  implicit private lazy val tcb: TracingContextBuilder[IO] =
    NoOpTracingContext.getNoOpTracingContextBuilder[IO].unsafeRunSync()

  implicit private lazy val mediaHandler: MediaHandler = stub[MediaHandler]
  implicit private lazy val coursesService: CoursesService[IO] = stub[MockableCourseService]
  implicit private lazy val controller = CoursesController()

  private class MockableCourseService(
    implicit courseRepository: CourseRepository[IO],
    interactiveWidgetsService: InteractiveWidgetsService[IO],
  ) extends CoursesService[IO]
}
