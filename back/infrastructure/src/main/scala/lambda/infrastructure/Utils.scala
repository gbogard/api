package lambda.infrastructure

import cats.effect._
import cats.data.EitherT
import java.io.File
import java.nio.file.Files
import java.util.UUID
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import org.apache.commons.io.FileUtils
import com.typesafe.scalalogging.StrictLogging
import lambda.domain.code._
import java.nio.file._

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

  /**
   * Creates a temporary folder. The folder is deleted when the resource is freed. 
   * The base path for the temporary folder will be temporary-folders-path.container-path from the config
   */
  def createTemporaryFolder[F[_]: Sync]()(implicit config: Configuration): Resource[F, File] = {
    val create = Sync[F].delay {
      val randomName = UUID.randomUUID().toString()
      val basePath: Path = Paths.get(config.temporaryFoldersBase.containerPath)
      Files.createDirectories(basePath)
      val f = Files.createTempDirectory(basePath, randomName).toFile
      logger.debug("Creating temporary directory {}", f.getAbsolutePath())
      f
    }
    def delete(f: File): F[Unit] = Sync[F].delay {
      logger.debug("Deleting temporary directory {}", f.getAbsolutePath())
      FileUtils.deleteDirectory(f)
    }
    Resource.make(create)(delete)
  }

  def wrapEitherInResource[F[_]: Sync, A, L, R](
      resource: Resource[F, A],
      either: A => EitherT[F, L, R]
  ): EitherT[F, L, R] =
    EitherT.apply[F, L, R](resource.use(a => either(a).value))

  def withTimeout(process: ProcessResult[IO], timeout: FiniteDuration)(
      implicit timer: Timer[IO],
      cs: ContextShift[IO]
  ): ProcessResult[IO] = {
    EitherT {
      IO.race(
        timer.sleep(timeout),
        process.value
      ) map {
        case Left(_)        => Left("Code execution timed out.")
        case Right(process) => process
      }
    }
  }
}