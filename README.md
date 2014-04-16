tinyapp
=======

App pour le tech event "perf continue"


Bugfix
=======

Jenkins:

Dans le build jenkins, le déploiement ne dépose pas le war au bon endroit.
Editer le job jenkins ```deploiement local``` et dans la partie ```execute shell``` remplacer par la commande:

    scp target/*.war devoxx@localhost:/home/devoxx/catalina_base/webapps/ && ssh devoxx@localhost ./restart.sh


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
