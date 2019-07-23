package lambda.infrastructure.gateway.services

import lambda.infrastructure.gateway.Serialization._
import lambda.domain.courses.CourseRepository
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe.CirceEntityCodec._
import lambda.domain.courses.widgets.WidgetInput
import lambda.application.InteractiveWidgetHandler
import lambda.application.InteractiveWidgetHandler.WidgetHandlerContext
import lambda.domain.courses.widgets._
import scala.concurrent.ExecutionContext
import lambda.domain.courses.Course.CourseId

object CourseService {
  def apply()(
      implicit courseRepository: CourseRepository[IO],
      widgetHandlerContext: WidgetHandlerContext[IO]
  ) = HttpRoutes.of[IO] {
    case GET -> Root / "courses" =>
      courseRepository.getCourses().flatMap(Ok(_))

    case GET -> Root / "courses" / id =>
      courseRepository.getCourse(CourseId(id)).value flatMap {
        case Some(course) => Ok(course)
        case None         => NotFound()
      }

    case req @ POST -> Root / "checkWidget" / widgetId =>
      req.attemptAs[WidgetInput].value.flatMap {
        case Right(widgetInput) =>
          courseRepository.getWidget(WidgetId(widgetId)).value flatMap {
            case Some(w: InteractiveWidget) =>
              InteractiveWidgetHandler[IO, IO.Par](w, widgetInput).value flatMap {
                case Right(output) => Ok(output)
                case Left(error)   => BadRequest(error)
              }
            case Some(_) => NotFound("Widget is not interactive")
            case _       => NotFound()
          }
        case Left(failure) => BadRequest(failure.message)
      }
  }

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
}
