package lambda.api.infrastructure

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import java.util.UUID

import cats.effect.{Resource, Sync}
import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.io.FileUtils

object Utils extends StrictLogging {

  def extractExtension(fileName: String): Option[String] =
    fileName.split('.').lastOption

  def readResource[F[_]: Sync](resourceName: String): Resource[F, File] = {
    def acquire = Sync[F].delay {
      val id = s"$resourceName-${UUID.randomUUID()}"
      val file = File.createTempFile(id, "." + extractExtension(resourceName).getOrElse("resource"))
      FileUtils.copyInputStreamToFile(getClass().getResourceAsStream(resourceName), file)
      file
    }

    def release(f: File) = Sync[F].delay {
      f.delete()
      ()
    }
    Resource.make(acquire)(release)
  }

  def createTemporaryFile[F[_]: Sync](content: String)(implicit config: Configuration): Resource[F, File] = {
    val create = Sync[F].delay {
      val randomName = UUID.randomUUID().toString()
      val basePath: Path = Paths.get(config.tmpFolder)
      val f = Files.createTempFile(basePath, randomName, "resource").toFile
      FileUtils.writeStringToFile(f, content, StandardCharsets.UTF_8)
      logger.debug("Creating temporary file {}", f.getAbsolutePath())
      f
    }
    def delete(f: File): F[Unit] = Sync[F].delay {
      logger.debug("Deleting temporary file {}", f.getAbsolutePath())
      FileUtils.forceDelete(f)
    }
    Resource.make(create)(delete)
  }

}
