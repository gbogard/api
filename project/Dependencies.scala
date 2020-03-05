import sbt._

object Dependencies {
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
  val scalaMock = "org.scalamock" %% "scalamock" % "4.4.0"
  val approvals = "com.github.writethemfirst" % "approvals-java" % "0.10.0"
  val tracing = "com.colisweb" %% "scala-opentracing" % "0.1.0"
  val domain = "lambda" %% "domain" % "0.4.4"
  val library = "lambda" %% "course-library" % "0.1.2"
  val scalaCodeRunner = "lambda" %% "scala-runner" % "0.2.2"
  val commonsIO = "commons-io" % "commons-io" % "2.6"
  val postgresDriver = "postgresql" % "postgresql" % "9.1-901-1.jdbc4"

  object Doobie {
    val version = "0.8.8"
    val core = "org.tpolecat" %% "doobie-core" % version
    val postgres = "org.tpolecat" %% "doobie-postgres" % version
    val hikari = "org.tpolecat" %% "doobie-hikari" % version

    val all = Seq(core, hikari)
  }

  object PureConfig {
    private val version = "0.10.1"
    val core = "com.github.pureconfig" %% "pureconfig" % version
    val all: Seq[ModuleID] = Seq(core)
  }

  object Cats {
    val version = "2.0.0-M4"
    val core = "org.typelevel" %% "cats-core" % version
    val effect = "org.typelevel" %% "cats-effect" % version
    val all = Seq(core, effect)
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
    val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
    val all = Seq(logback, scalaLogging)
  }
}
