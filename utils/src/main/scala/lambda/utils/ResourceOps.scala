package lambda.utils

import cats.data.EitherT
import cats.effect.{Bracket, Resource}

trait ResourceOps {
  implicit class ResourceOps[F[_], A](self: Resource[F, A])(implicit br: Bracket[F, Throwable]) {
    def useEither[E, B](fn: A => EitherT[F, E, B]): EitherT[F, E, B] =
      EitherT(self.use(fn(_).value))
  }

}
