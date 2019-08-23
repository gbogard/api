package lambda.infrastructure

import lambda.domain.MediaHandler
import lambda.domain.Media
import lambda.domain.Media.ClasspathResource

class MediaHandlerInterpreter(configuration: Configuration) extends MediaHandler {

  def toUrl(media: Media): String = media match {
    case ClasspathResource(path) => configuration.apiUrl + "/resource" + path
  }
}