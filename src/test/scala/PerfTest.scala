import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._
import bootstrap._
import assertions._
import scala.util.Random
import scala.xml.{Node, XML, Elem}

class Exercice1A extends Simulation{
  
  val scn = scenario("Exercice1A")
            .exec(http("Exercice1A")
              .get(Params.URL+"/slowfast"))
  
  setUp(scn.inject(ramp(Params.usersPerSec users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class Exercice1B extends Simulation{
  
  val scn = scenario("Exercice1B")
            .exec(http("Exercice1B")
              .get(Params.URL+"/exercice1B"))
  
  setUp(scn.inject(ramp(Params.usersPerSec users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class Exercice1C extends Simulation{
  
  val scn = scenario("Exercice1C")
            .exec(http("Exercice1C")
              .get(Params.URL+"/exercice1C"))
  
  setUp(scn.inject(ramp(Params.usersPerSec users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class Exercice1D extends Simulation{
  
  val scn = scenario("Exercice1D")
            .exec(http("Exercice1D")
              .get(Params.URL+"/exercice1D"))
  
  setUp(scn.inject(ramp(Params.usersPerSec users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class AutoFailer extends Simulation{
  
  val scn = scenario("AutoFailer")
            .exec(
                http("AutoFailRequest")
                	.get(Params.URL+"/autofailer")
                	.check(status.is(200))
            )
  
  setUp(scn.inject(ramp(Params.usersPerSec users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class AutoFailerWithout500 extends Simulation{
  
  val scn = scenario("AutoFailerWithout500")
            .exec(
                http("AutoFailRequest")
                	.get(Params.URL+"/autofailer")
                	.check(regex("Server Error").notExists)
              )
  
  setUp(scn.inject(ramp(Params.usersPerSec users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class CSVFeeder extends Simulation{
  
  val users = csv("users.csv").circular
  
  val scn = scenario("CSVFeeder")
		  	.feed(users)
            .exec(
            		http("SlowFast")
		              .get(Params.URL+"/slowfast")
		              .queryParam("forename", "${forename}")
		              .queryParam("lastname", "${lastname}")
		              .check(regex("John").notExists)
		              .check(regex("Doe").notExists)
		         )
  
  setUp(scn.inject(ramp(Params.usersPerSec users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class CustomFeeder extends Simulation{
  
  val speeds = new Feeder[String] {
	
	  private val RNG = new Random
	
	  override def hasNext = true
	
	  override def next: Map[String, String] = {
	    val speed = if(RNG.nextBoolean()) "slow" else "fast";
	    Map("speed" -> speed) 
	  }
  }
  
  val scn = scenario("CSVFeeder")
		  	.feed(speeds)
            .exec(
            		http("SlowFast")
		              .get(Params.URL+"/slowfast")
		              .queryParam("speed", "${speed}")
		         )
  
  setUp(scn.inject(ramp(Params.usersPerSec users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class ComplexScenario extends Simulation{
  val scn = scenario("SlowFastThenPiDigits")
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
		    		  .get(Params.URL+"/pi")
		    		  .queryParam("digits", "${digits}")
		    		  //Check number of digits
		         )
		         
	setUp(scn.inject(ramp(Params.usersPerSec users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
}

object Params {
  
  val URL = "http://"+System.getProperty("host", "localhost")+":"+System.getProperty("port", "8080")+"/tinyapp"
  val durationMinutes = System.getProperty("durationMinutes", "2").toInt
  val usersPerSec = System.getProperty("usersPerSec", "20").toDouble
  
}