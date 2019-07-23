package lambda.infrastructure.code

import org.scalatest.Assertion
import TestUtils._
import cats.effect.IO
import scala.concurrent.duration._
import scala.concurrent._
import cats.implicits._
import cats.effect._
import lambda.domain.code.ScalaCodeRunner.ScalaDependency
import lambda.infrastructure.Utils

class ScalaCodeRunnerSpec extends Approbation {

  describe("The Scala Code Runner") {

    describe("Single file program") {

      describe("When the program compiles") {
        it("Should return the program output when it runs") { approver =>
          testCodeRunner(
            "Main",
            Nil,
            "scala/compiles-and-runs.sc" :: Nil,
            result =>
              IO {
                approver.verify(result.right.get)
                succeed
              }
          )
        }

        it("Should return the standard error output when the program fails") { approver =>
          testCodeRunner(
            "Main",
            Nil,
            "scala/compiles-and-fails.sc" :: Nil,
            result =>
              IO {
                approver.verify(result.left.get)
                succeed
              }
          )
        }

        it(
          "Should return an error when the program doesn't exit after a timeout"
        ) { approver =>
          testCodeRunner(
            "Main",
            Nil,
            "scala/timeout.sc" :: Nil,
            result =>
              IO {
                approver.verify(result.left.get)
                succeed
              },
            1 second
          )
        }
      }

      describe("When the program does not compile") {
        it("It should return the compilation output") { approver =>
          testCodeRunner(
            "Main",
            Nil,
            "scala/does-not-compile.sc" :: Nil,
            result =>
              IO {
                approver.verify(result.left.get)
                succeed
              }
          )
        }
      }

    }

    describe("Multiple files program") {

      describe("When the program compiles") {
        it("Should return the program outputs when it runs") { approver =>
          testCodeRunner(
            "multi.Main",
            Nil,
            List(
              "scala/multiple-files-program/Operations.scala",
              "scala/multiple-files-program/Main.scala"
            ),
            result =>
              IO {
                approver.verify(result.right.get)
                succeed
              }
          )
        }
      }

      describe("When the main class is not found") {
        it("Should return the output of the Scala interpreter") { approver =>
          testCodeRunner(
            "toto",
            Nil,
            List(
              "scala/multiple-files-program/Operations.scala",
              "scala/multiple-files-program/Main.scala"
            ),
            result =>
              IO {
                approver.verify(result.left.get)
                succeed
              }
          )
        }
      }
    }

    describe("Program with external dependencies") {

      describe("When the program compiles") {

        it("Should return the output of the program") { approver =>
          testCodeRunner(
            "Main",
            List(
              ScalaDependency("org.typelevel", "cats-core", "1.6.1")
            ),
            List(
              "scala/with-cats.sc"
            ),
            result =>
              IO {
                approver.verify(result.right.get)
                succeed
              }
          )
        }
      }
    }

    describe("Security measures") {
      it("Should not be able to read files") { approver =>
        testCodeRunner(
          "Main",
          Nil,
          "scala/security/read-files.sc" :: Nil,
          result =>
            IO {
              approver.verify(limitLines(result.left.get, 1))
              succeed
            }
        )
      }

      it("Should not be able to launch external processes") { approver =>
        testCodeRunner(
          "Main",
          Nil,
          "scala/security/external-process.sc" :: Nil,
          result =>
            IO {
              approver.verify(limitLines(result.left.get, 1))
              succeed
            }
        )
      }
    }
  }

  implicit private val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  private def testCodeRunner(
      mainClass: String,
      dependencies: List[ScalaDependency],
      resources: List[String],
      assertion: Either[String, String] => IO[Assertion],
      timeout: FiniteDuration = 30 seconds
  ): Future[Assertion] = {
    resources
      .parTraverse(Utils.readResource[IO](_))
      .flatMap(
        files =>
          ScalaCodeRunnerImpl
            .run(files, mainClass, dependencies, timeout)
            .leftMap((normalizeEndings _) andThen (limitLines(_)))
            .value
      )
      .flatMap(assertion)
      .unsafeToFuture()
  }
}
