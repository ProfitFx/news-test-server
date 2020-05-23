import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import api.HttpServer._
import org.scalatest.{ FreeSpec, Matchers }

class AuthorTests extends FreeSpec with Matchers with ScalatestRouteTest {

  "GetToken" in {
    Get("/v1/auth?login=admin&password=admin") ~> myRoutes ~> check {
      status shouldEqual StatusCodes.OK
      val resp = responseAs[TokenResponse]
      resp.login shouldEqual "admin"
      resp.token should have size 32
    }
  }
}

