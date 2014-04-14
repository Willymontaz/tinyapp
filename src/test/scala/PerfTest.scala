import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._
import bootstrap._
import assertions._
import scala.util.Random
import scala.xml.{Node, XML, Elem}

/*
   Exercice 1A
   Objectifs:
    + Créer une requête gatling simple
    + Utiliser un nombre fixe de requêtes par seconde
    + Vérifier un statut http
    + Consulter les temps de réponse sur le dashboard graphite
    + Visualiser le dashboard gatling sur jenkins

    Le service à tester est une servlet ayant un temps de réponse compris entre 20ms et 200ms.
    Dans cet exercice, il faut créer un scénario gatling simulant 20 users par secondes (taux constant)
    La servlet est exposée sur /tinyservlet

   Exercice 1B
    Reproduire l'exercice 1A en simulant une charge progressive du
    nombre d'utilisateurs par seconde (de 0 à 20 utilisateurs/seconde sur 20 secondes)
    puis un plateau à taux constant

   Exercice 1C
    Reproduire l'exercice 1A en simulant une charge progressive amenant à 20 utilisateur/seconde
    suivie immédiatement d'un pic de 100 utilisateurs simultanés puis enfin un plateau à 20 utilisateurs/seconde

    Pour cette dernière partie, on pourra appeler le service /autofail

   Exercice 1D
    Reproduire l'exercice 1C mais vérifier que le code de retour http est bien 200
    Que remarque-t-on ?

 */
class Exercice1 extends Simulation{
  
  val scn = scenario("Exercice1")
            .exec(http("ConstantRate")
                  .get(Params.URL+"/tinyservlet")
                  .check(status.is(200))
            )
  
  setUp(scn.inject(
          ramp(Params.users users) over (5 seconds),
          atOnce(100 users),
          constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)
        )
  )
  
  
}

/*
  Exercice 2:
  objectifs:
   + récupérer une valeur d'une première requête et l’injecter dans la deuxième
   + vérifier que la réponse contient le résultat attendu
   + vérifier que le rapport gatling contient les deux temps de réponse

   Dans cet exercice, nous chaînons deux appels pour simuler un scenario utilisateur
   La première requête s'effectue sur le service /precision, qui renvoie un nombre de décimale attendu pour le calcul de Pi
   Le body de la réponse a cette forme :
   <precision>
      <digits>XXXXXX</digits>
   </precision>

   La seconde requête appelle le service /pi avec le paramètre "digits" prenant la valeur issue de la premier requête

   Pour ce test, on utilisera un plateau simple à 1 utilisateur par seconde

 */
class Exercice2 extends Simulation {
  val scn = scenario("Exercice2")
    .exec(
      http("GetPrecision")
        .get(Params.URL+"/precision")
        .check(bodyString.saveAs("response"))
    )
    .exec(session => {
        val response = session("response").as[String]
        val xml = XML.loadString(response)
        val digits = (xml \\ "digits").head.text
        session.set("digits", digits)
      }
    )
    .exec(session => {
        http("Pi")
          .get(Params.URL+"/pi")
          .queryParam("digits", "${digits}")
          .check(regex(".*").count.is(session("digits").as[String].toInt -2))
        session
      }
    )

  setUp(constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes))
}

/*
  Exercice 3:
  Objectifs:
    + Détecter une fuite mémoire
    + Ajouter une métrique jmxtrans
    + Corriger la fuite mémoire
    + Manipuler les métriques graphite

  Dans cet exercice, il suffit simplement de requêter le service /memory
  Implémenter un tir similaire à l'exercice 1 et le lancer
  Que s'est-il passé ?

  La servlet MemoryServlet utilise TinyCache qui expose un MBean com.xebia.tinyapp:type=CacheInfosMBean
  Configurer JMXTrans pour exposer les données du cache sur graphite
  Relancer le tir et observer les résultats dans graphite

  Corriger la fuite mémoire, relancer le tir et observer le résultat dans graphite

  Observations:
    + visualiser dans graphite la fuite mémoire
    + comparer les deux tirs dans graphite, avant et après la fuite mémoire

 */

class Exercice3 extends Simulation{

  val scn = scenario("Exercice3")
            .exec(http("Memory")
              .get(Params.URL+"/memory")
              .check(status.is(200))
            )

  setUp(scn.inject(constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes))  )

}

/*
  Exercice 4
  objectifs:
   + détecter un problème de pool de connexions
   + ajouter une métrique (Metrics) pour le nombre de requêtes/seconde sur la base de donnée
   + afficher cette métrique dans graphite
   + resizer le pool et relancer le tir

  observations:
   + visualiser la métrique sur la base de donnée
   + comparer les deux tirs
 */
class Pool extends Simulation{

  val scn = scenario("Pool")
    .exec(http("Pool")
    .get(Params.URL+"/jdbcpool"))

  setUp(scn.inject(constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))

}


/*
 Exercice 5:
 Objectifs:
   + détecter un threadlock
   + corréler cpu et temps réponse
   + utiliser diamond

 */
class Cpu extends Simulation{
  
  val scn = scenario("Cpu")
            .exec(http("Cpu")
                    .get(Params.URL+"/pi")
                    .queryParam("digits", "15000")
            )
  
  setUp(scn.inject(ramp(Params.users users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))

}

/*
  Exercice 6
  Utilisation des feeders
 */
class CSVFeeder extends Simulation{
  
  val users = csv("users.csv").circular
  
  val scn = scenario("CSVFeeder")
		  	.feed(users)
            .exec(
            		http("SlowFast")
		              .get(Params.URL+"/people")
		              .queryParam("forename", "${forename}")
		              .queryParam("lastname", "${lastname}")
		         )
  
  setUp(constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
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
  
  setUp(scn.inject(ramp(Params.users users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

/*
  Exercice 7
  Utilisation des templates
 */



object Params {
  
  val URL = "http://"+System.getProperty("host", "localhost")+":"+System.getProperty("port", "8080")+"/"
  val durationMinutes = System.getProperty("durationMinutes", "2").toInt
  val usersPerSec = System.getProperty("usersPerSec", "20").toDouble
  val users = System.getProperty("usersPerSec", "20").toInt
  
}