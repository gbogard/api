package lambda.api.application

import cats.effect.IO
import com.colisweb.tracing.{TracingContext, TracingContextBuilder}
import com.colisweb.tracing.http4s.TracedHttpRoutes
import com.colisweb.tracing.http4s.TracedHttpRoutes.using
import lambda.api.domain.{AuthenticationTokenVerifier, CoursesService, WidgetError, WidgetInput}
import lambda.domain.MediaHandler
import lambda.domain.courses.Course.CourseId
import lambda.domain.courses.WidgetId
import lambda.api.infrastructure.serialization._
import org.http4s.dsl.Http4sDsl

object CoursesController extends Http4sDsl[IO] {
  def apply()(
    implicit coursesService: CoursesService[IO],
    mediaHandler: MediaHandler,
    tracingContextBuilder: TracingContextBuilder[IO],
    verifier: AuthenticationTokenVerifier[IO]
  ) =
    TracedHttpRoutes[IO] {
      case (GET -> Root / "courses") using _ =>
        coursesService.getCoursesManifests().flatMap(Ok(_))

      case GET -> Root / "courses" / id / "manifest" using _ =>
        coursesService.getManifestById(CourseId(id)) flatMap {
          case Some(course) => Ok(course)
          case None         => NotFound()
        }

      case (req @ GET -> Root / "courses" / id) using _ =>
        Authentication.requireUser(req)(_ =>
          coursesService.getCourseById(CourseId(id)) flatMap {
            case Some(course) => Ok(course)
            case None         => NotFound()
          }
        )

      case (req @ POST -> Root / "checkWidget" / id) using ctx =>
        implicit val tracingContext: TracingContext[IO] = ctx

        Authentication.requireUser(req)(_ =>
          req.attemptAs[WidgetInput].value.flatMap {
            case Left(failure) => BadRequest(failure.message)
            case Right(input) =>
              coursesService.checkWidget(WidgetId(id), input).value.flatMap {
                case Left(e: WidgetError.WidgetNotFound) =>
                  NotFound(widgetErrorEncoder(e))
                case Left(e)       => BadRequest(widgetErrorEncoder(e))
                case Right(output) => Ok(output)
              }
          }
        )

    }

}
