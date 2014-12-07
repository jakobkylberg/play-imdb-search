name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

libraryDependencies += "org.apache.httpcomponents" % "fluent-hc" % "4.3.6"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.3.6"