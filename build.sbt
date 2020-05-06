lazy val scala212 = "2.12.10"
lazy val scala213 = "2.13.2"
lazy val supportedScalaVersions = List(scala213, scala212)

import ReleaseTransformations._

ThisBuild / scalaVersion := scala212
ThisBuild / organization := "compstak"

val CirceVersion = "0.13.0"
val Http4sVersion = "0.21.0"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings"
)

credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "nexus.compstak.com",
  sys.env.get("NEXUS_USERNAME").getOrElse(""),
  sys.env.get("NEXUS_PASSWORD").getOrElse("")
)

lazy val core = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    name := "nomad-http4s",
    resolvers ++= Seq(
      "CompStak Nexus Releases".at("https://nexus.compstak.com/repository/maven-group")
    ),
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % CirceVersion,
      "org.http4s" %% "http4s-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.scalatest" %% "scalatest" % "3.1.0" % Test
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
      val suffix = if (isSnapshot.value) "snapshots" else "releases"
      Some("CompStak".at(s"https://nexus.compstak.com/repository/maven-$suffix"))
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ =>
      false
    }
  )
