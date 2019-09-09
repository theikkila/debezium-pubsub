import Dependencies._

ThisBuild / scalaVersion     := "2.13.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.emblica"
ThisBuild / organizationName := "emblica"


// https://mvnrepository.com/artifact/io.debezium/debezium-embedded
libraryDependencies += "io.debezium" % "debezium-embedded" % "0.9.5.Final"
// https://mvnrepository.com/artifact/io.debezium/debezium-connector-sqlserver
libraryDependencies += "io.debezium" % "debezium-connector-sqlserver" % "0.9.5.Final"


lazy val root = (project in file("."))
  .settings(
    name := "debezium-pubsub",
    libraryDependencies += scalaTest % Test
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
