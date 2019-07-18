import Dependencies._

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "lambda"
ThisBuild / organizationName := "lambdacademy"

lazy val root = (project in file("."))
  .settings(
    name := "lambdacademy"
  )
  .aggregate(codeRunner, domain)

lazy val codeRunner = (project in file("code-runner"))
  .settings(
    name := "code-runner",
    libraryDependencies ++= Cats.all ++ Coursier.all ++ Log.all ++ Seq(
      commonsIO,
      scalaTest % Test,
      approvals % Test
    )
  )

lazy val domain = (project in file("domain"))
  .settings(
    name := "domain",
    libraryDependencies ++= Cats.all
  )
