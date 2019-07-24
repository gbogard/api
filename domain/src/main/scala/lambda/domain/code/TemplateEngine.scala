package lambda.domain.code

import cats.effect.Resource
import java.io.File

trait TemplateEngine[F[_]] {

  def canRender(file: File): Boolean

  def render(
      files: List[File],
      data: Map[String, Any] = Map.empty
  ): Resource[F, List[File]]
}
