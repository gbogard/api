package lambda.codeRunner

import cats.effect.IO
import cats.data.EitherT
import cats.implicits._
import java.io.File
import coursier.cache._
import coursier._
import coursier.interop.cats._
import scala.sys.process._
import lambda.codeRunner.ScalaCodeRunner._

case class ScalaCodeRunner(
    mainClass: String,
    dependencies: List[ScalaDependency] = Nil
) extends CodeRunner[IO] {

  import ScalaCodeRunner._

  def run(files: List[File]): EitherT[IO, String, String] = for {
    dependencies <- fetchDependencies
  } yield ""

  private val fetchDependencies: EitherT[IO, String, Seq[File]] = {
    val coursierIO =
      Fetch(cache)
        .addDependencies(dependencies.map(_.toCoursierDependency): _*)
        .io
    EitherT.right(coursierIO)
  }

  private def compileFiles(
      files: Seq[File],
      classPath: Seq[File],
      destFolder: File
  ): EitherT[IO, String, String] = {
    val classPathStr = classPath.map(_.getAbsolutePath()).mkString(":")
    val destFolderStr = destFolder.getAbsolutePath()

    val compilerOutputs = files.map(
      file =>
        StringProcessLogger.run(
          Process(
            s"scalac -cp $classPathStr -d $destFolderStr ${file.getAbsolutePath()}"
          )
        )
    )

    // TODO : change this
    compilerOutputs.head
  }

  private def run(
    classPathFolder: File,
    mainClass: String,
  ): EitherT[IO, String, String] = ???
}

object ScalaCodeRunner {
  implicit val cs = IO.contextShift(scala.concurrent.ExecutionContext.global)
  val cache = FileCache[IO]()

  case class ScalaDependency(
      org: String,
      name: String,
      version: String
  ) {
    def toCoursierDependency = Dependency(
      Module(Organization(org), ModuleName(name)),
      version
    )
  }
}
