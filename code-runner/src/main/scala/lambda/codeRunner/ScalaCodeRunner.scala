package lambda.coderunner

import cats.effect._
import cats.data.EitherT
import cats.implicits._
import java.io.File
import coursier.cache._
import coursier._
import coursier.interop.cats._
import scala.sys.process._
import lambda.coderunner.ScalaCodeRunner._
import Utils._

case class ScalaCodeRunner(
    mainClass: String,
    dependencies: List[ScalaDependency] = Nil
) extends CodeRunner[IO] {

  import ScalaCodeRunner._

  def run(files: List[File]): ProcessResult[IO] = wrapEitherInResource(createTemporaryFolder[IO](), (folder: File) => {
    for {
      dependencies <- fetchDependencies
      _ <- compileFiles(files, dependencies, folder)
      output <- run(folder, mainClass)
    } yield output
  })

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
  ): ProcessResult[IO] = {
    val classPathStr = classPath.map(_.getAbsolutePath()).mkString(":")
    val destFolderStr = destFolder.getAbsolutePath()

    val compilerOutputs = files.toList.map(
      file =>
        StringProcessLogger.run(
          Process(
            s"scalac -cp $classPathStr -d $destFolderStr ${file.getAbsolutePath()}"
          )
        )
    )

    flattenProcessResults(compilerOutputs)
  }

  private def run(
    compiledClassesFolder: File,
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
