package lambda.coderunner.infrastructure

import java.io.File

import cats.data.EitherT
import cats.effect._
import com.typesafe.scalalogging.StrictLogging
import coursier._
import coursier.cache._
import coursier.interop.cats._
import lambda.coderunner.domain._
import CodeRunner._
import ScalaCodeRunner._
import Utils._
import org.apache.commons.io.FileUtils

import scala.sys.process._
import scala.concurrent.duration._

case class ScalaCodeRunner(
    mainClass: String,
    dependencies: List[ScalaDependency] = Nil
) extends CodeRunner[IO]
    with StrictLogging {

  import ScalaCodeRunner._

  def run(files: List[File], timeout: FiniteDuration = 30 seconds): ProcessResult[IO] =
    wrapEitherInResource(
      createTemporaryFolder[IO](),
      (folder: File) => {
        for {
          dependencies <- fetchDependencies
          _ <- compileFiles(files, dependencies, folder) 
          output <- withTimeout(run(folder, mainClass), timeout) 
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
    val target = files.map(_.getAbsoluteFile()).mkString(" ")

    val cmd = s"scalac $classPathFlag -d $destFolderStr $target"
    StringProcessLogger
      .run(Process(cmd))
      .leftMap(removePathFromCompilerOutput)

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
  implicit val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)
  implicit val timer: Timer[IO] = IO.timer(scala.concurrent.ExecutionContext.global)

  val cache = FileCache[IO]()

  case class ScalaDependency(
      org: String,
      name: String,
      version: String,
      scalaVersion: String = "2.12"
  ) {
    def toCoursierDependency = Dependency(
      Module(Organization(org), ModuleName(s"${name}_$scalaVersion")),
      version
    )
  }
}
