package lambda.infrastructure.gateway

import cats.effect._
import cats.implicits._
import lambda.domain.courses.CourseRepository
import lambda.application.InteractiveWidgetHandler.WidgetHandlerContext
import org.http4s.implicits._
import org.http4s.server.blaze._
import scala.concurrent.ExecutionContext
import lambda.infrastructure.gateway.services.CourseService

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

    val app = (courseService).orNotFound

    BlazeServerBuilder[IO]
      .bindHttp(8080)
      .withHttpApp(app)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

}
