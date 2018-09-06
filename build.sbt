import sbt.Keys.{libraryDependencies, _}

// Multi project build file.  For val xxx = project, xxx is the name of the project and base dir
// logging docs: http://doc.akka.io/docs/akka/2.4.16/scala/logging.html

lazy val commonSettings = Seq(
	organization := "org.sackfix",
	version := "0.1.1",
	scalaVersion := "2.12.6",

	libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime", // without %runtime did not work in intellij
	libraryDependencies += "org.sackfix" %% "sackfix-common" % "0.1.1" exclude("org.apache.logging.log4j","log4j-api") exclude("org.apache.logging.log4j","log4j-core"),
	libraryDependencies += "org.sackfix" %% "sackfix-messages-fix44" % "0.1.0" exclude("org.apache.logging.log4j","log4j-api") exclude("org.apache.logging.log4j","log4j-core"),
	libraryDependencies += "com.typesafe" % "config" % "1.3.3",
	libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.14",
	libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.14" % "test",
	libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.5.14",
	libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test",
	libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19"  % "test",
	libraryDependencies += "javax.ws.rs" % "javax.ws.rs-api" % "2.1" artifacts( Artifact("javax.ws.rs-api", "jar", "jar")),
)

lazy val sfsessioncommon = (project in file("./sf-session-common")).
  settings(commonSettings: _*).
  settings(
    name := "sf-session-commmon",
    libraryDependencies ++=Seq(
      "org.apache.kafka" % "kafka-streams" % "2.0.0" exclude("log4j","log4j-api") exclude("log4j","log4j-core"),
      "org.apache.avro" % "avro" % "1.8.2",
      "org.apache.avro" % "avro-compiler" % "1.8.2",
      "org.apache.avro" % "avro-ipc" % "1.8.2"
    ),
  )

lazy val sackfixsessions = (project in file(".")).aggregate(sfsessioncommon)
