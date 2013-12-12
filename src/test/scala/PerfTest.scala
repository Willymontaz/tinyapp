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
         //Effectuer un get sur l'url "tinyapp/slowfast"
  
  //Injecter dans le scénario une montée progressive de 5 secondes puis un plateau de 2 minutes
  //Observer le résultat dans graphite et tenter de représenter le nombre de requêtes cumulées pour observer la montée progressive
  //Indice -> vous pouvez utiliser des fonction dans graphite pour effectuer des calculs sur les séries de données
  //http://graphite.readthedocs.org/en/latest/functions.html et chercher "integral"
  setUp(...)
  
}



