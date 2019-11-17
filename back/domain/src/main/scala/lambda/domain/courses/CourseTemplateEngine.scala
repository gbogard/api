package lambda.domain.courses

trait CourseTemplateEngine[F[_]] {
  def parse(templateString: String, idPrefix: String): F[List[Widget]]
}