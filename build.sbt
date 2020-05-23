name := "news-test-server"

scalaVersion := "2.12.5"

lazy val akkaHttpVersion = "10.1.12"

lazy val akkaVersion = "2.6.5"

resolvers += "Bartek's repo at Bintray" at "https://dl.bintray.com/btomala/maven"
lazy val myProject = (project in file(".")).enablePlugins(SbtTwirl)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,

  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "com.typesafe" % "config" % "1.3.2",
  "btomala" %% "akka-http-twirl" % "1.2.0",

  "ch.megard" %% "akka-http-cors" % "0.3.1"
)



//assemblyOutputPath in assembly := file(s"${baseDirectory.value}/../${name.value}.jar")

//lazy val akkaHttpVersion = "10.1.1"
//lazy val akkaVersion = "2.5.12"
//lazy val root = (project in file(".")).settings(
//  inThisBuild(List(
//    name := "akka-api-server",
//    scalaVersion := "2.12.5")),
//  libraryDependencies ++= Seq(
//    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
//    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
//    "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
//    "com.typesafe.akka" %% "akka-stream" % akkaVersion
//  ))
//

//https://github.com/nulab/akka-http-oauth2-provider/blob/master/src/test/scala/scalaoauth2/provider/OAuth2ProviderSpec.scala
//https://github.com/jw3/example-akka-oauth/blob/master/src/main/scala/com/github/jw3/oauth/Client.scala
//http://enear.github.io/2017/03/07/scalajs-react-part1/

/*To do
 Рубрики не трогаем (сделаем массив из 10 шт)
 Добавить создателя в новость
 Добавить комменты (Создание, редактирование, удаление)
(id, Record, user, text)
 Убрать Delete из record
Прибраться с кодами ошибок
*/