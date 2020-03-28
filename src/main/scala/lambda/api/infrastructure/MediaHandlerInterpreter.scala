package lambda.api.infrastructure

import lambda.domain.Media.ClasspathResource
import lambda.domain.{Media, MediaHandler}

class MediaHandlerInterpreter(configuration: Configuration) extends MediaHandler {

  def toUrl(media: Media): String = media match {
    case ClasspathResource(path) => configuration.apiUrl + "/resource" + path
  }
}
