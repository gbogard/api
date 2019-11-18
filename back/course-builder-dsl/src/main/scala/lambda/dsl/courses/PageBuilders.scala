package lambda.dsl.courses

import lambda.domain.courses.InteractiveCodeWidget
import lambda.domain.courses.Page.{CodePage, PageId, SimplePage}

trait PageBuilders {
  def simplePage(
      id: String,
      title: String
  ) = SimplePage(
    PageId(id),
    title,
    Nil
  )

  def simplePage(title: String): SimplePage = simplePage(slug(title), title)

  def codePage(
      id: String,
      title: String,
      code: InteractiveCodeWidget
  ) = CodePage(
    PageId(id),
    title,
    Nil,
    code
  )
}
