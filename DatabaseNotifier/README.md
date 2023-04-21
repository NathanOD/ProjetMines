
# DatabaseNotifier

Ce module informe le module OrowanCalculator de l'insertion de ligne dans la base de données.

## Classes
### DatabaseNotifier
Cette classe permet de publier des événements aux auditeurs enregistrés et fournit une interface RPC (appel de procédure à distance) aux clients externes afin qu'ils reçoivent des notifications d'événements.
### DatabaseTrigger
Cette classe définit et redéfinit des méthodes du Trigger qui détecte s'il y a une nouvelle ligne dans la base de données.
### EventNotifierService
Cette classe vérifie si une nouvelle ligne a été entrée dans la base de données et créé un évènement si c'est le cas.
## Protocol Buffers
### EventNotifier
Le serveur se connecte à la classe DatabaseNotifier et renvoie un message vide s'il y a une nouvelle ligne dans la base de données.


# IMPORTANT: COMMENT L'INSTALLER

Pour installer le DatabaseNotifier, il faut que celui-ci soit trouvable par la base de données H2.
Pour cela, il faut dans un premier temps utiliser la base de données en mode Serveur
(soit en s'y connectant avec une URL de type "tcp://"). Ceci est primordial, car il permet un accès concurrent à la base
de données. 
Une fois cette configuration effective, il faudra trouver l'emplacement d'installation de H2 pour trouver dans quel dossier
est démarrée la base de données. Une fois ce dossier trouvé, il vous faudra y ajouter l'archive 
"Mines-x-ArcelorMittal_Orowan-1.0-DatabaseNotifier.jar". 

Par la suite, il faudra modifier la ligne de commande de lancement de la base de données.
La ligne initiale ressemble à:
```@java -cp "h2-2.1.214.jar;%H2DRIVERS%;%CLASSPATH%" org.h2.tools.Console %*```
Il faudra y ajouter le chemin d'accès à l'archive DatabaseNotifier, comme suit :
```@java -cp "h2-2.1.214.jar;Mines-x-ArcelorMittal_Orowan-1.0-DatabaseNotifier.jar;%H2DRIVERS%;%CLASSPATH%" org.h2.tools.Server %*```
Il faut aussi faire attention à modifier la classe lancée de "org.h2.tools.Console" à "org.h2.tools.Server" en fin de ligne.

Une fois ceci réalisé, la configuration de la base donnée est terminée seulement à conditions que le dossier d'installation 
de H2 ne soit pas protégé. Si H2 est installé dans ``C:\Program Files``, le programme java n'aura pas l'accès en écriture au dossier
et ne pourra pas générer la configuration nécessaire à l'exécution du DatabaseNotifier.
Dans ce cas présent, il faudra alors manuellement rajouter le fichier ``config.properties`` contenant la configuration suivante:
```properties
# Database notifier RPC Server
rpc-db-port=32764
```
Vous êtes ensuite libre de modifier ladite configuration pour utiliser le port qui vous convient.

Pour vérifier la bonne intégration de l'archive, il est nécessaire d'observer la console lors du lancement de la base de données.
Lors de la première connexion à la base, ces messages devraient s'afficher:
```
{HEURE} [INFO ] | DatabaseNotifier:68 > Configuration file path: {PATH}\config.properties
{HEURE} [INFO ] | Configuration:98 > The configuration is correct and loaded!
{HEURE} [INFO ] | OrowanRPCServer:38 > DatabaseNotifier RPC Server successfully started on port {PORT}.
```

Si le DatabaseNotifier est mal intégré, vous obtiendrez des erreurs lors de l'insertion de données de capteurs dans 
la base de données, et le système ne pourra pas fonctionner.