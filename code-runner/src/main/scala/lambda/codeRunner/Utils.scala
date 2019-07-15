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

object Utils {

  type ProcessResult[F[_]] = EitherT[F, String, String]

  def createTemporaryFolder[F[_]]()(
      implicit m: Monad[F],
      s: Sync[F]
  ): Resource[F, File] = {
    val create = s.delay {
      val randomName = UUID.randomUUID().toString()
      Files.createTempDirectory(randomName).toFile
    }
    def delete(f: File): F[Unit] = {
      if (f.isDirectory()) {
        f.listFiles().toList.traverse(delete) *> s.delay { f.delete() }
      } else {
        s.delay { f.delete() }
      }
    }
    Resource.make(create)(delete)
  }

  def wrapEitherInResource[F[_]: Sync, A, L, R](
      resource: Resource[F, A],
      either: A => EitherT[F, L, R]
  ): EitherT[F, L, R] =
    EitherT.apply[F, L, R](resource.use(a => either(a).value))

  def flattenProcessResults[F[_], Par[_]](
      results: List[ProcessResult[F]]
  )(implicit p: Parallel[F, Par], m: Monad[F]): ProcessResult[F] = {
    EitherT {
      results.parTraverse(_.value) map { eithers =>
        eithers.partition(_.isLeft) match {
          case (Nil, outputs) => Right(outputs.mkString)
          case (outputs, _)   => Left(outputs.mkString)
        }
      }
    }
  }

  def withTimeout(process: ProcessResult[IO], timeout: FiniteDuration)(
      implicit timer: Timer[IO], cs: ContextShift[IO]
  ): ProcessResult[IO] = {
    EitherT {
      IO.race(
        timer.sleep(timeout),
        process.value
      ) map {
        case Left(_) => Left("Code execution timed out.")
        case Right(process) => process
      }
    }
  }
}
