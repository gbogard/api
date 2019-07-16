package lambda.coderunner

import org.scalatest._
import Utils._
import TestUtils._
import cats.effect.IO
import com.github.writethemfirst.approvals.approvers.Approver

class ScalaCodeRunnerSpec extends AsyncFunSpec with Matchers {
  val approver = new Approver()

  describe("The Scala Code Runner") {

    describe("Single file program") {

      describe("When the program compiles") {
        it("Should return the program output when it runs") {
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
        ) {
          testCodeRunner(
            ScalaCodeRunner("Main", Nil),
            "does-not-compile.sc" :: Nil,
            result =>
              IO {
                result shouldBe true
              }
          )
        }
      }

      describe("When the program does not compile") {
        it("It should return the compilation output") {
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
        it("Should return the program outputs when it runs") {
          testCodeRunner(
            ScalaCodeRunner("Main", Nil),
            List(
              "scala/multiple-files-program/Operations.scala",
              "scala/multiple-files-program/Main.scala",
            ),
            result =>
              IO {
                approver.verify(result.right.get)
                succeed
              }
          )
        }
      }

      describe("When the main class is not found") {}
    }

    describe("Program with external dependencies") {

      describe("When the program compiles") {}
    }
  }
}
