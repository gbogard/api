package lambda.application

import lambda.domain.courses.CourseRepository
import cats.effect.Sync
import cats.implicits._
import lambda.application.InteractiveWidgetHandler.WidgetHandlerContext
import lambda.domain.courses.Course._
import lambda.domain.courses.Course
import lambda.domain.courses._
import cats.data.OptionT
import cats.data.EitherT
import com.colisweb.tracing.TracingContext

class CoursesRequestHandler[F[_]: Sync, Par[_]]()(
    implicit courseRepository: CourseRepository[F],
    widgetHandlerContext: WidgetHandlerContext[F]
) {

  def getCoursesManifests(): F[List[CourseManifest]] = courseRepository.getCourses()

  def getCourseById(id: CourseId): OptionT[F, Course] = courseRepository.getCourse(id)

  def checkWidget(id: WidgetId, input: WidgetInput)(
      implicit tracingContext: TracingContext[F]
  ): EitherT[F, WidgetError, WidgetOutput] = {
    for {
      widget <- EitherT.fromOptionF(
        courseRepository.getWidget(id).value,
        WidgetError.WidgetNotFound(id)
      )
      result <- widget match {
        case w: InteractiveWidget => InteractiveWidgetHandler[F, Par](w, input)
        case _                    => EitherT.leftT[F, WidgetOutput](WidgetError.WidgetNotInteractive(id)).leftWiden[WidgetError]
      }
    } yield result
  }
}
