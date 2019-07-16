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
import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.io.FileUtils

case class ScalaCodeRunner(
    mainClass: String,
    dependencies: List[ScalaDependency] = Nil
) extends CodeRunner[IO]
    with StrictLogging {

  import ScalaCodeRunner._

  def run(files: List[File]): ProcessResult[IO] =
    wrapEitherInResource(
      createTemporaryFolder[IO](),
      (folder: File) => {
        for {
          dependencies <- fetchDependencies
          _ <- compileFiles(files, dependencies, folder)
          output <- run(folder, mainClass)
        } yield output
      }
    )

  private val fetchDependencies: EitherT[IO, String, Seq[File]] =
    if (dependencies.nonEmpty) {
      logger.debug(
        "Fetching scala dependencies using Coursier : {}",
        dependencies
      )
      val coursierIO =
        Fetch(cache)
          .addDependencies(dependencies.map(_.toCoursierDependency): _*)
          .io
      EitherT.right(coursierIO)
    } else EitherT.rightT(Nil)

  private def compileFiles(
      files: Seq[File],
      dependenciesClasses: Seq[File],
      destFolder: File
  ): ProcessResult[IO] = {
    val classPathFlag =
      if (dependenciesClasses.nonEmpty) {
        s"-cp ${dependenciesClasses.map(_.getAbsolutePath()).mkString(":")}"
      } else ""
    val destFolderStr = destFolder.getAbsolutePath()

    val moveFilesToFolder = IO {
      files.foreach { f =>
        FileUtils.copyFileToDirectory(f, destFolder, false)
        logger.debug(
          "Copied Scala source file {} to {}",
          f.getAbsolutePath(),
          destFolderStr
        )
      }
    }

    EitherT.right(moveFilesToFolder) flatMap { _ =>
      val cmd = s"scalac $classPathFlag -d $destFolderStr $destFolderStr/*.sc*"
      StringProcessLogger
        .run(Process(cmd))
        .leftMap(removePathFromCompilerOutput)
    }

  }

  private def removePathFromCompilerOutput(output: String): String = {
    val pathRegex = "\\/.*\\/".r
    pathRegex.replaceAllIn(output, "lambda/")
  }

  private def run(
      compiledClassesFolder: File,
      mainClass: String
  ): EitherT[IO, String, String] = {
    val cmd = s"scala -cp ${compiledClassesFolder.getAbsolutePath()} $mainClass"
    StringProcessLogger.run(Process(cmd))
  }

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
