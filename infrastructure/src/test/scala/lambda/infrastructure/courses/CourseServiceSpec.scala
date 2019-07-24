package lambda.infrastructure.courses

import cats.effect.IO
import io.circe._
import io.circe.syntax._
import lambda.domain.courses.widgets._
import lambda.domain.courses.widgets.WidgetInput._
import lambda.infrastructure.gateway.Serialization._
import lambda.application.InteractiveWidgetHandler.WidgetHandlerContext
import lambda.infrastructure.code._
import lambda.infrastructure.Mocks._
import lambda.infrastructure.gateway.services.CourseService
import org.http4s.circe._
import org.http4s._
import org.scalatest._

class CourseServiceSpec extends AsyncFunSpec with Matchers {

  describe("Course service") {
    describe("GET /courses") {
      it("Should return a list of course manifests when the repository has courses") {
        val expectedCourses = lambda.library.courses.map(_.manifest)

        implicit val repo = mockCourseRepository(
          coursesResult = expectedCourses
        )
        implicit val ctx = mockWidgetHandlerContext()
        val service = CourseService()

        val request = Request[IO](uri = Uri.unsafeFromString("/courses"))
        service
          .run(request)
          .value
          .flatMap(_.get.as[String])
          .map(body => {
            body shouldBe expectedCourses.asJson.noSpaces
          })
          .unsafeToFuture()
      }

      it("Should return an empty list when the repository has no course") {
        implicit val repo = mockCourseRepository()
        implicit val ctx = mockWidgetHandlerContext()
        val service = CourseService()

        val request = Request[IO](uri = Uri.unsafeFromString("/courses"))
        service
          .run(request)
          .value
          .flatMap(_.get.as[String])
          .map(body => {
            body shouldBe "[]"
          })
          .unsafeToFuture()
      }
    }

    describe("GET /courses/id") {
      val course = mockCourse()

      implicit val repo = mockCourseRepository(singleCourseResult = {
        case id if id == course.id => Some(course)
        case _                     => None
      })

      implicit val ctx = mockWidgetHandlerContext()
      val service = CourseService()

      it("Should return the matching course if any") {
        val request = Request[IO](uri = Uri.unsafeFromString(s"/courses/${course.id.underlying}"))
        service
          .run(request)
          .value
          .flatMap(_.get.as[String])
          .map(body => {
            body shouldBe course.asJson.noSpaces
          })
          .unsafeToFuture()
      }

      it("Should return a 404 when no course is found") {
        val request = Request[IO](uri = Uri.unsafeFromString(s"/courses/toto"))
        service
          .run(request)
          .value
          .map(_.get.status.code shouldBe 404)
          .unsafeToFuture()
      }
    }

    describe("POST /checkWidget/id") {

      it("Should return a 400 if the input is not a valid widget input") {
        implicit val repo = mockCourseRepository()
        implicit val ctx = mockWidgetHandlerContext()
        val service = CourseService()
        val request = Request[IO](method = Method.POST, uri = Uri.unsafeFromString("/checkWidget/toto"))

        service
          .run(request)
          .value
          .map(_.get.status.code shouldBe 400)
          .unsafeToFuture()
      }

      it("Should return a 404 when the widget does not exist") {
        implicit val repo = mockCourseRepository()
        implicit val ctx = mockWidgetHandlerContext()
        val service = CourseService()
        val request = Request[IO](method = Method.POST, uri = Uri.unsafeFromString("/checkWidget/toto"))
          .withEntity(Json.obj("answerId" -> Json.fromInt(2)))

        service
          .run(request)
          .value
          .map(_.get.status.code shouldBe 404)
          .unsafeToFuture()
      }

      describe("ScalaCodeWidget") {
        val widget = InteractiveCodeWidget.Scala2CodeWidget(
          WidgetId("scala2"),
          Nil,
          "Main"
        )

        implicit val repo = mockCourseRepository(singleWidgetResult = {
          case id if id == widget.id => Some(widget)
          case _                     => None
        })

        implicit val ctx = mockWidgetHandlerContext()
        val service = CourseService()

        it("Return an error when the given input isn't a CodeInput") {
          val request = Request[IO](
            Method.POST,
            Uri.unsafeFromString("/checkWidget/scala2")
          ).withEntity(Json.obj("answerId" -> Json.fromInt(4)))

          service
            .run(request)
            .value
            .map(_.get)
            .map(res => {
              res.status.code shouldBe 400
              res.as[Json].unsafeRunSync() shouldBe
                Json.obj("error" -> Json.fromString("Wrong input for widget"))
            })
            .unsafeToFuture()

        }
      }

      describe("Multiple choices widget") {
        val widget = MultipleChoices(
          WidgetId("id"),
          required = true,
          MultipleChoices.Question(
            "What is love ?",
            MultipleChoices.Answer(
              AnswerId(2),
              "Baby don't hurt me"
            ),
            Nil
          )
        )

        implicit val repo = mockCourseRepository(singleWidgetResult = {
          case id if id == widget.id => Some(widget)
          case _                     => None
        })

        implicit val ctx = mockWidgetHandlerContext()
        val service = CourseService()

        it("Should return a 200 when the answer is correct") {
          val request = Request[IO](
            Method.POST,
            Uri.unsafeFromString("/checkWidget/id")
          ).withEntity(Json.obj("answerId" -> Json.fromInt(widget.question.rightAnswer.id.answerId)))

          service
            .run(request)
            .value
            .map(_.get)
            .map(res => {
              res.status.code shouldBe 200
              res.as[Json].unsafeRunSync() shouldBe Json.obj("success" -> Json.fromString("Right answer"))
            })
            .unsafeToFuture()
        }

        it("Should return a 400 when the answer is wrong") {
          val request = Request[IO](
            Method.POST,
            Uri.unsafeFromString("/checkWidget/id")
          ).withEntity(Json.obj("answerId" -> Json.fromInt(4)))

          service
            .run(request)
            .value
            .map(_.get)
            .map(res => {
              res.status.code shouldBe 400
              res.as[Json].unsafeRunSync() shouldBe Json.obj("error" -> Json.fromString("Wrong answer"))
            })
            .unsafeToFuture()
        }
      }
    }
  }

  private def mockWidgetHandlerContext(): WidgetHandlerContext[IO] = WidgetHandlerContext(
    mockScalaCodeRunner(),
    mockTemplateEngine(),
    sourceFileHandler
  )

}
