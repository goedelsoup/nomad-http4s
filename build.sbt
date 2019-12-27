import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.linux.LinuxPlugin.autoImport._

import ReleaseTransformations._

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / organization := "compstak"

val CirceVersion = "0.12.2"
val Http4sVersion = "0.21.0-M6"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings"
)

lazy val core = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    name := "nomad-http4s",
    resolvers ++= Seq(
      "CompStak Releases".at("s3://compstak-maven.s3-us-east-1.amazonaws.com/releases"),
      "CompStak Snapshots".at("s3://compstak-maven.s3-us-east-1.amazonaws.com/snapshots")
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % CirceVersion,
      "org.http4s" %% "http4s-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("com.github.agourlay.cornichon.framework.CornichonFramework"),
    scalafmtOnCompile := true,
    initialCommands in console := """
                                    |import cats.implicits._
                                    |import nomad._
        """.stripMargin,
    publishTo := {
      val prefix = if (isSnapshot.value) "snapshots" else "releases"
      Some("CompStak".at(s"s3://compstak-maven.s3-us-east-1.amazonaws.com/$prefix"))
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ =>
      false
    }
  )
