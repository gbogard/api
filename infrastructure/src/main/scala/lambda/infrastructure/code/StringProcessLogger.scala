package lambda.infrastructure.code

import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._
import scala.util.{Failure, Success}

class StringProcessLogger extends ProcessLogger() {
  var stdOut = ""
  var stdErr = ""

  def buffer[T](f: => T): T = f

  def err(s: => String): Unit = stdErr += s + "\r\n"

  def out(s: => String): Unit = stdOut += s + "\r\n"
}

object StringProcessLogger extends StrictLogging {
  def run(processBuilder: ProcessBuilder, onCancel: IO[Unit] = IO.unit)(implicit ec: ExecutionContext): EitherT[IO, String, String] = EitherT {
    IO.cancelable { cb =>
      val processLogger = new StringProcessLogger
      logger.debug(
        "Running external process through StringProcessLogger : {}",
        processBuilder
      )
      val process = processBuilder.run(processLogger)
      val computation = Future {
        val exitCode = process.exitValue()
        logger.debug(
          "{} StdOut: {} StdErr: {}",
          process,
          processLogger.stdOut,
          processLogger.stdErr
        )
        if (exitCode > 0) Left(processLogger.stdErr)
        else Right(processLogger.stdOut)
      }
      computation.onComplete({
        case Success(output) => cb(Right(output))
        case Failure(error) => cb(Left(error))
      })
      onCancel *> IO {
        process.destroy()
      }

    }
  }
}
