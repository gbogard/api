package lambda.infrastructure.gateway

import cats.effect._
import lambda.infrastructure.code.SSPTemplateEngine
import lambda.domain.code.TemplateEngine
import lambda.domain.courses.CourseRepository
import lambda.application.InteractiveWidgetHandler.WidgetHandlerContext
import lambda.infrastructure.code.ScalaCodeRunnerImpl
import lambda.infrastructure.courses.LibraryCourseRepository
import lambda.infrastructure.Configuration
import lambda.infrastructure.MediaHandlerInterpreter

object Main extends IOApp {

  val templateEngine: TemplateEngine[IO] = new SSPTemplateEngine[IO]

  val courseRepository: CourseRepository[IO] = LibraryCourseRepository

  def run(args: List[String]): IO[ExitCode] =
    for {
      config <- Configuration.load[IO]
      scala2CodeRunner = new ScalaCodeRunnerImpl()(config)
      widgetHandlerContext = WidgetHandlerContext(
        scala2CodeRunner = scala2CodeRunner,
        templateEngine = templateEngine,
        sourceFileHandler = lambda.infrastructure.code.sourceFileHandler
      )
      mediaHandler = new MediaHandlerInterpreter(config)
      apiContext = Api.Context(
        courseRepository,
        widgetHandlerContext,
        mediaHandler
      )
      exitCode <- Api()(apiContext)
    } yield exitCode
}
