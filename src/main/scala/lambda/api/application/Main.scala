package lambda.api.application

import cats.effect._
import com.colisweb.tracing.{LoggingTracingContext, TracingContextBuilder}
import lambda.api.domain._
import lambda.api.infrastructure._
import lambda.domain.code.CodeTemplateEngine
import lambda.domain.courses.CourseRepository
import lambda.api.infrastructure.code.{ScalaCodeRunnerInterpreter, TemplateEngineInterpreter}
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
      exitCode <- {
        implicit val tcb = tracingContextBuilder
        implicit val scala2CodeRunner = new ScalaCodeRunnerInterpreter(scalaRunnerClient)
        implicit val sourceFileHandler = lambda.api.infrastructure.code.sourceFileHandler(config)
        implicit val templateEngine = new lambda.api.infrastructure.code.TemplateEngineInterpreter[IO]
        implicit val interactiveWidgetsService = new InteractiveWidgetsService[IO]
        implicit val mediaHandler = new MediaHandlerInterpreter(config)
        implicit val coursesService =  new CoursesService[IO]
        implicit val verfiier = new AuthenticationTokenVerifierInterpreter(config)
        Api()
      }
    } yield exitCode

  private val createTracingContext: IO[TracingContextBuilder[IO]] =
    IO.pure(LoggingTracingContext())
}
