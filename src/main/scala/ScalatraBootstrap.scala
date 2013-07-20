import in.rockyj.hello._
import org.scalatra._
import javax.servlet.ServletContext
import com.mongodb.casbah.Imports._

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    val mongoClient =  MongoClient()
    val mongoColl = mongoClient("casbah_test")("test_data")
    context.mount(new AppServlet(mongoColl), "/*")
  }
}
