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
              .get("http://"+Params.host+"/tinyapp/autofailer"))
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(20 usersPerSec) during (2 minutes)))
  
  
}

class CPUConsumer extends Simulation{
  
  val scn = scenario("CPUConsumer")
            .exec(http("CPUConsumer")
              .get("http://"+Params.host+"/tinyapp/cpuconsumer"))
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(20 usersPerSec) during (2 minutes)))
  
  
}

class IOConsumer extends Simulation{
  
  val scn = scenario("IOConsumer")
            .exec(http("IOConsumer")
              .get("http://"+Params.host+"/tinyapp/ioconsumer"))
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(20 usersPerSec) during (2 minutes)))
  
  
}

class MemoryConsumer extends Simulation{
  
  val scn = scenario("MemoryConsumer")
            .exec(http("MemoryConsumer")
              .get("http://"+Params.host+"/tinyapp/memoryconsumer"))
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(20 usersPerSec) during (30 seconds)))
  
  
}

class SlowFast extends Simulation{
  
  val scn = scenario("SlowFast")
            .exec(http("SlowFast")
              .get("http://"+Params.host+"/tinyapp/slowfast"))
  
  setUp(scn.inject(ramp(10 users) over (5 seconds),
                 constantRate(20 usersPerSec) during (30 seconds)))
  
  
}

class ComplexScenario extends Simulation{
  
  
}

object Params {
  
  val host = System.getProperty("host", "localhost:8080")
  
}