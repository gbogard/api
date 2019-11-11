Toto

----
scala:
  baseFiles:
    - toto
  defaultValue: >-
    object Foo {
      1 + 3
    }
  mainClass: MyMainClass
  dependencies:
    - org: cats
      name: cats
      version: version
      scalaVersion: "2.12"
----