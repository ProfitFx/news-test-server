import akka.http.scaladsl.model.StatusCodes
import org.scalatest.{ FreeSpec, Matchers }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import api.HttpServer._

class AuthTest extends FreeSpec with Matchers with ScalatestRouteTest {

  "GetToken" in {
    Get("/v1/auth?login=admin&password=admin") ~> myRoutes ~> check {
      status shouldEqual StatusCodes.OK
      val resp = responseAs[TokenResponse]
      resp.login shouldEqual "admin"
      resp.token should have size 32
    }
  }
  "GetTokenFail" in {
    Get("/v1/auth?login=admin&password=admin123") ~> myRoutes ~> check {
      status shouldEqual StatusCodes.Forbidden
      val resp = responseAs[String]
      resp shouldEqual "Incorrect login or password"
    }
  }

  "Ping" in {
    Get("/v1/ping") ~> myRoutes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[String] shouldEqual "OK"
    }
  }
  //  "Ping1" in {
  //    Get("/v1/ping") ~> myRoutes ~> check {
  //      status shouldEqual StatusCodes.OK
  //      responseAs[String] shouldEqual "KO"
  //    }
  //  }

  //  "Simple test1" in {
  //
  //    Get() ~> route ~> check {
  //      status shouldEqual StatusCodes.OK
  //      responseAs[String] shouldEqual "Captain on the bridge!123"
  //
  //    }
  //  }

  //  val route =
  //    get {
  //      pathSingleSlash {
  //        complete {
  //          "Captain on the bridge!"
  //        }
  //      } ~
  //        path("ping") {
  //          complete("PONG!")
  //        }
  //    }
}

