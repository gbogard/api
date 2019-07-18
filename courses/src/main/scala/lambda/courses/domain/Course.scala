package lambda.courses.domain

case class Course(
  title: String,
  description: String,
  tags: List[String],
  pages: List[Page]
)