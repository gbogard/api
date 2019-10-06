package lambda.library
import scala.io.Source
import scala.io.Codec

object Utils {
  def unsafeTextFromResource(resourceName: String) =
    Source.fromResource(resourceName)(Codec.UTF8).getLines().mkString("\r\n")
}