name := "letter-shop-scala"

version := "0.1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaVer = "2.4.7"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVer withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-http-core" % akkaVer withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-http-experimental" % akkaVer withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVer withSources() withJavadoc()
  )
}
