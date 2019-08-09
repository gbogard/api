package lambda.infrastructure.code

import java.io.File

import cats.data.EitherT
import cats.effect._
import com.typesafe.scalalogging.StrictLogging
import coursier._
import coursier.cache._
import coursier.interop.cats._
import lambda.domain.code._
import lambda.domain.code.ScalaCodeRunner
import lambda.domain.code.ScalaCodeRunner._
import lambda.infrastructure.Utils._
import lambda.infrastructure.ExternalDependencies.Scala2

import scala.sys.process._
import scala.concurrent.duration._

object ScalaCodeRunnerImpl extends ScalaCodeRunner[IO] with StrictLogging {

  def run(
      files: List[File],
      mainClass: String,
      dependencies: List[ScalaDependency] = Nil,
      timeout: FiniteDuration = 30 seconds
  ): ProcessResult[IO] =
    wrapEitherInResource(
      createTemporaryFolder[IO](),
      (folder: File) => {
        for {
          dependencies <- fetchDependencies(dependencies)
          _ <- compileFiles(files, dependencies, folder)
          output <- withTimeout(run(folder, mainClass), timeout)
        } yield output
      }
    )

  private def fetchDependencies(dependencies: List[ScalaDependency]): EitherT[IO, String, Seq[File]] =
    if (dependencies.nonEmpty) {
      logger.debug(
        "Fetching scala dependencies using Coursier : {}",
        dependencies
      )
      val coursierIO =
        Fetch(cache)
          .addDependencies(dependencies.map(toCoursierDependency(_)): _*)
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

    val cmd = s"${Scala2.scalac} $classPathFlag -d $destFolderStr $target"
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
  ): EitherT[IO, String, String] = EitherT {
    Security.securityPolicyFile[IO] use { securityPolicyFile =>
      val securityPolicyFlag = s"-Djava.security.policy==${securityPolicyFile.getAbsolutePath()}"
      val cpFlag = s"-cp ${compiledClassesFolder.getAbsolutePath()}"
      val cmd = s"${Scala2.scala} $cpFlag ${Security.securityMangerFlag} $securityPolicyFlag $mainClass"
      StringProcessLogger.run(Process(cmd)).value
    }
  }

  implicit private val cs: ContextShift[IO] =
    IO.contextShift(scala.concurrent.ExecutionContext.global)
  implicit private val timer: Timer[IO] =
    IO.timer(scala.concurrent.ExecutionContext.global)

  private val cache = FileCache[IO]()

  private def toCoursierDependency(dep: ScalaDependency) = Dependency.of(
    Module(Organization(dep.org), ModuleName(s"${dep.name}_${dep.scalaVersion}")),
    dep.version
  )

  private object Security {
    val securityMangerFlag = "-Djava.security.manager"
    def securityPolicyFile[F[_]: Sync] =
      readResource[F]("/security/jvm-security.policy")
  }

}
