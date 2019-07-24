package lambda

import lambda.domain.courses.Page._
import lambda.domain.courses.widgets._

package object library {

  lazy val courses = List(
    ATourOfScala.course
  )

  lazy val widgets: List[Widget] = courses.flatMap(_.pages).flatMap {
    case p: SimplePage => p.widgets
    case p: CodePage   => p.widgets :+ p.code
  }

}