import sbtghpackages.TokenSource.Environment
import Dependencies._

ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "1.0.0"
ThisBuild / organization := "lambda"
ThisBuild / organizationName := "lambdacademy"
ThisBuild / resolvers += Resolver.bintrayRepo("colisweb", "maven")

githubOwner := "lambdacademy-dev"
resolvers += Resolver.githubPackages("lambdacademy-dev")
githubRepository := "api"
githubTokenSource :=  TokenSource.Environment("GITHUB_TOKEN") || TokenSource.GitConfig("github.token")

lazy val api = (project in file("."))
  .settings(
    name := "lambdacademy-api",
    libraryDependencies ++= Cats.all
      ++ Log.all
      ++ Http4s.all
      ++ Circe.all
      ++ PureConfig.all
      ++ Seq(
        jwks,
        jwt,
        domain,
        library,
        tracing,
        scalaCodeRunner,
        programExecutor,
        commonsIO,
        approvals % Test,
        scalaTest % Test,
        scalaMock % Test
      ),
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
      version => ImageName(s"docker.pkg.github.com/${githubOwner.value}/${githubRepository.value}/lambda-api:$version")
    )
  ).enablePlugins(DockerPlugin)
