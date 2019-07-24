package lambda.infrastructure.code

import cats.effect._
import java.io.File
import java.util.UUID
import lambda.domain.code._
import org.apache.commons.io.FileUtils
import java.nio.charset.StandardCharsets

class SSPTemplateEngine[F[_]: Sync] extends TemplateEngine[F] {

  private val engine = new org.fusesource.scalate.TemplateEngine

  def canRender(file: File): Boolean = engine.canLoad(file.getAbsolutePath())

  private def renderSingleFile(
      file: File,
      data: Map[String, Any]
  ): Resource[F, File] = {
    val acquire = Sync[F] delay {
      val output = engine.layout(file.getAbsolutePath(), data)
      val tempFile = File.createTempFile(UUID.randomUUID().toString(), ".out")
      FileUtils.writeStringToFile(tempFile, output, StandardCharsets.UTF_8)
      tempFile
    }
    def release(f: File) = Sync[F] delay {
      FileUtils.forceDelete(f)
    }
    Resource.make(acquire)(release)
  }

  def render(files: List[File], data: Map[String, Any] = Map.empty): Resource[F, List[File]] =
    files.map(renderSingleFile(_, data)).foldLeft(Resource.pure[F, List[File]](Nil)) {
      case (listResource, fileResource) =>
        listResource.flatMap(list => fileResource.map(file => list :+ file))
    }
}
