import scala.io.Source

object Main extends App {
  println(
    Source.fromFile("toto").getLines
  ) 
}