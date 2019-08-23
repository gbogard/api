package lambda.infrastructure.gateway

import cats.effect._
import cats.implicits._
import lambda.domain.courses.CourseRepository
import lambda.application.InteractiveWidgetHandler.WidgetHandlerContext
import org.http4s.implicits._
import org.http4s.server.blaze._
import scala.concurrent.duration._
import lambda.infrastructure.gateway.services._
import lambda.infrastructure.ExecutionContexts._
import org.http4s.server.middleware._
import lambda.domain.MediaHandler

object Api {

  case class Context(
      courseRepository: CourseRepository[IO],
      widgetHandlerContext: WidgetHandlerContext[IO],
      mediaHandler: MediaHandler
  )

  def apply()(implicit ctx: Context) = {
    implicit val courseRepository = ctx.courseRepository
    implicit val handlerContext = ctx.widgetHandlerContext
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
