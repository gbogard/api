package lambda.gateway

import cats.effect._
import lambda.coderunner.infrastructure.SSPTemplateEngine
import lambda.coderunner.domain.TemplateEngine
import lambda.courses.domain.CourseRepository
import lambda.courses.application.InteractiveWidgetHandler.WidgetHandlerContext
import lambda.coderunner.domain.ScalaCodeRunner
import lambda.coderunner.infrastructure.ScalaCodeRunnerImpl

object Main extends IOApp {

  val templateEngine: TemplateEngine[IO] = new SSPTemplateEngine[IO]

  // TODO : fix this
  val courseRepository: CourseRepository[IO] = ???
  val scala2CodeRunner: ScalaCodeRunner[IO] = new ScalaCodeRunnerImpl

  val widgetHandlerContext = WidgetHandlerContext(
    scala2CodeRunner = scala2CodeRunner,
    templateEngine = templateEngine
  )

  implicit val apiContext = Api.Context(
    courseRepository,
    widgetHandlerContext
  )

  def run(args: List[String]): IO[ExitCode] = Api()
}
