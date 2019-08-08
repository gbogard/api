package lambda.courseTemplateEngine.widgets

case class Question(title: String, answer: String, propositions: List[String], required: Option[Boolean])
