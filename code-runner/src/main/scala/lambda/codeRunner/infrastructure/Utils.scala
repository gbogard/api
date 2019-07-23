package lambda.coderunner.infrastructure

import cats.effect._
import cats.data.EitherT
import java.io.File
import java.nio.file.Files
import java.util.UUID
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import org.apache.commons.io.FileUtils
import com.typesafe.scalalogging.StrictLogging
import lambda.coderunner.domain._

object Utils extends StrictLogging {

  def createTemporaryFolder[F[_]: Sync](): Resource[F, File] = {
    val create = Sync[F].delay {
      val randomName = UUID.randomUUID().toString()
      val f = Files.createTempDirectory(randomName).toFile
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
