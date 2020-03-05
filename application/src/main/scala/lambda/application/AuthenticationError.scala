package lambda.application

sealed trait AuthenticationError

object AuthenticationError {
  case object WrongPassword extends AuthenticationError
  case object UserNotFound extends AuthenticationError
}