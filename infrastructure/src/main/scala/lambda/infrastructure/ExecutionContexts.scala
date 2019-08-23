package lambda.infrastructure

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import cats.effect._

object ExecutionContexts {

  val blockingEc = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))
  
  implicit val globalTimer: Timer[IO] = IO.timer(ExecutionContext.global)
  implicit val globalContextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
}