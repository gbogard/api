import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
  lazy val scalaMock = "org.scalamock" %% "scalamock" % "4.4.0"
  lazy val approvals = "com.github.writethemfirst" % "approvals-java" % "0.10.0"
  lazy val tracing = "com.colisweb" %% "scala-opentracing" % "0.1.0"
  lazy val domain = "lambda" %% "domain" % "0.4.4"
  lazy val library = "lambda" %% "course-library" % "0.1.2"
  lazy val scalaCodeRunner = "lambda" %% "scala-runner" % "0.2.2"
  lazy val commonsIO = "commons-io" % "commons-io" % "2.6"

  object PureConfig {
    private val version = "0.10.1"
    val core = "com.github.pureconfig" %% "pureconfig" % version
    val all: Seq[ModuleID] = Seq(core)
  }

  object Cats {
    lazy val version = "2.0.0-M4"
    lazy val core = "org.typelevel" %% "cats-core" % version
    lazy val effect = "org.typelevel" %% "cats-effect" % version
    lazy val all = Seq(core, effect)
  }

  object Http4s {
    private val version = "0.20.0"

    val dsl = "org.http4s" %% "http4s-dsl" % version
    val blazeServer = "org.http4s" %% "http4s-blaze-server" % version
    val blazeClient = "org.http4s" %% "http4s-blaze-client" % version
    val circeIntegration = "org.http4s" %% "http4s-circe" % version

    val all: Seq[ModuleID] = Seq(dsl, blazeServer, blazeClient, circeIntegration)
  }

  object Circe {
    private val version = "0.11.0"

    val core = "io.circe" %% "circe-core" % version
    val generic = "io.circe" %% "circe-generic" % version
    val genericExtras = "io.circe" %% "circe-generic-extras" % version
    val parser = "io.circe" %% "circe-parser" % version
    val optics = "io.circe" %% "circe-optics" % version
    val yaml = "io.circe" %% "circe-yaml" % "0.10.0"

    val all: Seq[ModuleID] = Seq(core, generic, genericExtras, parser, optics, yaml)
  }

  object Log {
    lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
    lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
    lazy val all = Seq(logback, scalaLogging)
  }
}
