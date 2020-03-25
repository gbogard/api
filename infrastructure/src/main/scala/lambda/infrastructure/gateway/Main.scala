package lambda.infrastructure.gateway

import cats.effect._
import com.colisweb.tracing.{LoggingTracingContext, TracingContextBuilder}
import lambda.application.{CoursesService, InteractiveWidgetsService}
import lambda.domain.code.CodeTemplateEngine
import lambda.domain.courses.CourseRepository
import lambda.infrastructure.code.{ScalaCodeRunnerInterpreter, TemplateEngineInterpreter}
import lambda.infrastructure.{Configuration, CourseRepositoryInterpreter, MediaHandlerInterpreter}
import lambda.runners.scala.client.ScalaRunnerClient

object Main extends IOApp {

  val templateEngine: CodeTemplateEngine[IO] = new TemplateEngineInterpreter[IO]
  implicit val courseRepository: CourseRepository[IO] = new CourseRepositoryInterpreter

  def run(args: List[String]): IO[ExitCode] =
    resources.use((buildApi _).tupled)

  def resources =
    for {
      config <- Resource.liftF(Configuration.load[IO])
      scalaRunnerClient <- lambda.runners.scala.client.build(config.scalaRunnerHost, config.scalaRunnerPort)
    } yield (config, scalaRunnerClient)

  def buildApi(config: Configuration, scalaRunnerClient: ScalaRunnerClient) =
    for {
      tracingContextBuilder <- createTracingContext
      mediaHandler = new MediaHandlerInterpreter(config)
      coursesService = {
        implicit val scala2CodeRunner = new ScalaCodeRunnerInterpreter(scalaRunnerClient)
        implicit val sourceFileHandler = lambda.infrastructure.code.sourceFileHandler(config)
        implicit val templateEngine = new lambda.infrastructure.code.TemplateEngineInterpreter[IO]
        implicit val interactiveWidgetsService = new InteractiveWidgetsService[IO]
        new CoursesService[IO, IO.Par]
      }
      exitCode <- Api()(tracingContextBuilder, coursesService, mediaHandler)
    } yield exitCode

  private val createTracingContext: IO[TracingContextBuilder[IO]] = 
    IO.pure(LoggingTracingContext())
}
