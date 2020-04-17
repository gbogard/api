package lambda.api.application

import cats.effect.IO
import lambda.api.domain.AuthenticationTokenVerifier
import lambda.domain.auth.User
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`WWW-Authenticate`
import org.http4s.util.CaseInsensitiveString
import org.http4s.{Challenge, Request, Response}

object Authentication extends Http4sDsl[IO] {

  def requireUser(req: Request[IO])(response: User => IO[Response[IO]])
    (implicit verifier: AuthenticationTokenVerifier[IO]): IO[Response[IO]] = {

    req.headers.get(CaseInsensitiveString("authorization")).map(_.value) match {
      case Some(Bearer(token)) =>
        verifier.verifyAndExtract(token).flatMap({
          case Some(user) => response(user)
          case None =>
            Unauthorized(wwwAuthenticate, "Auth token invalid or expired")
        })
      case None =>
        Unauthorized(wwwAuthenticate, "You must be authenticated to access this resource")
    }
  }


  private val wwwAuthenticate: `WWW-Authenticate` = `WWW-Authenticate`(
    Challenge("Bearer", "Access to the Lambdacademy API")
  )
  private val Bearer = "Bearer (.+)".r

}
