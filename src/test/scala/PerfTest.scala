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
            .get("http://webapp-3.aws.xebiatechevent.info:8080/tinyapp/slowfast"))    
  
  //Injecter dans le scénario une montée progressive de 5 secondes puis un plateau de 2 minutes
  //Observer le résultat dans graphite et tenter de représenter le nombre de requêtes cumulées pour observer la montée progressive
  //Indice -> vous pouvez utiliser des fonction dans graphite pour effectuer des calculs sur les séries de données
  //http://graphite.readthedocs.org/en/latest/functions.html et chercher "integral"
  setUp(scn.inject(ramp(20 users) over (5 seconds), constantRate(20 usersPerSec) during (2 minutes)))
  
}



