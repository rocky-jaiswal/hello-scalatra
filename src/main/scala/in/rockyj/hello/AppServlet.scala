package in.rockyj.hello

import org.scalatra._
import scalate.ScalateSupport
// MongoDb-specific imports
import com.mongodb.casbah.Imports._

class AppServlet(mongoColl: MongoCollection) extends MyScalatraWebAppStack {

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
    val q = MongoDBObject(params("key") -> params("value"))
    for ( x <- mongoColl.findOne(q) ) yield x
  }
  
}
