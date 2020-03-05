package lambda.application

import cats.Monad
import cats.data.EitherT
import lambda.domain.auth.{HashedPassword, PasswordHasher, User, UserRepository}
import lambda.domain.{Clock, IdGenerator}

class AuthenticationService[F[_]: Monad](
  implicit repository: UserRepository[F],
  pwd: PasswordHasher[F],
  ids: IdGenerator[F],
  clock: Clock[F]
) {

  def signUp(
    userName: String,
    password: String,
    emailAddress: String
  ): EitherT[F, SignUpError, User] =
    for {
      _ <- checkUserExistence(userName)
      id <- EitherT.liftF(ids.randomUserId())
      now <- EitherT.liftF(clock.getLocalTime())
      hashedPassword <- EitherT.liftF(pwd.hash(password))
      user = User(id, userName, emailAddress, now, now)
      _ <- EitherT.liftF(repository.persistUser(user, hashedPassword))
    } yield user

  def login(userName: String, password: String): EitherT[F, AuthenticationError, User] =
    for {
      userAndPassword <- EitherT.fromOptionF(repository.getUserByUserName(userName).value, AuthenticationError.UserNotFound)
      _ <- checkPassword(userAndPassword._2, password)
    } yield userAndPassword._1

  private def checkPassword(hashedPassword: HashedPassword, input: String): EitherT[F, AuthenticationError, Unit] =
    EitherT.right(pwd.check(input, hashedPassword)).flatMap({
      case true => EitherT.rightT(())
      case _ => EitherT.leftT(AuthenticationError.WrongPassword)
    })

  private def checkUserExistence(userName: String): EitherT[F, SignUpError, Unit] =
    EitherT.right(repository.getUserByUserName(userName).value).flatMap({
      case Some(_) => EitherT.leftT(SignUpError.UserAlreadyExists)
      case _ => EitherT.rightT(())
    })
}
