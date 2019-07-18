package lambda.courses.domain

import Page._
import lambda.courses.domain.widgets._

sealed trait Page {
  def id: PageId
  def title: String
}

object Page {

  case class PageId(underlying: String) extends AnyVal

  case class SimplePage(
    id: PageId,
    title: String,
    widgets: List[Widget]
  ) extends Page

  case class CodePage[F[_]](
    id: PageId,
    title: String,
    widgets: List[Widget],
    code: InteractiveCodeWidget[F]
  )
}