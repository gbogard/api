package lambda.api.domain

import lambda.domain.auth.User

trait AuthenticationTokenVerifier[F[_]] {

  def verifyAndExtract(token: String): F[Option[User]]
}
