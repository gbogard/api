package lambda.infrastructure.gateway

import cats.effect._
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.blaze._
import scala.concurrent.duration._
import lambda.infrastructure.ExecutionContexts._
import org.http4s.server.middleware._
import lambda.domain.MediaHandler
import lambda.application._
import com.colisweb.tracing.TracingContextBuilder
import com.typesafe.scalalogging.LazyLogging

object Api extends LazyLogging {

  def apply()(
    implicit tracingContextBuilder: TracingContextBuilder[IO],
    coursesService: CoursesService[IO, IO.Par],
    mediaHandler: MediaHandler
  ) = {

    val services = CoursesController() <+> MediaController()(blockingEc, globalContextShift)

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
