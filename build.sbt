lazy val scala212 = "2.12.10"
lazy val scala213 = "2.13.2"
lazy val supportedScalaVersions = List(scala213, scala212)

inThisBuild(
  List(
    scalaVersion := scala213,
    organization := "com.compstak",
    homepage := Some(url("https://github.com/compstak/nomad-http4s")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "LukaJCB",
        "Luka Jacobowitz",
        "luka.jacobowitz@gmail.com",
        url("https://github.com/LukaJCB")
      ),
      Developer(
        "goedelsoup",
        "Cory Parent",
        "goedelsoup@gmail.com",
        url("https://github.com/goedelsoup")
      )
    )
  )
)

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

addCommandAlias("fmtAll", ";scalafmt; test:scalafmt; scalafmtSbt")
addCommandAlias("fmtCheck", ";scalafmtCheck; test:scalafmtCheck; scalafmtSbtCheck")
addCommandAlias("validate", "fmtCheck; test")

lazy val core = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    name := "nomad-http4s",
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % CirceVersion,
      "org.http4s" %% "http4s-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.scalatest" %% "scalatest" % "3.1.0" % Test
    ),
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.0").cross(CrossVersion.full)),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("com.github.agourlay.cornichon.framework.CornichonFramework"),
    scalafmtOnCompile := true,
    initialCommands in console := """
                                    |import cats.implicits._
                                    |import nomad._
        """.stripMargin
  )
