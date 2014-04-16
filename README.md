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

Jenkins:

Dans le build jenkins, le déploiement ne dépose pas le war au bon endroit.
Editer le job jenkins ```deploiement local``` et dans la partie ```execute shell``` remplacer par la commande:

    scp target/*.war devoxx@localhost:/home/devoxx/catalina_base/webapps/ && ssh devoxx@localhost ./restart.sh

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
    

Changer l'url de graphite:

    [[GraphiteHandler]]
    host=192.168.0.15 # mettre l'ip du serveur devoxx-ci
    port=2003
    

Redémarrer le service:

    $ sudo service diamond restart
