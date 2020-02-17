import sbtghpackages.TokenSource.Environment
import Dependencies._

ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "lambda"
ThisBuild / organizationName := "lambdacademy"
ThisBuild / resolvers += Resolver.bintrayRepo("colisweb", "maven")

ThisBuild / githubUser := sys.env.getOrElse("GITHUB_USER", "REPLACE_ME")
ThisBuild / githubOwner := "lambdacademy-dev"
ThisBuild / githubTokenSource := Some(Environment("GITHUB_TOKEN"))
ThisBuild / githubRepository := "course-dsl"

ThisBuild / resolvers ++= Seq("domain", "course-library", "scala-runner").map(
  Resolver.githubPackagesRepo("lambdacademy-dev", _)
)

lazy val root = (project in file("."))
  .settings(
    name := "lambdacademy",
    reStart := (reStart in infrastructure).evaluated
  )
  .aggregate(infrastructure, application)

/**
  * A project for business logic
  */
lazy val application = (project in file("application"))
  .settings(
    name := "application",
    libraryDependencies ++= Cats.all ++ Seq(
      domain,
      tracing,
      approvals % Test,
      scalaTest % Test
    )
  )
  .dependsOn(utils)

/**
  * A project for implementations of persistence layer, gateway endpoints,
  * etc.
  */
lazy val infrastructure = (project in file("infrastructure"))
  .settings(
    name := "infrastructure",
    mainClass in assembly := Some("lambda.infrastructure.gateway.Main"),
    assemblyJarName in assembly := "lambdacademy.jar",
    test in assembly := {},
    libraryDependencies ++= Cats.all
      ++ Log.all
      ++ Http4s.all
      ++ Circe.all
      ++ PureConfig.all
      ++ Seq(
        domain,
        library,
        tracing,
        scalaCodeRunner,
        commonsIO,
        approvals % Test,
        scalaTest % Test
      )
  )
  .dependsOn(application, utils)

lazy val utils = project.settings(
  libraryDependencies ++= Cats.all
)

ThisBuild / scalacOptions ++= BuildConfiguration.scalacOptions
