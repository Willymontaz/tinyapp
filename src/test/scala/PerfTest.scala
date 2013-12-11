import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._
import bootstrap._
import assertions._
import scala.util.Random
import scala.xml.{Node, XML, Elem}

class AutoFailer extends Simulation{
  
  val scn = scenario("AutoFailer")
            .exec(
                http("AutoFailRequest")
                	.get(Params.URL+"/autofailer")
                	.check(status.is(200))
            )
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class AutoFailerWithout500 extends Simulation{
  
  val scn = scenario("AutoFailerWithout500")
            .exec(
                http("AutoFailRequest")
                	.get(Params.URL+"/autofailer")
                	.check(regex("Server Error").notExists)
              )
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class CPUConsumer extends Simulation{
  
  val scn = scenario("CPUConsumer")
            .exec(http("CPUConsumer")
              .get(Params.URL+"/cpuconsumer"))
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class IOConsumer extends Simulation{
  
  val scn = scenario("IOConsumer")
            .exec(http("IOConsumer")
              .get(Params.URL+"/ioconsumer"))
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class MemoryConsumer extends Simulation{
  
  val scn = scenario("MemoryConsumer")
            .exec(http("MemoryConsumer")
              .get(Params.URL+"/memoryconsumer"))
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class SlowFast extends Simulation{
  
  val scn = scenario("SlowFast")
            .exec(http("SlowFast")
              .get(Params.URL+"/slowfast"))
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class SlowFastWithCSVFeeder extends Simulation{
  
  val users = csv("users.csv").circular
  
  val scn = scenario("SlowFastWithCSVFeeder")
		  	.feed(users)
            .exec(
            		http("SlowFast")
		              .get(Params.URL+"/slowfast")
		              .queryParam("forename", "${forename}")
		              .queryParam("lastname", "${lastname}")
		              .check(regex("John").notExists)
		              .check(regex("Doe").notExists)
		         )
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class SlowFastWithCustomFeeder extends Simulation{
  
  val speeds = new Feeder[String] {
	
	  private val RNG = new Random
	
	  override def hasNext = true
	
	  override def next: Map[String, String] = {
	    val speed = if(RNG.nextBoolean()) "slow" else "fast";
	    Map("speed" -> speed) 
	  }
  }
  
  val scn = scenario("SlowFastWithCSVFeeder")
		  	.feed(speeds)
            .exec(
            		http("SlowFast")
		              .get(Params.URL+"/slowfast")
		              .queryParam("speed", "${speed}")
		         )
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class ComplexScenario extends Simulation{
  val scn = scenario("SlowFastThenPi")
            .exec(
            		http("SlowFastCo")
		              .get(Params.URL+"/slowfast")
		              .check(bodyString.saveAs("response"))
		         )
		    .exec(session => {
		    		val response = session("response").as[String]
		    		val xml = XML.loadString(response)
		    		val digits = (xml \\ "digits").head.text
		    		session.set("digits", digits)
		    		}
		    	  )
		    .exec(
		    		http("Pi")
		    		  .get(Params.URL+"/cpuconsumer")
		    		  .queryParam("digits", "${digits}")
		    		  //Check number of digits
		         )
		         
	setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
}

object Params {
  
  val URL = "http://"+System.getProperty("host", "localhost")+":"+System.getProperty("port", "8080")+"/tinyapp"
  val durationMinutes = System.getProperty("durationMinutes", "2").toInt
  val usersPerSec = System.getProperty("usersPerSec", "20").toDouble
  
}