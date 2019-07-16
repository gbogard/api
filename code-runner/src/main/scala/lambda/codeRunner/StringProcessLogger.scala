package lambda.coderunner

import scala.sys.process._
import cats.data.EitherT
import cats.effect.IO

class StringProcessLogger extends ProcessLogger() {
  var stdOut = ""
  var stdErr = ""

  def buffer[T](f: => T): T = f
  def err(s: => String): Unit = stdErr += s + "\r\n"
  def out(s: => String): Unit = stdOut += s + "\r\n"
}

object StringProcessLogger {
  def run(process: ProcessBuilder): EitherT[IO, String, String] = EitherT {
    IO {
      val logger = new StringProcessLogger
      val exitCode = process.!(logger)
      if (exitCode > 0) Left(logger.stdErr) else Right(logger.stdOut)
    }
  }
}
