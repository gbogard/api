package lambda.infrastructure.code

import cats.effect.Concurrent
import cats.effect.concurrent.Semaphore
import lambda.infrastructure.Configuration

object Security {
  object Jvm {
    val securityManagerFlag = "-Djava.security.manager"
    def securityPolicyFileName = "jvm-security.policy"
  }

  object Docker {
    def permits[F[_]: Concurrent](conf: Configuration): F[Semaphore[F]] =
      Semaphore[F] (conf.maxNumberOfDockerContainers.toLong)
  }
}
