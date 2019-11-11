package lambda.infrastructure.gateway

import cats.effect._
import lambda.infrastructure.code.TemplateEngineInterpreter
import lambda.domain.code.TemplateEngine
import lambda.domain.courses.CourseRepository
import lambda.application.InteractiveWidgetHandler.WidgetHandlerContext
import lambda.infrastructure.code.ScalaCodeRunnerInterpreter
import lambda.infrastructure.courses.LibraryCourseRepository
import lambda.infrastructure.Configuration
import lambda.infrastructure.MediaHandlerInterpreter
import lambda.infrastructure.courseTemplateEngine.CourseTemplateEngineInterpreter
import lambda.application.CoursesRequestHandler
import lambda.domain.courses.CourseTemplateEngine
import com.colisweb.tracing.LoggingTracingContext
import com.colisweb.tracing.TracingContextBuilder

object Main extends IOApp {

  val templateEngine: TemplateEngine[IO] = new TemplateEngineInterpreter[IO]
  implicit val courseTemplateEngine: CourseTemplateEngine[IO] = CourseTemplateEngineInterpreter
  implicit val courseRepository: CourseRepository[IO] = new LibraryCourseRepository

  def run(args: List[String]): IO[ExitCode] =
    for {
      tracingContextBuilder <- createTracingContext
      config <- Configuration.load[IO]
      scala2CodeRunner = new ScalaCodeRunnerInterpreter()(config)
      widgetHandlerContext = WidgetHandlerContext(
        scala2CodeRunner = scala2CodeRunner,
        templateEngine = templateEngine,
        sourceFileHandler = lambda.infrastructure.code.sourceFileHandler
      )
      mediaHandler = new MediaHandlerInterpreter(config)
      coursesRequestHandler = {
        implicit val whc = widgetHandlerContext
        new CoursesRequestHandler[IO, IO.Par]
      }
      apiContext = Api.Context(
        coursesRequestHandler,
        mediaHandler
      )
      exitCode <- Api()(apiContext, tracingContextBuilder)
    } yield exitCode

  private val createTracingContext: IO[TracingContextBuilder[IO]] = 
    IO.pure(LoggingTracingContext())
}
