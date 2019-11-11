package lambda.infrastructure.code

import lambda.infrastructure.TestUtils.Approbation
import lambda.infrastructure.Utils
import org.apache.commons.io.FileUtils
import cats.effect._
import java.nio.charset.StandardCharsets

class TemplateEngineInterpreterSpec extends Approbation {
  val engine = new TemplateEngineInterpreter[IO]

  describe("TemplateEngineInterpreter") {
    it("Should render the single page template") { approver =>
      (
        for {
          file <- Utils.readResource[IO]("/templating/simple-template.ssp")
          result <- engine.render(file :: Nil)
          resultContent <- Resource.liftF(IO {
            FileUtils.readFileToString(result.head, StandardCharsets.UTF_8)
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
          file <- Utils.readResource[IO]("/templating/template-with-arg.ssp")
          result <- engine.render(file :: Nil, Map("name" -> "Guillaume"))
          resultContent <- Resource.liftF(IO {
            FileUtils.readFileToString(result.head, StandardCharsets.UTF_8)
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
          file <- Utils.readResource[IO]("/templating/includes/Main.ssp")
          result <- engine.render(file :: Nil)
          resultContent <- Resource.liftF(IO {
            FileUtils.readFileToString(result.head, StandardCharsets.UTF_8)
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
