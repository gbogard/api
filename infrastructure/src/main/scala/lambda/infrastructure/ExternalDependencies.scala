package lambda.infrastructure
import java.nio.file.Paths

object ExternalDependencies {
  private val currentDirectory = new java.io.File(".").getCanonicalFile()

  object Scala2 {
    val scalac = Paths.get(currentDirectory.getAbsolutePath(), "deps", "scala-2.12.8", "bin", "scalac")
    val scala = Paths.get(currentDirectory.getAbsolutePath(), "deps", "scala-2.12.8", "bin", "scala")
  } 
}