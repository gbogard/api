package lambda.core

import cats.effect.Sync
import java.io.File

object Utils {

  def readResource[F[_]: Sync](resourceName: String): F[File] = Sync[F].delay {
    new File(getClass().getClassLoader().getResource(resourceName).toURI())
  }
}