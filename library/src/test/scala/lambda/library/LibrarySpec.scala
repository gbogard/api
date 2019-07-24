package lambda.library

import org.scalatest._

class LibrarySpec extends FunSpec with Matchers {
  describe("Courses library") {
    it("Should have unique courses ids") {
      courses.map(_.id).distinct shouldEqual courses.map(_.id)
    } 

    it("Should have unique widgets ids") {
      widgets.map(_.id).distinct shouldEqual widgets.map(_.id)
    }
  }
}