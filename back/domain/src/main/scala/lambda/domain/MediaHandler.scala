package lambda.domain

trait MediaHandler {
  def toUrl(media: Media): String
}