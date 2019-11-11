package lambda.domain

import cats.data.EitherT

package object code {

  type ProcessResult[F[_]] = EitherT[F, String, String]

}