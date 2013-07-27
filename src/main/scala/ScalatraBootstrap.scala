import in.rockyj.hello._
import org.scalatra._
import javax.servlet.ServletContext
import com.mongodb.casbah.Imports._
import _root_.akka.actor.{ActorSystem, Props}

class ScalatraBootstrap extends LifeCycle {

  val system = ActorSystem("ScalatraSystem")
  
  override def init(context: ServletContext) {
    val mongoClient =  MongoClient()
    val mongoColl = mongoClient("casbah_test")("test_data")
    context.mount(new AppServlet(mongoColl, system), "/*")
  }
  
  override def destroy(context:ServletContext) {
    system.shutdown()
  }
  
}

