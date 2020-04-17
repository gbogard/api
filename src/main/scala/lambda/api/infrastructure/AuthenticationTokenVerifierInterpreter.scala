package lambda.api.infrastructure

import java.util.concurrent.TimeUnit

import cats.implicits._
import cats.effect.IO
import com.auth0.jwk.JwkProviderBuilder
import com.typesafe.scalalogging.StrictLogging
import io.circe.Decoder
import lambda.api.domain.AuthenticationTokenVerifier
import lambda.domain.auth.User
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtOptions}

class AuthenticationTokenVerifierInterpreter(configuration: Configuration)
  extends AuthenticationTokenVerifier[IO] with StrictLogging {

  def verifyAndExtract(token: String): IO[Option[User]] = {
    (for {
      kid <- getKid(token)
      jwk <- IO(jwksProvider.get(kid))
      payload <- IO.fromTry(JwtCirce.decodeJson(
        token,
        jwk.getPublicKey,
        JwtAlgorithm.allRSA()
      ))
      _ <- IO(logger.debug("Successfully verified JWT Token"))
      user <- IO.fromEither(payload.as(payloadDecoder))
    } yield Option(user)).recover({
      case _ => None
    })
  }

  private lazy val payloadDecoder: Decoder[User] = json => {
    val claimsNamespace = "https://api.lambdacademy.dev"
    for {
      id <- json.downField(claimsNamespace).downField("user_id").as[String]
      nickname <- json.downField(claimsNamespace).downField("nickname").as[String]
      email <- json.downField(claimsNamespace).downField("email").as[String]
    } yield User(User.UserId(id), nickname, email)
  }

  private def getKid(token: String): IO[String] = IO {
    JwtCirce.decodeAll(token, JwtOptions(signature = false, expiration = false))
      .toOption
      .flatMap(_._1.keyId)
      .get
  }

  private val jwksProvider = new JwkProviderBuilder(configuration.jwksDomain)
    .cached(10, 1, TimeUnit.HOURS)
    .rateLimited(10, 1, TimeUnit.MINUTES)
    .build()

}
