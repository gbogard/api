package lambda.application

sealed trait SignUpError

object SignUpError {
  case object UserAlreadyExists extends SignUpError
}
