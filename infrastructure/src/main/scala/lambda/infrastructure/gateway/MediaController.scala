package lambda.infrastructure.gateway

import cats.effect.{ContextShift, IO}
import org.http4s.dsl.io._
import org.http4s.{HttpRoutes, Request, StaticFile}

import scala.concurrent.ExecutionContext

object MediaController {

  def apply()(implicit ec: ExecutionContext, cs: ContextShift[IO]) = HttpRoutes.of[IO] {
    case req @ GET -> "resource" /: path if path.toList.contains("public") =>
      val filePath = "/" + path.toList.mkString("/")
      static(filePath, req)

  }

  private def static(file: String, request: Request[IO])(implicit ec: ExecutionContext, cs: ContextShift[IO]) =
    StaticFile.fromResource(file, ec, Some(request)).getOrElseF(NotFound())
}
