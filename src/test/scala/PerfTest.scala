import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._
import bootstrap._
import assertions._

class AutoFailer extends Simulation{
  
  val scn = scenario("AutoFailer")
            .exec(http("AutoFailRequest")
              .get(Params.URL+"/autofailer"))
  
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

class ComplexScenario extends Simulation{
  
  
}

object Params {
  
  val URL = "http://"+System.getProperty("host", "localhost")+":"+System.getProperty("port", "8080")+"/tinyapp"
  val durationMinutes = System.getProperty("durationMinutes", "2").toInt
  val usersPerSec = System.getProperty("usersPerSec", "20").toDouble
  
}