tinyapp
=======

App pour le tech event "perf continue"


Mise à jour
===========

Avant de commencer, mettez à jour le repo git du projet:

    $ cd workspace/tinyapp
    $ git pull


Bugfix
=======

Jenkins
-------


Dans le build jenkins, le déploiement ne dépose pas le war au bon endroit.
Editer le job jenkins ```deploiement local``` et dans la partie ```execute shell``` remplacer par la commande:

    scp target/*.war devoxx@localhost:/home/devoxx/catalina_base/webapps/ && ssh devoxx@localhost ./restart.sh



Pour activer le plugin gatling pour jenkins, dans le job ```tir-en-local```, cliquer à la fin sur ```Add post-build action``` et choisisser ```Track a gatling load simulation```.


Tomcat
======

Le war du projet est déployé à l'aide de jenkins qui redémarre également le serveur.
Vous pouvez également le redémarrer manuellement:

    $ ./start.sh
    $ ./restart.sh

JMXTrans
========

Le service tourne déjà. Les fichiers de configuration json se trouve dans le projet ```tinyapp``` dans le répertoire ```src\test\resources```.

Le script ```add-to-jmxtrans.sh``` ajoute ces fichiers de conf et redémarre le service:

    $ cd
    $ ./add-to-jmxtrans.sh
    

Diamond
=======

Configuration:

    $ sudo vi /etc/diamond/diamond.conf
    

Changer (s'il y a besoin) l'url de graphite:

    [[GraphiteHandler]]
    host=localhost # mettre l'ip du serveur devoxx-ci
    port=2003

Changer également l'intervalle entre chaque mesure de 300 à 10s:    

    interval=10

Redémarrer le service:

    $ sudo service diamond restart


JMX/JConsole
============

Pour visualiser les mBean JMX disponibles, ça peut être util d'utiliser JConsole (ou mieux visualvm). On a ajouté un raccourci:

    $ cd
    $ ./jconsole.sh
   
