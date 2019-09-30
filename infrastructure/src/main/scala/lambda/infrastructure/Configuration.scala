package lambda.infrastructure

import pureconfig.generic.auto._
import cats.effect.Sync

case class Configuration(
  env: String, 
  scalaUtilsClassPath: Configuration.DockerVolume,
  apiUrl: String,
  temporaryFoldersBase: Configuration.DockerVolume,
  defaultCpusLimit: Float,
  sharedFiles: Configuration.DockerVolume
 ) {
  val isDev = env == "development"
}

object Configuration {
  case class DockerVolume(hostPath: String, containerPath: String)

  def load[F[_]: Sync]: F[Configuration] = Sync[F].delay(pureconfig.loadConfigOrThrow[Configuration])
}