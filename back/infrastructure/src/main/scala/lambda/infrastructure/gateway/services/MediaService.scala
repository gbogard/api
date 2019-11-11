package lambda.infrastructure.gateway.services

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import scala.concurrent.ExecutionContext

object MediaService {

  def apply()(implicit ec: ExecutionContext, cs: ContextShift[IO]) = HttpRoutes.of[IO] {
    case req @ GET -> "resource" /: path if path.toList.contains("public") => 
      val filePath = "/" + path.toList.mkString("/")
      static(filePath, req)

  }

  private def static(file: String, request: Request[IO])(implicit ec: ExecutionContext, cs: ContextShift[IO]) =
    StaticFile.fromResource(file, ec, Some(request)).getOrElseF(NotFound())
}