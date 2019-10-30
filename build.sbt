import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.linux.LinuxPlugin.autoImport._

import ReleaseTransformations._

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / organization := "compstak"

val CatsRetryVersion = "1.0.0"
val CirceVersion = "0.12.2"
val CirisVersion = "0.13.0-RC1"
val Http4sVersion = "0.21.0-M5"
val Log4CatsVersion = "1.0.1"
val LogbackVersion = "1.2.3"
val MulesVersion = "0.3.0-M1"

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
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-refined" % CirceVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-prometheus-metrics" % Http4sVersion,
      "io.chrisdavenport" %% "log4cats-core" % Log4CatsVersion,
      "io.chrisdavenport" %% "log4cats-slf4j" % Log4CatsVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "ch.qos.logback" % "logback-core" % "1.2.3",
      "net.logstash.logback" % "logstash-logback-encoder" % "5.2",
      "is.cir" %% "ciris-cats" % CirisVersion,
      "is.cir" %% "ciris-cats-effect" % CirisVersion,
      "is.cir" %% "ciris-core" % CirisVersion,
      "is.cir" %% "ciris-refined" % CirisVersion,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % IntegrationTest
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0"),
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
