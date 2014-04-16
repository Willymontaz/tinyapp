tinyapp
=======

App pour le tech event "perf continue"


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
