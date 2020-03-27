package lambda.api.infrastructure

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._

package object serialization extends Decoders with Encoders {
  implicit def jsonEntityDecoder[A: Decoder]: EntityDecoder[IO, A] = jsonOf[cats.effect.IO, A]
  implicit def jsonEntityEncoder[A: Encoder]: EntityEncoder[IO, A] = jsonEncoderOf[cats.effect.IO, A]
}
