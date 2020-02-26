package lambda.infrastructure.code

import java.io.File
import java.nio.charset.StandardCharsets
import java.util.UUID

import cats.effect._
import lambda.domain.code._
import org.apache.commons.io.FileUtils

/**
  * This template engine replaces blocks of texts delimited by line-comments like so :
  *
  * ```
  * // [userInput
  * val a: Int = ???
  * // ]
  * ```
  *
  * with a Map containing ("userInput" -> "val a: Int = 42")
  * will be replaced by
  *
  * ```
  * val a: Int = 42
  * ```
  * This allows replacing portions of code files using user input, while providing a default implementation
  * for internal tests
  */
class TemplateEngineInterpreter[F[_]: Sync] extends TemplateEngine[F] {

  def canRender(file: File): Boolean = true

  def render(files: List[File], data: Map[String, Any] = Map.empty): Resource[F, List[File]] =
    files.map(renderSingleFile(_, data)).foldLeft(Resource.pure[F, List[File]](Nil)) {
      case (listResource, fileResource) =>
        listResource.flatMap(list => fileResource.map(file => list :+ file))
    }

  private val commentRegex = "//\\s*\\[(\\S*)\\s(?si)(.*)//\\]".r("name, content")

  private def renderSingleFile(
    file: File,
    data: Map[String, Any]
  ): Resource[F, File] = {
    val acquire = Sync[F] delay {
      val originalContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
      println(originalContent)
      val output = commentRegex.replaceAllIn(originalContent, m => data.getOrElse(m.group("name"), m.group("content")).toString)
      val tempFile = File.createTempFile(UUID.randomUUID().toString(), ".out")
      FileUtils.writeStringToFile(tempFile, output, StandardCharsets.UTF_8)
      tempFile
    }
    def release(f: File) = Sync[F] delay {
      FileUtils.forceDelete(f)
    }
    Resource.make(acquire)(release)
  }
}
