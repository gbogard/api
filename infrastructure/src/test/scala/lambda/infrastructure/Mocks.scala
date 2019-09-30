package lambda.infrastructure

import cats.data.OptionT
import cats.effect.IO
import lambda.domain.courses.Course
import lambda.domain.courses._
import lambda.domain.courses.Course._
import lambda.domain.courses.CourseRepository
import lambda.domain.code.ScalaCodeRunner
import lambda.domain.code.TemplateEngine
import cats.effect.Resource
import lambda.domain.MediaHandler
import lambda.domain.Media
import com.colisweb.tracing.TracingContext

object Mocks {
  def mockCourseRepository(
      coursesResult: List[CourseManifest] = Nil,
      singleCourseResult: CourseId => Option[Course] = Function.const(None),
      singleWidgetResult: WidgetId => Option[Widget] = Function.const(None)
  ): CourseRepository[IO] = new CourseRepository[IO] {
    def getCourse(
        id: lambda.domain.courses.Course.CourseId
    ): cats.data.OptionT[cats.effect.IO, lambda.domain.courses.Course] =
      OptionT.fromOption(singleCourseResult(id))
    def getCourses(): cats.effect.IO[List[lambda.domain.courses.Course.CourseManifest]] =
      IO.pure(coursesResult)
    def getWidget(
        id: WidgetId
    ): cats.data.OptionT[cats.effect.IO, Widget] =
      OptionT.fromOption(singleWidgetResult(id))
  }

  def mockCourse(): Course = Course(
    CourseId("1"),
    "Mocked Course",
    "Lorem Ipsum dolor sit amet",
    "scala" :: "fp" :: Nil,
    image = None,
    pages = Nil,
  )

  def mockScalaCodeRunner(): ScalaCodeRunner[IO] = new ScalaCodeRunner[IO] {

    def run(
        files: List[java.io.File],
        mainClass: String,
        dependencies: List[lambda.domain.code.ScalaCodeRunner.ScalaDependency],
        timeout: scala.concurrent.duration.FiniteDuration
    )(implicit tracingContext: TracingContext[IO]) = ???
  }

  def mockTemplateEngine(): TemplateEngine[IO] = new TemplateEngine[IO] {
    def canRender(file: java.io.File): Boolean = true
    def render(
        files: List[java.io.File],
        data: Map[String, Any] = Map.empty
    ): cats.effect.Resource[cats.effect.IO, List[java.io.File]] =
      Resource.pure(files)
  }

  implicit def mockMediaHandler: MediaHandler = new MediaHandler {
    def toUrl(media: Media): String = ""
  }
}
