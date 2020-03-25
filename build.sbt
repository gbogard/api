import sbtghpackages.TokenSource.Environment
import Dependencies.{scalaMock, _}

ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "lambda"
ThisBuild / organizationName := "lambdacademy"
ThisBuild / resolvers += Resolver.bintrayRepo("colisweb", "maven")

ThisBuild / githubUser := sys.env.getOrElse("GITHUB_USER", "REPLACE_ME")
ThisBuild / githubOwner := "lambdacademy-dev"
ThisBuild / githubTokenSource := Some(Environment("GITHUB_TOKEN"))
ThisBuild / githubRepository := "api"

ThisBuild / resolvers ++= Seq("domain", "course-library", "scala-runner", "program-executor").map(
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
      scalaTest % Test,
      scalaMock % Test
    )
  )
  .dependsOn(commons)

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
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case "application.conf"                  => MergeStrategy.concat
      case _                                   => MergeStrategy.last
    },
    dockerfile in docker := {
      new Dockerfile {
        from("azul/zulu-openjdk-alpine:13")
        add(assembly.value, "/app/lambdacademy.jar")
        expose(8080)
        entryPoint(
          "java",
          "-Dconfig.resource=application-prod.conf",
          "-Dlogger.resource=prod-logback-prod.xml",
          "-jar",
          "/app/lambdacademy.jar"
        )
      }
    },
    imageNames in docker := Seq(version.value, "LATEST").map(
      version =>
        ImageName(s"docker.pkg.github.com/${githubOwner.value}/${githubRepository.value}/lambda-api:$version")
    ),
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
        programExecutor,
        commonsIO,
        approvals % Test,
        scalaTest % Test,
        scalaMock % Test
      )
  )
  .dependsOn(application, commons)
  .enablePlugins(DockerPlugin)

lazy val commons = project.settings(
  libraryDependencies ++= Cats.all
)

ThisBuild / scalacOptions ++= BuildConfiguration.scalacOptions
