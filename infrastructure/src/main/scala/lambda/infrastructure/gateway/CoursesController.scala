package lambda.infrastructure.gateway

import cats.effect.IO
import com.colisweb.tracing.TracingContextBuilder
import com.colisweb.tracing.http4s.TracedHttpRoutes
import com.colisweb.tracing.http4s.TracedHttpRoutes.using
import lambda.application.{CoursesService, WidgetError, WidgetInput}
import lambda.domain.MediaHandler
import lambda.domain.courses.Course.CourseId
import lambda.domain.courses.WidgetId
import lambda.infrastructure.gateway.ErrorsEncoders.widgetErrorEncoder
import org.http4s.dsl.io._
import lambda.infrastructure.serialization._

object CoursesController {
  def apply()(
    implicit handler: CoursesService[IO, IO.Par],
    mediaHandler: MediaHandler,
    tracingContextBuilder: TracingContextBuilder[IO]
  ) = TracedHttpRoutes[IO] {
    case (GET -> Root / "courses") using _ =>
      handler.getCoursesManifests.flatMap(Ok(_))

    case (GET -> Root / "courses" / id) using _ =>
      handler.getCourseById(CourseId(id)).value flatMap {
        case Some(course) => Ok(course)
        case None         => NotFound()
      }

    case (req @ POST -> Root / "checkWidget" / id) using ctx =>
      implicit val tracingContext = ctx

      req.attemptAs[WidgetInput].value.flatMap {
        case Left(failure) => BadRequest(failure.message)
        case Right(input) =>
          handler.checkWidget(WidgetId(id), input).value.flatMap {
            case Left(e: WidgetError.WidgetNotFound) => NotFound(widgetErrorEncoder(e))
            case Left(e)                             => BadRequest(widgetErrorEncoder(e))
            case Right(output)                       => Ok(output)
          }
      }

  }

}
