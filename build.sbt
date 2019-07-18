import Dependencies._

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "lambda"
ThisBuild / organizationName := "lambdacademy"

lazy val root = (project in file("."))
  .settings(
    name := "lambdacademy"
  )
  .aggregate(codeRunner, courses)

lazy val core = (project in file("core"))
  .settings(
    name := "core",
    libraryDependencies ++= Cats.all
  )

lazy val codeRunner = (project in file("code-runner"))
  .settings(
    name := "code-runner",
    libraryDependencies ++= Cats.all ++ Coursier.all ++ Log.all ++ Seq(
      commonsIO,
      scalate,
      scalaTest % Test,
      approvals % Test
    )
  )
  .dependsOn(core)

lazy val courses = (project in file("courses"))
  .settings(
    name := "courses",
    libraryDependencies ++= Cats.all
  )
  .dependsOn(core, codeRunner)
