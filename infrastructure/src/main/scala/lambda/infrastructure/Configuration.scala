package lambda.infrastructure

import pureconfig.generic.auto._
import cats.effect.Sync

case class Configuration(
  env: String, 
  scalaUtilsClassPath: String,
  apiUrl: String,
  temporaryFoldersBase: String,
  defaultCpusLimit: Float
 ) {
  val isDev = env == "development"
}

object Configuration {
  def load[F[_]: Sync]: F[Configuration] = Sync[F].delay(pureconfig.loadConfigOrThrow[Configuration])
}