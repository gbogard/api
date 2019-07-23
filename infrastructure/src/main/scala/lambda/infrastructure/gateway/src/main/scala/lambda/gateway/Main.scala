package lambda.infrastructure.gateway

import cats.effect._
import lambda.infrastructure.code.SSPTemplateEngine
import lambda.domain.code.TemplateEngine
import lambda.domain.courses.CourseRepository
import lambda.application.InteractiveWidgetHandler.WidgetHandlerContext
import lambda.domain.code.ScalaCodeRunner
import lambda.infrastructure.code.ScalaCodeRunnerImpl

object Main extends IOApp {

  val templateEngine: TemplateEngine[IO] = new SSPTemplateEngine[IO]

  // TODO : fix this
  val courseRepository: CourseRepository[IO] = ???
  val scala2CodeRunner: ScalaCodeRunner[IO] = new ScalaCodeRunnerImpl

  val widgetHandlerContext = WidgetHandlerContext(
    scala2CodeRunner = scala2CodeRunner,
    templateEngine = templateEngine,
    sourceFileHandler = lambda.infrastructure.code.sourceFileHandler
  )

  implicit val apiContext = Api.Context(
    courseRepository,
    widgetHandlerContext
  )

  def run(args: List[String]): IO[ExitCode] = Api()
}
