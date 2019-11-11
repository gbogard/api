package lambda.library
import scala.io.Source

object Utils {
  def unsafeTextFromResource(resourceName: String) =
    Source.fromResource(resourceName).getLines().mkString("\r\n")
}