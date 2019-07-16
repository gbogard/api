package lambda.coderunner

import cats.effect._
import cats.data.EitherT
import cats.implicits._
import java.io.File
import java.nio.file.Files
import java.util.UUID
import cats.Monad
import cats.Parallel
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import scala.io.Source
import org.apache.commons.io.FileUtils
import com.typesafe.scalalogging.StrictLogging

object Utils extends StrictLogging {

  type ProcessResult[F[_]] = EitherT[F, String, String]

  def createTemporaryFolder[F[_]]()(
      implicit m: Monad[F],
      s: Sync[F]
  ): Resource[F, File] = {
    val create = s.delay {
      val randomName = UUID.randomUUID().toString()
      val f = Files.createTempDirectory(randomName).toFile
      logger.debug("Creating temporary directory {}", f.getAbsolutePath())
      f
    }
    def delete(f: File): F[Unit] = s.delay {
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

  def readResource[F[_]: Sync](resourceName: String): F[File] = Sync[F].delay {
    new File(getClass().getClassLoader().getResource(resourceName).toURI())
  }

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
