package lambda.infrastructure

import pureconfig.generic.auto._
import cats.effect.Sync

case class Configuration(
  env: String, 
  apiUrl: String,
  temporaryFoldersBase: Configuration.DockerVolume,
  defaultCpusLimit: Float,
  runners: Configuration.Runners,
  maxNumberOfDockerContainers: Int = 5,
 ) {
  val isDev = env == "development"
}

object Configuration {
  case class DockerVolume(hostPath: String, containerPath: String) {
    def toMap: Map[String, String] = Map[String, String](hostPath -> containerPath)
  }

  case class Runners(
     scala2: Runners.Scala2
  )

  object Runners {
    case class Scala2(
        path: DockerVolume
    ) {
      val dockerImage: String = "tindzk/seed:0.1.6"
    }
  }

  def load[F[_]: Sync]: F[Configuration] = Sync[F].delay(pureconfig.loadConfigOrThrow[Configuration])
}