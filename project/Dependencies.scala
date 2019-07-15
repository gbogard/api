import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"

  object Cats {
    lazy val core = "org.typelevel" %% "cats-core" % "2.0.0-M4"
    lazy val effect = "org.typelevel" %% "cats-effect" % "2.0.0-M4"
    lazy val all = Seq(core, effect)
  }

  object Coursier {
    lazy val core = "io.get-coursier" %% "coursier" % "2.0.0-RC2-6"
    lazy val interop = "io.get-coursier" %% "coursier-cats-interop" % "2.0.0-RC2-6"
    lazy val all = Seq(core, interop)
  }
}
