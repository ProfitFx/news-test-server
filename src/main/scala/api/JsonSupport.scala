//https://github.com/spray/spray-json/blob/master/src/main/scala/spray/json/CollectionFormats.scala

package api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import DefaultJsonProtocol._

trait JsonSupport extends SprayJsonSupport {

  final case class TokenResponse(login: String, token: String, expiredTime: String)

  final case class Rubric(id: Int, title: String, name: String)

  final case class Author(id: Int, firstName: String, lastName: String, position: String) {
    assert(firstName.length <= 64, "firstName.length should be less or equal 64")
  }

  final case class RecordToPost(rubricId: Int, authorId: Int, title: String, content: String)

  //final case class Record(id: String, rubricId: Int, authorId: Int, title: String, content: String, status: String, creator: String, createTime: String, modifiedDate: String) {
  final case class Record(id: String, rubricId: Int, authorId: Int, title: String, content: String, status: String, createTime: String, modifiedDate: String) {

  }

  val statusList = List("created", "verified", "published")

  //final case class Records(items: Set[Record])

  implicit val tokenJsonFormat = jsonFormat3(TokenResponse)
  implicit val RubricJsonFormat = jsonFormat3(Rubric)
  implicit val RubricsJsonFormat = arrayFormat[Rubric]
  implicit val RecordToPostJsonFormat = jsonFormat4(RecordToPost)
  implicit val RecordJsonFormat = jsonFormat8(Record)
  //  implicit val RecordJsonFormat = jsonFormat9(Record)
  implicit val AuthorJsonFormat = jsonFormat4(Author)
  //  implicit val RecordsJsonFormat = jsonFormat1(Records)
  implicit val RecordsJsonFormat = arrayFormat[Record]

}

//
//object JsonSupport {
//
//  final case class Author(id: Int, firstName: String, lastName: String, position: String)
//
//}
