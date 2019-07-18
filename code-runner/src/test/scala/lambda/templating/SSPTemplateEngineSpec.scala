package lambda.templating

import lambda.coderunner.TestUtils.Approbation
import lambda.coderunner.Utils
import org.apache.commons.io.FileUtils
import cats.effect._

class SSPTemplateEngineSpec extends Approbation {
  val engine = new SSPTemplateEngine[IO]

  describe("SSPTemplateEngine") {
    it("Should render the single page template") { approver =>
      (
        for {
          file <- Resource.liftF(
            Utils.readResource[IO]("templating/simple-template.ssp")
          )
          result <- engine.render(file)
          resultContent <- Resource.liftF(IO {
            FileUtils.readFileToString(result)
          })
        } yield resultContent
      ).use { result =>
          IO {
            approver.verify(result)
            succeed
          }
        }
        .unsafeToFuture()
    }

    it("Should render the single page template with data") { approver =>
      (
        for {
          file <- Resource.liftF(
            Utils.readResource[IO]("templating/template-with-arg.ssp")
          )
          result <- engine.render(file, Map("name" -> "Guillaume"))
          resultContent <- Resource.liftF(IO {
            FileUtils.readFileToString(result)
          })
        } yield resultContent
      ).use { result =>
          IO {
            approver.verify(result)
            succeed
          }
        }
        .unsafeToFuture()
    }

    it("Should render a page with another included page") { approver =>
      (
        for {
          file <- Resource.liftF(
            Utils.readResource[IO]("templating/includes/Main.ssp")
          )
          result <- engine.render(file)
          resultContent <- Resource.liftF(IO {
            FileUtils.readFileToString(result)
          })
        } yield resultContent
      ).use { result =>
          IO {
            approver.verify(result)
            succeed
          }
        }
        .unsafeToFuture()
    }
  }
}
