package lambda.api.application

import cats.effect.{Blocker, ContextShift, IO}
import org.http4s.{HttpRoutes, Request, StaticFile}
import org.http4s.dsl.Http4sDsl

object MediaController extends Http4sDsl[IO] {

  def apply()(implicit blocker: Blocker, cs: ContextShift[IO]) = HttpRoutes.of[IO] {
    case req @ GET -> "resource" /: path if path.toList.contains("public") =>
      val filePath = "/" + path.toList.mkString("/")
      static(filePath, req)
  }

  private def static(file: String, request: Request[IO])(implicit blocker: Blocker, cs: ContextShift[IO]) =
    StaticFile.fromResource(file, blocker, Some(request)).getOrElseF(NotFound())
}
