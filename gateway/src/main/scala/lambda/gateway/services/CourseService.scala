package lambda.gateway.services

import lambda.gateway.Serialization._
import lambda.courses.domain.CourseRepository
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe.CirceEntityCodec._
import lambda.courses.domain.widgets.WidgetInput
import lambda.courses.application.InteractiveWidgetHandler
import lambda.courses.application.InteractiveWidgetHandler.WidgetHandlerContext
import lambda.courses.domain.widgets._
import scala.concurrent.ExecutionContext
import lambda.courses.domain.Course.CourseId

object CourseService {
  def apply()(
      implicit courseRepository: CourseRepository[IO],
      widgetHandlerContext: WidgetHandlerContext[IO]
  ) = HttpRoutes.of[IO] {
    case GET -> Root / "courses" =>
      courseRepository.getCourses().flatMap(Ok(_))

    case GET -> Root / "courses" / id =>
      courseRepository.getCourse(CourseId(id)).flatMap(Ok(_))

    case req @ POST -> Root / "checkWidget" / widgetId =>
      req.attemptAs[WidgetInput].value.flatMap {
        case Right(widgetInput) =>
          courseRepository.getWidget(WidgetId(widgetId)) flatMap {
            case w: InteractiveWidget =>
              InteractiveWidgetHandler[IO, IO.Par](w, widgetInput).value flatMap {
                case Right(output) => Ok(output)
                case Left(error)   => BadRequest(error)
              }
            case _ => NotFound("Widget is not interactive")
          }
        case Left(failure) => BadRequest(failure.message)
      }
  }

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
}
