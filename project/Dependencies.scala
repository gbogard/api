import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
  lazy val approvals = "com.github.writethemfirst" % "approvals-java" % "0.9.0"
  lazy val commonsIO = "commons-io" % "commons-io" % "2.6"
  lazy val scalate = "org.scalatra.scalate" %% "scalate-core" % "1.9.4"

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

  object Log {
    lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
    lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
    lazy val all = Seq(logback, scalaLogging)
  }
}
