package lambda.infrastructure

import java.nio.file.Paths

import pureconfig.generic.auto._
import cats.effect.Sync
import lambda.runners.scala.ScalaRunnerConfig

case class Configuration(
  apiUrl: String,
  tmpFolder: String
 ) {
  implicit val scala2RunnerConfig = ScalaRunnerConfig(Paths.get(tmpFolder))
}

object Configuration {

  def load[F[_]: Sync]: F[Configuration] = Sync[F].delay(pureconfig.loadConfigOrThrow[Configuration])
}