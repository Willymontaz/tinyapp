import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.core.session.Expression
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

  setUp(scn.inject(constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
}

/*
  Exercice 3:
  Objectifs:
    + Ajouter une métrique jmxtrans
    + Manipuler les métriques graphite

  Dans cet exercice, il suffit simplement de requêter le service /memory
  Implémenter un tir similaire à l'exercice 1 et le lancer
  Que s'est-il passé ?

  La servlet MemoryServlet utilise TinyCache qui expose un MBean com.xebia.tinyapp:type=CacheInfosMBean
  Configurer JMXTrans pour exposer les données du cache sur graphite (penser à exposer le port JMX du tomcat !)
  Relancer le tir et observer les résultats dans graphite

  Corriger la fuite mémoire, relancer le tir et observer le résultat dans graphite

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
   + ajouter une métrique (Metrics)
   + afficher cette métrique dans graphite

   Cet exercice fait appel au service /jdbcpool
   On peut réutiliser le même scénario que dans l'exercice précédent
   Nous allons chercher à observer l'impact de la taille d'un pool de connexion sur les performances
   Les résultats de l'exercice dépendent de votre machine, vous devrez donc trouver vous même les paramètres correspondant à votre configuration
   Le pool de connexion est initalement configuré avec une taille faible (10 connexions).

   En premier lieu, ajouter une configuration metrics pour observer la taille du pool de connexion jdbc et exposer
   cette métrique en JMX

   Essayer de lancer des tirs de 1 minute avec plusieurs valeurs différentes pour le nombre d'utilisateurs par seconde (plutôt grand -> 70/80 utilisateurs/sec)
   L'objectif est de trouver la limite atteinte avec ce paramètre pour le pool de connexion. (à titre indicatif
   sur un mac book pro récent on peut pousser jusqu'à 80 utilisateurs par seconde)
   Observer les résultats des tirs gatling et les corréler avec l'occupation du pool de thread. A la limite, vous devriez observer des temps de réponse
   élevés et beaucoup de requêtes en erreur, le pool de thread étant lui complètement rempli.

   Essayer ensuite de dimmensionner le pool avec une grande valeur (par exemple 200) et observer un nouveau tir.
   Que remarque-t-on ?

   Essayer de dimmensionner correctement le pool de thread pour obtenir (au 95ctile) de meilleurs performances en compromis avec le nombre d'erreur du service

 */
class Exercice4 extends Simulation{

  val scn = scenario("Pool")
    .exec(http("Pool")
    .get(Params.URL+"/jdbcpool"))

  setUp(scn.inject(constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))

}


/*
 Exercice 5:
 Objectifs:
   + corréler cpu et temps réponse
   + utiliser diamond

 Cet exercice fait appel au service Pi avec le paramètre digits à 8000 (à adapter selon la puissance de votre machine)
 Configurer Diamond pour afficher les métriques systèmes sur graphite
 Lancer un tir tel que dans l'exercice précédent avec 3 utilisateurs par seconde.
 Obersver les résultats du tir dans graphite et les corréler à l'occupation du CPU
 Que remarque-t-on ?

 Essayer de détecter le problème dans le code et le corriger
 Relancer le tir pour vérifier que votre corretion est efficace

 */
class Exercice5 extends Simulation{
  
  val scn = scenario("Cpu")
            .exec(http("Cpu")
                    .get(Params.URL+"/pi")
                    .queryParam("digits", "1000")
            )
  
  setUp(scn.inject(constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))

}

/*
  Exercice 6A
  Objectif : Utilisation des feeders

  Cet exercice se base sur le service /people
  Ce service prend en paramètre forename et lastname et sinon renvoi une erreur 500.

  Nous souhaitons réaliser un test à partir de données provenant d'un fichier csv. Pour cela, Gatling propose d'utiliser des "Feeders"
  Utiliser un feeder de type csv pour alimenter le service et essayer différent type de stratégies pour le feeder (circular, queue)


 */
class Exercice6A extends Simulation{
  
  val users = csv("users.csv").circular
  
  val scn = scenario("CSVFeeder")
		  	.feed(users)
            .exec(
            		http("People")
		              .get(Params.URL+"/people")
		              .queryParam("forename", "${forename}")
		              .queryParam("lastname", "${lastname}")
		         )
  
  setUp(scn.inject(constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

/*
  Exercice 6B
  Gatling permet également de créer ses propres feeders

  Cet exercice se base sur le service /slowfast qui prend en entrée l'argument "speed" avec la valeur slow ou fast
  Ecrire un feeder custom permettant de requêter le service aléatoirement avec la valeur "slow" ou "fast"

 */

class Exercice6B extends Simulation{
  
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
  
  setUp(scn.inject(constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))
  
  
}

/*
  Exercice 7
  Utilisation des templates

  Cet exercice se base sur le service /people qui expose la méthode POST et attend un body xml de la forme suivante :
  <person>
    <forename>John</forename>
    <lastname>Doe</lastname>
  </person>

  Créer un scénario gatling utilisant un template et y injecter les valeurs provenant du feeder csv de l'exercice 6

 */

class Exercice7 extends Simulation{

  val users = csv("users.csv").circular

  val template: Expression[String] = (session: Session) =>
    for {
      forename <- session("forename").validate[String]
      lastname <- session("lastname").validate[String]
    } yield s"""{
      <person>
        <forename>$forename</forename>
        <lastname>$lastname</lastname>
      </person>
    }"""

  val scn = scenario("CSVFeeder")
    .feed(users)
    .exec(
      http("People")
        .post(Params.URL+"/people")
        .body(StringBody(template))
    )

  setUp(scn.inject(constantRate(Params.usersPerSec usersPerSec) during (Params.durationMinutes minutes)))

}


object Params {
  
  val URL = "http://"+System.getProperty("host", "localhost")+":"+System.getProperty("port", "8080")+"/tinyapp"
  val durationMinutes = System.getProperty("durationMinutes", "2").toInt
  val usersPerSec = System.getProperty("usersPerSec", "20").toDouble
  val users = System.getProperty("usersPerSec", "20").toInt
  
}