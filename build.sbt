name := "meetup-stream-workshop"

version := "0.1.0"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.17",
  "com.typesafe.akka" %% "akka-http"   % "10.1.5"
)
