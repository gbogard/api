import sbtghpackages.TokenSource.Environment
import Dependencies._
import com.typesafe.sbt.packager.archetypes.scripts.BashStartScriptPlugin.autoImport.bashScriptExtraDefines

ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "1.0.1"
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
    bashScriptExtraDefines += """addJava "-Dconfig.resource=application-prod.conf" """,
    bashScriptExtraDefines += """addJava "-Dlogger.resource=logback-prod.xml" """,
    dockerfile in docker := {
      val appDir: File = stage.value
      val targetDir = "/app"

      new Dockerfile {
        from("openjdk:13-alpine")
        copy(appDir, targetDir, chown = "daemon:daemon")
        expose(8080)
        entryPoint(s"$targetDir/bin/${executableScriptName.value}")
      }
    },
    imageNames in docker := Seq(version.value, "LATEST").map(
      version => ImageName(s"docker.pkg.github.com/${githubOwner.value}/${githubRepository.value}/lambda-api:$version")
    )
  ).enablePlugins(sbtdocker.DockerPlugin, AshScriptPlugin)
