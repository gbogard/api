package lambda.infrastructure

import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}
import cats.effect._

object ExecutionContexts {

  val blockingEc: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())
  
  implicit val globalTimer: Timer[IO] = IO.timer(ExecutionContext.global)
  implicit val globalContextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val codeRunnerEc: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(6))
}