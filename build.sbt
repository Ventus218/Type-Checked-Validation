val scala3Version = "3.5.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "type-checked-validation",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version
  )
