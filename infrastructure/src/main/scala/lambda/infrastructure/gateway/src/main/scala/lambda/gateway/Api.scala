package lambda.infrastructure.gateway

import cats.effect._
import cats.implicits._
import lambda.domain.courses.CourseRepository
import lambda.application.InteractiveWidgetHandler.WidgetHandlerContext
import org.http4s.implicits._
import org.http4s.server.blaze._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import lambda.infrastructure.gateway.services.CourseService
import org.http4s.server.middleware._

object Api {

  case class Context(
      courseRepository: CourseRepository[IO],
      widgetHandlerContext: WidgetHandlerContext[IO]
  )

  implicit private val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit private val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  def apply()(implicit ctx: Context) = {
    implicit val courseRepository = ctx.courseRepository
    implicit val handlerContext = ctx.widgetHandlerContext

    val courseService = CourseService()

    val corsConfig = CORSConfig(
      anyOrigin = false,
      anyMethod = true,
      allowedOrigins = Set(
        "http://localhost:1111",
        "https://lambdacademy.dev"
      ),
      allowCredentials = false,
      maxAge = 1.day.toSeconds
    )

    val app = CORS(courseService, corsConfig).orNotFound

    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(app)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

}
