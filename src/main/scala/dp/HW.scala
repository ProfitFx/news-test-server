package dp

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.{ Instant, LocalDateTime }

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.typesafe.config.ConfigFactory

//object HW extends App {
object HW {
  println("HW")

  //  val r = List(1, 2, 3, 5).foldLeft(1)((a, b) => a * b)
  //  println(r)
  //  List(1, 2, 3, 5).reverse
  //
  //  def mp[T](f: T => T, k: List[T]): List[T] = {
  //    k match {
  //      case Nil => Nil
  //      case h :: t => f(h) :: mp(f, t)
  //      //   case _ => _
  //    }
  //  }
  //
  //  def f(x: Int): Int = x + 100
  //
  //  println(mp(f, List(1, 2, 3)))

  //  val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
  //  val s = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
  //  //println(s)
  //  //println(Instant.now().toString)
  //
  //  val sl = List(1, 2, 3)
  //
  //  val resp = sl.lift(2).getOrElse(0)
  //  val resp1 = sl.lift(3).getOrElse(0)
  //  // println(resp)
  //  // println(resp1)
  //
  //  val conf = ConfigFactory.load()
  //  val port = conf.getInt("server.port")
  //  val tokenTimeout = conf.getInt("application.token.timeout")
  //
  //  val cr = conf.getStringList("application.admins").asScala
  //  val st = cr.map(x => {
  //    val s = x.split(";")
  //    s(0) -> s(1)
  //  }).toMap
  //
  //  println(st)
  //  st.foreach(x => println(x))
  //
  //  val cr1 = conf.getList("application.admins")
  //
  //  var cred = Map(
  //    "admin" -> "admin",
  //    "admin1" -> "admin1",
  //    "admin33" -> "admin33"
  //  )
  //
  //  println(cred)
  //  cred.foreach(x => println(x))
  //
  //  val i = Int.MaxValue
  //  println(i)
  //  //val cr: Array[String] = conf.getStringList("application.admins")
  //
  //  //
  //
  //  // val r = cr.map()

}