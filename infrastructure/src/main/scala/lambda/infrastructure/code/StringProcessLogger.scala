package lambda.infrastructure.code

import scala.sys.process._
import cats.data.EitherT
import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging

class StringProcessLogger extends ProcessLogger() {
  var stdOut = ""
  var stdErr = ""

  def buffer[T](f: => T): T = f
  def err(s: => String): Unit = stdErr += s + "\r\n"
  def out(s: => String): Unit = stdOut += s + "\r\n"
}

object StringProcessLogger extends StrictLogging {
  def run(process: ProcessBuilder): EitherT[IO, String, String] = EitherT {
    IO {
      val processLogger = new StringProcessLogger
      logger.debug(
        "Running external process through StringProcessLogger : {}",
        process
      )
      val exitCode = process.!(processLogger)
      logger.debug(
        "{} StdOut: {} StdErr: {}",
        process,
        processLogger.stdOut,
        processLogger.stdErr
      )
      if (exitCode > 0) Left(processLogger.stdErr)
      else Right(processLogger.stdOut)
    }
  }
}
