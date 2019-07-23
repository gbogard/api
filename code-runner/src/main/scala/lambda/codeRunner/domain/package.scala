package lambda.coderunner

import cats.data.EitherT

package object domain {

  type ProcessResult[F[_]] = EitherT[F, String, String]

}