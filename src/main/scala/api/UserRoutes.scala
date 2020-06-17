package api

import java.security.MessageDigest
import java.time.Instant

import akka.actor.FSM.->
import akka.http.scaladsl.marshalling.{Marshaller, ToResponseMarshallable}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.ContentNegotiator.Alternative.ContentType
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.{Directive0, Directive1, Route, StandardRoute}
import spray.json.DefaultJsonProtocol._
import com.typesafe.config.{Config, ConfigFactory, ConfigObject}
import web.html._
import akka.http.scaladsl.model.headers._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings

import scala.collection.JavaConverters._
import scala.collection.mutable
//import org.scalatra._
//import org.scalatra.example.html.hello

trait UserRoutes extends Processing {

  // def complete[T: Marshaller](status: StatusCode, headers: Seq[HttpHeader], value: T): StandardRoute

  val ping: Route = pathPrefix("ping") {
    authorize(true) {
      complete("OK")
    }
  }

  val about: Route = pathPrefix("about") {
    authorize(true) {
      complete(s"Hi! I am test-news-server for research postman features. Current time: ${Instant.now().toString} ")
    }
  }

  val clear: Route = pathPrefix("clear") {
    authorize(true) {
      records = Set.empty[Record]
      tokens = Set.empty[TokenResponse]
      authors = Set.empty[Author]
      //      val au: mutable.Buffer[String] = conf.getStringList("application.authors").asScala
      //      au.foreach(x => {
      //        val s = x.split(";")
      //        authors += Author(s(0).toInt, s(1), s(2), s(3))
      //      })
      complete(s"Authors, records and token clean")
    }
  }

  val reset: Route = pathPrefix("reset") {
    authorize(true) {
      records = Set.empty[Record]
      tokens = Set.empty[TokenResponse]
      authors = Set.empty[Author]
      val au: mutable.Buffer[String] = conf.getStringList("application.authors").asScala
      au.foreach(x => {
        val s = x.split(";")
        authors += Author(s(0).toInt, s(1), s(2), s(3))
      })
      complete(s"Authors, records and token reseted")
    }
  }

  val web: Route =
    pathPrefix("main") {
      var s = main.render()
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s.body))
    } ~ pathPrefix("authors") {
      val alist: Set[List[String]] = for (a <- authors) yield List(a.id.toString, a.firstName, a.lastName, a.position)
      var s = table.render("Список авторов", List("Номер", "Имя", "Фамилия", "Должность"), alist)
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s.body))
    } ~ pathPrefix("rubrics") {
      val alist: Set[List[String]] = for (r <- rubrics) yield List(r.id.toString, r.name, r.title)
      var s = table.render("Список рубрик", List("Номер", "Название", "Описание"), alist)
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s.body))
    } ~ pathPrefix("users") {
      val alist: Set[List[String]] = (for (r <- cred) yield List(r._1, r._2)).toSet
      var s = table.render("Список логинов паролей", List("Логин", "Пароль"), alist)
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s.body))
    } ~ pathPrefix("records") {
      // val alist: Set[List[String]] = for (r <- records) yield List(r.id, r.rubricId.toString, r.authorId.toString, r.status, r.creator, r.createTime, r.modifiedDate, r.title, r.content)
      val alist: Set[List[String]] = for (r <- records) yield List(r.id, r.rubricId.toString, r.authorId.toString, r.status, r.createTime, r.modifiedDate, r.title, r.content)
      var s = table.render("Список новостей", List("Идентификатор", "Рубрика", "Автор", "Статус", "Пользователь", "Дата создания", "Дата изменения", "Заголовок", "Содержание"), alist)
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s.body))
    }


  val doc: Route = pathPrefix("doc") {
    getFromResource("redoc-static.html")
  }
  val st = CorsSettings.defaultSettings.withAllowGenericHttpRequests(true)
  val sw: Route =
    pathPrefix("swagger") {
      getFromResource("swagger2.json")
    }


  val routeAuth: Route = pathPrefix("auth") {
    get {
      pathEndOrSingleSlash {
        parameters('login, 'password) {
          (login, password) => getTokenResponse(login, password)
        }
      }
    }
  }


  val routeRubrics: Route = pathPrefix("rubric") {
    headerValueByName("Authorization") { token =>
      authorize(checkToken(token)) {
        concat(get {
          pathEndOrSingleSlash {
            parameters('sort ?) { sortOrder =>
              complete(
                if (sortOrder.getOrElse("") == "desc") rubrics.toSeq.sortWith(_.id > _.id)
                else if (sortOrder.getOrElse("") == "asc") rubrics.toSeq.sortWith(_.id < _.id)
                else rubrics)
            }
          }
        }, path(IntNumber) { id => {
          pathEndOrSingleSlash {
            val r = rubrics.find(_.id == id)
            if (r.nonEmpty) complete(r)
            else complete(StatusCodes.NotFound, s"Rubric with id = $id not found")
          }
        }
        })
      }
    }
  }

  val routeAuthors: Route = pathPrefix("author") {
    concat({
      pathEndOrSingleSlash {
        get {
          parameters('sort ?) { sortOrder =>
            complete(
              authors.toSeq.sortWith(_.id < _.id))
          }
        } ~
          put {
            //            headerValueByName("Authorization") { token =>
            //              authorize(checkToken(token)) {
            //            if (getApiUser(token) == "user") complete(StatusCodes.Forbidden, "Admin Required")
            //            else {
            entity(as[Author]) { r =>
              authors -= authors.find(_.id == r.id).orNull
              val author: Author = Author(r.id, r.firstName, r.lastName, r.position)
              authors += author
              complete(author)
            }
          }
        //    }
        //     }
        //   }
      }
    }, {
      path(IntNumber) { id =>
        pathEndOrSingleSlash {
          get {
            //            headerValueByName("Authorization") { token =>
            //              authorize(checkToken(token)) {
            val a = authors.find(_.id == id)
            if (a.nonEmpty) complete(a)
            else complete(StatusCodes.NotFound, s"Author with id = $id not found")
            //             }
            //           }
          } ~ delete {
            val a = authors.find(_.id == id)
            if (a.isEmpty) {
              complete(StatusCodes.NotFound, s"Author with id '$id' not found")
            } else {
              authors -= a.get
              complete(s"Author with id '$id' deleted")
            }
          }
        }
      }
    }
    )
  }

  val routeRecords: Route = pathPrefix("record") {
    //    headerValueByName("Authorization") { token =>
    //      authorize(checkToken(token)) {
    concat({
      pathEndOrSingleSlash {
        get {
          parameters('sort ?, 'rubricid.as[Int] ?, 'authorid.as[Int] ?, 'status ?) {
            (sortOrder, rubricId, authorId, status) => {
              var rcs = records
              if (rubricId.isDefined) {
                rcs = rcs.filter(_.rubricId == rubricId.get)
              }
              if (authorId.isDefined) {
                rcs = rcs.filter(_.authorId == authorId.get)
              }
              if (status.isDefined) {
                rcs = rcs.filter(_.status == status.get)
              }
              complete(
                if (sortOrder.getOrElse("") == "desc") rcs.toSeq.sortWith(_.createTime > _.createTime)
                else if (sortOrder.getOrElse("") == "asc") rcs.toSeq.sortWith(_.createTime < _.createTime)
                else rcs)
            }
          }
        } ~
          post {
            entity(as[RecordToPost]) { r =>
              postRecord(r)
            }
          }
      }
    }, {
      path(JavaUUID) { id =>
        pathEndOrSingleSlash {
          get {
            val rec = records.find(_.id == id.toString)
            if (rec.nonEmpty) complete(rec)
            else complete(StatusCodes.NotFound, s"Record with id = $id not found")
          } ~ put {
            entity(as[RecordToPost]) { r =>
              putRecord(r, id.toString)
            }
          } ~ patch {
            parameters('status) { st =>
              updateRecordStatus(st, id.toString)
            }
          } ~ delete {
            deleteRecord(id.toString)
          }
        }
      }
    })
    //    }
    //  }
  }

  val myRoutes: Route = cors(st) {
    doc ~ sw ~ web ~ pathPrefix("v1") {
      ping ~ about ~ clear ~ reset ~ routeAuth ~ routeRubrics ~ routeAuthors ~ routeRecords
    }
  }
}

