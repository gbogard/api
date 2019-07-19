package lambda.coderunner

import org.scalatest._
import TestUtils._
import cats.effect.IO
import com.github.writethemfirst.approvals.approvers.Approver
import scala.concurrent.duration._
import lambda.coderunner.infrastructure.ScalaCodeRunner
import ScalaCodeRunner._
import lambda.coderunner.infrastructure.Utils._

class ScalaCodeRunnerSpec extends Approbation {

  describe("The Scala Code Runner") {

    describe("Single file program") {

      describe("When the program compiles") {
        it("Should return the program output when it runs") { approver =>
          testCodeRunner(
            ScalaCodeRunner("Main", Nil),
            "scala/compiles-and-runs.sc" :: Nil,
            result =>
              IO {
                approver.verify(result.right.get)
                succeed
              }
          )
        }

        it("Should return the standard error output when the program fails") {
          approver =>
            testCodeRunner(
              ScalaCodeRunner("Main", Nil),
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
            ScalaCodeRunner("Main", Nil),
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
            ScalaCodeRunner("Main", Nil),
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
            ScalaCodeRunner("multi.Main", Nil),
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
            ScalaCodeRunner("toto", Nil),
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
            ScalaCodeRunner(
              "Main",
              List(
                ScalaDependency("org.typelevel", "cats-core", "1.6.1")
              )
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
          ScalaCodeRunner("Main", Nil),
          "scala/security/external-process.sc" :: Nil,
          result =>
            IO {
              approver.verify(result.left.get)
              succeed
            }
        )
      }

      it("Should not be able to launch external processes") { approver =>
        testCodeRunner(
          ScalaCodeRunner("Main", Nil),
          "scala/security/external-process.sc" :: Nil,
          result =>
            IO {
              approver.verify(result.left.get)
              succeed
            }
        )
      }
    }
  }
}
