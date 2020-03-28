package lambda.api.application

import cats.implicits._
import org.http4s.implicits._
import cats.effect.{Blocker, ContextShift, ExitCode, IO, Timer}
import com.colisweb.tracing.TracingContextBuilder
import com.typesafe.scalalogging.LazyLogging
import lambda.api.domain.CoursesService
import lambda.domain.MediaHandler
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, CORSConfig}

import scala.concurrent.duration._

object Api extends LazyLogging {

  def apply()(
    implicit tracingContextBuilder: TracingContextBuilder[IO],
    coursesService: CoursesService[IO],
    mediaHandler: MediaHandler,
    cs: ContextShift[IO],
    timer: Timer[IO]
  ): IO[ExitCode] = Blocker[IO] use { implicit blocker =>

    val services = CoursesController() <+> MediaController()

    val corsConfig = CORSConfig(
      anyOrigin = false,
      anyMethod = true,
      allowedOrigins = Set(
        "http://localhost:3000",
        "http://localhost:1111",
        "https://lambdacademy.dev",
        "https://dashboard.lambdacademy.dev"
      ),
      allowCredentials = false,
      maxAge = 1.day.toSeconds
    )

    val app = CORS(services, corsConfig).orNotFound
    val host = "0.0.0.0"
    val port = 8080

    IO {
      logger.info("Starting Lambdacademy server on {}:{}", host, port)
    } *> BlazeServerBuilder[IO]
      .bindHttp(port, host)
      .withHttpApp(app)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

}
