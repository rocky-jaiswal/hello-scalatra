package in.rockyj.hello

//Scalatra
import org.scalatra._
import scalate.ScalateSupport
import org.scalatra.{FutureSupport, Accepted, ScalatraServlet}
import org.scalatra.json._
// MongoDb-specific imports
import com.mongodb.casbah.Imports._
// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}
//Akka
import _root_.akka.actor.{ActorRef, Actor, Props, ActorSystem}
import _root_.akka.util.Timeout
import _root_.akka.pattern.ask

case class Query(key: String, value: String, mongoColl: MongoCollection)

class AppServlet(mongoColl: MongoCollection, system:ActorSystem) extends MyScalatraWebAppStack with JacksonJsonSupport with FutureSupport {

  val myActor = system.actorOf(Props[MyActor])

  implicit val timeout = Timeout(10)
  protected implicit val jsonFormats: Formats = DefaultFormats
  protected implicit def executor = system.dispatcher
  
  get("/") {
    contentType="text/html"
    layoutTemplate("/WEB-INF/templates/views/index.jade")
  }
  
  /**
   * Insert a new object into the database:
   *curl -i -H "Accept: application/json" -X POST -d "key=super&value=duper" http://localhost:8080/insert
   */
  post("/insert") {
    val key = params("key")
    val value = params("value")
    val newObj = MongoDBObject(key -> value)
    mongoColl += newObj
  }
  
  /**
   * Query for the first object which matches the values given.
   * try http://localhost:8080/query/super/duper in your browser.
   */
  get("/query/:key/:value") {
    contentType = formats("json")
    val q = Query(params("key"), params("value"), mongoColl)
    myActor ? q
  }
  
}


class MyActor extends Actor {

  def receive = {
    case Query(key, value, mongoColl) => {
      val q = MongoDBObject(key -> value)
      val res = mongoColl.findOne(q).getOrElse(Map("not found" -> true))
      sender ! res
    }
  }

}
