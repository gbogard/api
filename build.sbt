import Dependencies._

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "lambda"
ThisBuild / organizationName := "lambdacademy"
ThisBuild / resolvers += Resolver.bintrayRepo("colisweb", "maven")

lazy val root = (project in file("."))
  .settings(
    name := "lambdacademy"
  )
  .aggregate(domain, infrastructure, application, library, scalaUtils)

/**
 * A project for domain models and interfaces
 */
lazy val domain = (project in file("domain"))
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
lazy val application = (project in file("application"))
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
 * A library of utilities that will be added a a dependency for
 * all scala courses
 */
lazy val scalaUtils = (project in file("scala-utils"))
  .settings(
    name := "scalaUtils",
    libraryDependencies ++= Seq(
      pprint,
      scalaTest % Test
    )
  )

/**
 * A project for the actual course curriculum
 */
lazy val library = (project in file("library"))
  .settings(
    name := "library",
    libraryDependencies ++= Seq(
      scalaTest % Test
    )
  )
  .dependsOn(domain)

ThisBuild / scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-language:postfixOps", // Enable postfix notation
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xfuture", // Turn on future language features.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match", // Pattern match may not be typesafe.
  "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification", // Enable partial unification in type constructor inference
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
)