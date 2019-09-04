package lambda.infrastructure.gateway

import cats.effect._
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.blaze._
import scala.concurrent.duration._
import lambda.infrastructure.gateway.services._
import lambda.infrastructure.ExecutionContexts._
import org.http4s.server.middleware._
import lambda.domain.MediaHandler
import lambda.application._
import com.colisweb.tracing.TracingContext.TracingContextBuilder

object Api {

  case class Context(
      coursesRequestHandler: CoursesRequestHandler[IO, IO.Par], 
      mediaHandler: MediaHandler
  )

  def apply()(implicit ctx: Context, tracingContextBuilder: TracingContextBuilder[IO]) = {
    implicit val coursesRequestHandler = ctx.coursesRequestHandler
    implicit val mediaHandler = ctx.mediaHandler

    val services = CourseService() <+> MediaService()(blockingEc, globalContextShift)

    val corsConfig = CORSConfig(
      anyOrigin = false,
      anyMethod = true,
      allowedOrigins = Set(
        "http://localhost:1111",
        "https://lambdacademy.dev",
        "https://dashboard.lambdacademy.dev"
      ),
      allowCredentials = false,
      maxAge = 1.day.toSeconds
    )

    val app = CORS(services, corsConfig).orNotFound

    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(app)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

}
