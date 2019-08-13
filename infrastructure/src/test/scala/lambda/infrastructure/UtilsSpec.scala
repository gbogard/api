package lambda.infrastructure

import org.scalatest._
import cats.effect.IO
import org.apache.commons.io.FileUtils
import java.nio.charset.StandardCharsets

class UtilsSpec extends FunSpec with Matchers {
  describe("Utils") {
    describe("readResource") {
      it("Should read a resource located on the classpath of the project") {
        (Utils.readResource[IO]("/sample-resource.txt") use { resource =>
          IO {
            FileUtils.readFileToString(resource, StandardCharsets.UTF_8) shouldBe "Hello There"
          }
        }).unsafeRunSync()
      }
    }
  }
}
