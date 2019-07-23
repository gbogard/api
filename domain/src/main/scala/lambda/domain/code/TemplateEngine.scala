package lambda.domain.code

import cats.effect._
import cats.Monad
import java.io.File

trait TemplateEngine[F[_]] {
  def render(file: File, data: Map[String, Any] = Map.empty): Resource[F, File]

  def canRender(file: File): Boolean

  def render(
      files: List[File],
      data: Map[String, Any]
  )(implicit m: Monad[F]): Resource[F, List[File]] =
    files.map(render(_, data)).foldLeft(Resource.pure[F, List[File]](Nil)) {
      case (listResource, fileResource) => listResource.flatMap(list => fileResource.map(file => list :+ file))
    }
}
