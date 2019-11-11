package lambda.infrastructure.gateway.services

import lambda.infrastructure.gateway.Serialization.Decoders._
import lambda.infrastructure.gateway.Serialization.Encoders._
import lambda.infrastructure.gateway.ErrorsEncoders._
import cats.effect._
import io.circe.syntax._
import org.http4s.dsl.io._
import org.http4s.circe.CirceEntityCodec._
import lambda.application._
import lambda.domain.courses._
import lambda.domain.courses.Course.CourseId
import lambda.domain.MediaHandler
import com.colisweb.tracing.http4s.TracedHttpRoutes
import com.colisweb.tracing.TracingContextBuilder
import com.colisweb.tracing.http4s.TracedHttpRoutes._

object CourseService {
  def apply()(
      implicit handler: CoursesRequestHandler[IO, IO.Par],
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
            case Right(output)                       => Ok(output.asJson)
          }
      }

  }

}
