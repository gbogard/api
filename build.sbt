import Dependencies._

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "lambda"
ThisBuild / organizationName := "lambdacademy"
ThisBuild / resolvers += Resolver.bintrayRepo("colisweb", "maven")

lazy val root = (project in file("."))
  .settings(
    name := "lambdacademy",
    reStart := (reStart in infrastructure).evaluated
  )
  .settings(BuildConfiguration.devConfig)
  .aggregate(domain, infrastructure, application, library, scalaUtils)

/**
 * A project for domain models and interfaces
 */
lazy val domain = (project in file("back/domain"))
  .settings(
    name := "domain",
    libraryDependencies ++= Cats.all ++ Seq(
      tracing,
      approvals % Test,
      scalaTest % Test
    )
  )

/**
 * A project for business logic
 */
lazy val application = (project in file("back/application"))
  .settings(
    name := "application",
    libraryDependencies ++= Cats.all ++ Seq(
      tracing,
      approvals % Test,
      scalaTest % Test
    )
  )
  .dependsOn(domain)

/**
 * A project for implementations of persistence layer, gateway endpoints,
 * code runners etc.
 */
lazy val infrastructure = (project in file("back/infrastructure"))
  .settings(
    name := "infrastructure",
    mainClass in assembly := Some("lambda.infrastructure.gateway.Main"),
    assemblyJarName in assembly := "lambdacademy.jar",
    test in assembly := {},
    libraryDependencies ++= Cats.all
      ++ Log.all
      ++ Http4s.all
      ++ Circe.all
      ++ Coursier.all
      ++ Scala.all
      ++ PureConfig.all
      ++ Seq(
        scalate,
        commonsIO,
        tracing,
        approvals % Test,
        scalaTest % Test
      )
  )
  .dependsOn(domain, application, library, scalaUtils)

/**
 * A project for the actual course curriculum
 */
lazy val library = (project in file("back/library"))
  .settings(
    name := "library",
    libraryDependencies ++= Seq(
      scalaTest % Test
    )
  )
  .dependsOn(domain)

ThisBuild / scalacOptions ++= BuildConfiguration.scalacOptions