package lambda.infrastructure.gateway

import cats.effect._
import lambda.infrastructure.code.{ScalaCodeRunnerInterpreter, TemplateEngineInterpreter}
import lambda.domain.code.TemplateEngine
import lambda.domain.courses.CourseRepository
import lambda.infrastructure.{Configuration, LibraryCourseRepository, MediaHandlerInterpreter}
import lambda.application.{CoursesService, InteractiveWidgetsService}
import com.colisweb.tracing.LoggingTracingContext
import com.colisweb.tracing.TracingContextBuilder

object Main extends IOApp {

  val templateEngine: TemplateEngine[IO] = new TemplateEngineInterpreter[IO]
  implicit val courseRepository: CourseRepository[IO] = new LibraryCourseRepository

  def run(args: List[String]): IO[ExitCode] =
    for {
      tracingContextBuilder <- createTracingContext
      config <- Configuration.load[IO]
      mediaHandler = new MediaHandlerInterpreter(config)
      coursesRequestHandler = {
        implicit val scala2CodeRunner = new ScalaCodeRunnerInterpreter
        implicit val sourceFileHandler = lambda.infrastructure.code.sourceFileHandler(config)
        implicit val templateEngine = new lambda.infrastructure.code.TemplateEngineInterpreter[IO]
        implicit val interactiveWidgetsService = new InteractiveWidgetsService[IO]
        new CoursesService[IO, IO.Par]
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
