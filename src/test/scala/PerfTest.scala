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
    + faire une requête gatling simple
    + fixer le nombre de requêtes par seconde
    + vérifier le statut 200

   Observations:
    + vérifier les temps de réponse sur le dashboard graphite
    + visualiser le dashboard gatling sur jenkins

   Exercice 1B
   Reproduire l'exercice 1A en simulant une charge progressive du nombre d'utilisateurs par seconde (de 0 à 20 utilisateurs/seconde sur 20 secondes)
   + vérifier les temps de réponse sur le dashboard graphite
   + visualiser le dashboard gatling sur jenkins

   Exercice 1C
   Reproduire l'exercice 1A en simulant un pic de 100 utilisateurs simultanés
   + vérifier les temps de réponse sur le dashboard graphite
   + visualiser le dashboard gatling sur jenkins

 */
class Exercice1 extends Simulation{
  
  val scn = scenario("ConstantRate")
            .exec(http("ConstantRate")
              .get(Params.URL+"/slowfast"))
  
  setUp(scn.inject(ramp(Params.users users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

/*
  Exercice 2:
  objectifs:
   + récupérer une valeur de la première requête et l’injecter dans la deuxième
   + vérifier que la réponse contient le mot « succès »

  observations:
   + la requête 2 échoue dans 50% des cas
   + le rapport gatling contient les deux temps de réponse

 */
class ComplexScenario extends Simulation {
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
    .exec(session => {
        http("Pi")
          .get(Params.URL+"/pi")
          .queryParam("digits", "${digits}")
          .check(regex(".*").count.is(session("digits").as[String].toInt -2))
        session
      }
    )

  setUp(scn.inject(ramp(Params.users users) over (5 seconds),
    constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
}

class Cpu extends Simulation{
  
  val scn = scenario("Cpu")
            .exec(http("Cpu")
                    .get(Params.URL+"/cpuconsumer")
                    .queryParam("digits", "15000")
            )
  
  setUp(scn.inject(ramp(Params.users users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
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
  Mixer les deux dans l'exercice 2
 */
class AutoFailer extends Simulation{
  
  val scn = scenario("AutoFailer")
            .exec(
                http("AutoFailRequest")
                	.get(Params.URL+"/autofailer")
                	.check(status.is(200))
            )
  
  setUp(scn.inject(ramp(Params.users users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

class AutoFailerWithout500 extends Simulation{
  
  val scn = scenario("AutoFailerWithout500")
            .exec(
                http("AutoFailRequest")
                	.get(Params.URL+"/autofailer")
                	.check(regex("Server Error").notExists)
              )
  
  setUp(scn.inject(ramp(Params.users users) over (5 seconds),
                 constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

/*
  Exercice 5:

  objectifs:
   + détecter un threadlock
   + corréler cpu et temps réponse
   + utiliser diamond

  observations:
   +


 */

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
		              .get(Params.URL+"/slowfast")
		              .queryParam("forename", "${forename}")
		              .queryParam("lastname", "${lastname}")
		              .check(regex("John").notExists)
		              .check(regex("Doe").notExists)
		         )
  
  setUp(scn.inject(ramp(Params.users users) over (5 seconds),
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