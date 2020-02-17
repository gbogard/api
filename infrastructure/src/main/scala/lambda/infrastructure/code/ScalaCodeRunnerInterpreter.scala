package lambda.infrastructure.code

import java.io.File

import cats.data.EitherT
import cats.effect._
import com.typesafe.scalalogging.StrictLogging
import lambda.domain.code.{ProcessResult, ScalaCodeRunner}
import lambda.domain.code.ScalaCodeRunner._
import lambda.infrastructure.Configuration
import lambda.programexecutor.ProgramEvent
import lambda.runners.scala
import lambda.runners.scala.Dependency

class ScalaCodeRunnerInterpreter(conf: Configuration) extends ScalaCodeRunner[IO] with StrictLogging {
  import conf._

  def runFiles(files: List[File], dependencies: List[ScalaDependency]): ProcessResult[IO] =
    EitherT(
      scala
        .runFiles(
          sourceFiles = files,
          dependencies = dependencies.map(d => Dependency(d.org, d.name, d.version))
        )
        .compile
        .to[List]
        .map(events => {
          val exitCode = events
            .collect({
              case ProgramEvent.Exit(code) => code
            })
            .head

          val out = events
            .collect({
              case ProgramEvent.StdOut(out) => out
            })
            .mkString

          val err = events
            .collect({
              case ProgramEvent.StdErr(err) => err
            })
            .mkString

          if (exitCode == 0) {
            Right(out)
          } else {
            Left(err)
          }
        })
    )

}
