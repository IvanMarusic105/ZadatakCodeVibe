name := "CarApplication1"
 
version := "1.0" 
      
lazy val `carapplication1` = (project in file(".")).enablePlugins(PlayScala)

      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.13.5"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice)

//circe library
//pokušao sam puno primjeraka ali mi vračaju isti error u CarAdvertsController.scala linija 7
//https://www.baeldung.com/scala/circe-json
val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
//database plug-in

libraryDependencies += jdbc

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.25"
)
