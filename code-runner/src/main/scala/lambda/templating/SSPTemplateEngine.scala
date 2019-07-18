package lambda.templating

import cats.effect._
import java.io.File
import java.util.UUID
import org.apache.commons.io.FileUtils

class SSPTemplateEngine[F[_]: Sync] extends TemplateEngine[F] {

  private val engine = new org.fusesource.scalate.TemplateEngine

  def render(
      file: File,
      data: Map[String, Any] = Map.empty
  ): Resource[F, File] = {
    val acquire = Sync[F] delay {
      val output = engine.layout(file.getAbsolutePath(), data)
      val tempFile = File.createTempFile(UUID.randomUUID().toString(), ".out")
      FileUtils.writeStringToFile(tempFile, output)
      tempFile
    }
    def release(f: File) = Sync[F] delay {
      FileUtils.forceDelete(f)
    }
    Resource.make(acquire)(release)
  }
}
