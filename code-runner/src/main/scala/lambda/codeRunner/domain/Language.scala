package lambda.coderunner.domain

sealed trait Language

object Language {

  case object Scala2 extends Language
  case object Clojure extends Language

}