package lambda.api.infrastructure

import cats.effect.Sync
import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class Configuration(
  apiUrl: String,
  tmpFolder: String,
  scalaRunnerHost: String,
  scalaRunnerPort: Int,
 )

object Configuration {

  def load[F[_]: Sync]: F[Configuration] = Sync[F].delay(ConfigSource.default.loadOrThrow[Configuration])
}