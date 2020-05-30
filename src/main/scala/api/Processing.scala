package api

import java.security.MessageDigest
import java.time.Instant

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.StandardRoute
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.typesafe.config.{ Config, ConfigFactory }
import scala.collection.JavaConverters._

import scala.collection.mutable

trait Processing extends JsonSupport {

  val conf: Config = ConfigFactory.load()
  val port: Int = conf.getInt("server.port")
  val tokenTimeout: Int = conf.getInt("application.token.timeout")

  val cred: Map[String, String] = conf.getStringList("application.admins").asScala.map(x => {
    val s = x.split(";")
    s(0) -> s(1)
  }).toMap

  var tokens = Set.empty[TokenResponse]

  var rubrics = Set.empty[Rubric]
  val rb: mutable.Buffer[String] = conf.getStringList("application.rubrics").asScala
  rb.foreach(x => {
    val s = x.split(";")
    rubrics += Rubric(s(0).toInt, s(1), s(2))
  })

  var authors = Set.empty[Author]
  // authors += Author(5, "Name", "Fam", "Pos")
  val au: mutable.Buffer[String] = conf.getStringList("application.authors").asScala
  au.foreach(x => {
    val s = x.split(";")
    authors += Author(s(0).toInt, s(1), s(2), s(3))
  })

  var records = Set.empty[Record]

  def getMd5(str: String): String = {
    MessageDigest.getInstance("MD5").digest(str.getBytes).map(0xFF & _).map {
      "%02x".format(_)
    }.foldLeft("") {
      _ + _
    }
  }

  def checkToken(token: String): Boolean = {
    val tk = if (token.contains("Bearer ")) token.replace("Bearer ", "") else ""
    tokens.exists(x => {
      x.token == tk & x.expiredTime >= Instant.now().toString
    })
  }

  def getApiUser(token: String): String = {
    val tk = if (token.contains("Bearer ")) token.replace("Bearer ", "") else ""
    val s = tokens.find(_.token == tk)
    if (s.nonEmpty) s.get.login else null
  }

  def getTokenResponse(login: String, password: String): StandardRoute = {
    val s = cred.find(x => x._1 == login & x._2 == password)
    if (s.isEmpty) {
      complete(StatusCodes.Forbidden, "Incorrect login or password")
    } else {
      val token = getMd5(s"$login + ${System.currentTimeMillis()}")
      val tResp = TokenResponse(login, token, Instant.now().plusSeconds(tokenTimeout).toString)
      tokens += tResp
      complete(tResp)
    }
  }

  def postRecord(r: RecordToPost): StandardRoute = {
    if (!rubrics.exists(_.id == r.rubricId)) {
      complete(StatusCodes.BadRequest, s"Rubric with id ${r.rubricId} not found")
      // complete(StatusCodes.BadRequest, s"""{"Error message": "Rubric with id ${r.rubricId} not found"}""")
    } else if (!authors.exists(_.id == r.authorId)) {
      complete(StatusCodes.BadRequest, s"Author with id ${r.authorId} not found")
    } else {
      //val rec: Record = Record(java.util.UUID.randomUUID().toString, r.rubricId, r.authorId, r.title, r.content, statusList.head, getApiUser(token), Instant.now().toString, Instant.now().toString)
      val rec: Record = Record(java.util.UUID.randomUUID().toString, r.rubricId, r.authorId, r.title, r.content, statusList.head, Instant.now().toString, Instant.now().toString)
      records += rec
      complete(rec)
    }
  }

  def putRecord(r: RecordToPost, id: String): StandardRoute = {
    val frec = records.find(_.id == id.toString)
    if (!rubrics.exists(_.id == r.rubricId)) {
      complete(s"Rubric with id ${r.rubricId} not found")
    } else if (!authors.exists(_.id == r.authorId)) {
      complete(s"Author with id ${r.authorId} not found")
    } else {
      if (frec.isDefined) {
        records -= frec.get
        val rec: Record = frec.get.copy(rubricId = r.rubricId, authorId = r.authorId, title = r.title, content = r.content, modifiedDate = Instant.now().toString)
        records += rec
        complete(rec)
      } else {
        // val rec: Record = Record(id, r.rubricId, r.authorId, r.title, r.content, statusList.head, getApiUser(token), Instant.now().toString, Instant.now().toString)
        val rec: Record = Record(id, r.rubricId, r.authorId, r.title, r.content, statusList.head, Instant.now().toString, Instant.now().toString)
        records += rec
        complete(rec)
      }
    }
  }

  def updateRecordStatus(st: String, id: String): StandardRoute = {
    if (!statusList.contains(st)) {
      complete(StatusCodes.Conflict, s"Status parameter must be one of (${statusList.mkString(",")})")
    } else {
      val frec = records.find(_.id == id)
      if (frec.isEmpty) {
        complete(StatusCodes.NotFound, s"Record with id '$id' not found")
      } else {
        val newRec = frec.get.copy(status = st, modifiedDate = Instant.now().toString)
        records -= frec.get
        records += newRec
        complete(newRec)
      }
    }
  }

  def deleteRecord(id: String): StandardRoute = {
    val frec = records.find(_.id == id.toString)
    if (frec.isEmpty) {
      complete(StatusCodes.NotFound, s"Record with id '$id' not found")
    } else {
      records -= frec.get
      complete(s"Record with id '$id' deleted")
    }
  }

  //  def findByIdOrNotFound(id: Int, set: Set[Any]): StandardRoute = {
  //    if (rubrics.exists(_.id == id)) {
  //      complete(rubrics.find(_.id == id))
  //    }
  //    else complete(StatusCodes.NotFound, s"Rubric with id = $id not found")
  //  }
}
