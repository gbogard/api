package lambda.domain.courses

import Page._

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

  case class CodePage(
    id: PageId,
    title: String,
    widgets: List[Widget],
    code: InteractiveCodeWidget
  ) extends Page
}