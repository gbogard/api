package lambda.infrastructure

import cats.effect.Sync
import java.util.UUID.randomUUID

object Docker {

  /**
    * Given a list of paths, returns a Map of "host path" (the original path) to
    * "container path", a random path to which the path will be mounted in the container
    */
  def createVolumeNames[F[_]: Sync](paths: List[String], prefix: String): F[Map[String, String]] =
    Sync[F].delay {
      paths.map(hostPath => (hostPath, s"$prefix/${randomUUID().toString}")).toMap
    }

  def volumeNamesToFlags(volumes: Map[String, String]) =
    volumes.toList
      .map({
        case (hostName, containerName) => s"-v $hostName:$containerName"
      })
      .mkString(" ")
}
