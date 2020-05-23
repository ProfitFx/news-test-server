package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{ parameters, pathPrefix }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object HttpServer extends App with UserRoutes {

  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  lazy val routes: Route = myRoutes

  //val port = 1234
  Http().bindAndHandle(routes, "0.0.0.0", port)
  println(s"Server online at http://localhost:$port/")
  Await.result(system.whenTerminated, Duration.Inf)
}
