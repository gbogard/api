package lambda.infrastructure

import pureconfig.generic.auto._
import cats.effect.Sync

case class Configuration(
  apiUrl: String,
  tmpFolder: String,
  scalaRunnerHost: String,
  scalaRunnerPort: Int,
 )

object Configuration {

  def load[F[_]: Sync]: F[Configuration] = Sync[F].delay(pureconfig.loadConfigOrThrow[Configuration])
}