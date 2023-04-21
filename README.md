# ProjetMines

Projet Mines x ArcelorMittal
Auteurs: Steven HENRY, Sidi SID'AHMED, Nathan ODIC.
Date de rendu: 20/04/2023

## Description Technique

Ce projet a été réalisé avec Java 14, JavaFX 19 et construit avec Gradle.
La base de données utilisée est H2 Server.

## Structure Globale

Le projet contient 5 modules :

- **CommonUtils**
    - Ce module contient des classes utiles et communes qui peuvent être utilisées par d'autres modules du projet permettant des actions telles que l'accès a la base de données et la configuration des serveurs.

- **DatabaseNotifier**
    - Ce module informe le module OrowanCalculator de l'insertion de ligne dans la base de données.

- **OrowanCalculator**
    - Ce module calcule la sortie d'Orowan dès qu'il reçoit une donnée à partir des paramètres d'entrée fournis. Il fait office de serveur pour les clients.

- **OrowanGui**
    - Ce module contient l'interface utilisateur graphique pour l'application.

- **OrowanInputSimulator**
    - Ce module simule les entrées de l'utilisateur pour tester l'application.
  
Veuillez consultez les README de chaque module pour plus de detail.

## Installation et Lancement

Pour installer et lancer le projet, suivez les étapes suivantes :

- Installer Java 14.
- Ce système fonctionne obligatoirement avec une base de données H2 Server que vous devrez installer.
Veuillez ensuite créer un fichier sur votre machine au format .mv.db qui constituera la base de données utilisée
lors de ce projet.
- Connectez-vous à votre base de données grâce à l'interface Web, ou bien en ligne de commande, puis exécutez le script SQL
de configuration de la base de donnée (database_generation.sql). Attention, certains utilisateurs sont préréglés dans ce script,
veuillez les modifier pour plus de sécurité.
- Installer le module DatabaseNotifier (Les étapes d'installation sont précisées dans le fichier DatabaseNotifier/README.md)
- Installer le module OrowanCalculator (déployer le jar dans l'environnement final)
- Pensez à configurer votre réseau pour faire en sorte d'autoriser les clients qui se connecteront sur le serveur. Le
module OrowanCalculator necéssite une plage de 5 ports d'affilée libres sur votre réseau ainsi qu'un autre port de
communication des évènements avec le DatabaseNotifier. La base de données peut rester en local sur cette machine car 
seuls les modules DatabaseNotifier, OrowanInputSimulator et OrowanCalculator y accèdent, mais jamais les clients de 
manière directe.
- Démarrer le serveur de calcul (OrowanCalculator) une première fois, pour qu'il génère la configuration nécessaire à son
fonctionnement.
- Configurer le serveur de calcul (régler le fichier ``config.properties`` généré à l'emplacement d'exécution du serveur
de calcul)
- Installer le client (module OrowanGUI) sur les appareils voulus. Plusieurs appareils peuvent se connecter simultanément 
sur le serveur de calcul et observer des données différentes. Pour l'installer, il suffit de posséder Java 14, de placer
dans un dossier les fichiers ``Mines-x-ArcelorMittal_Orowan-1.0-GUI.jar``, ``RUN-GUI.bat`` et le dossier ``javafx-sdk-19``.
- L'exécution du fichier ``RUN-GUI.bat`` devrait démarrer le client.
- Une page de configuration s'affiche, et plusieurs champs sont à spécifier. Le premier champs est l'adresse du serveur 
- de calcul. Le serveur de calcul est la pièce centrale du système car c'est par lui que toutes les actions de 
l'utilisateur passent. Ce champ est donc directement lié à la configuration précédemment effectuée du serveur de calcul.
Le second champs est le nom d'utilisateur au sein du système, et son mot de passe. Deux utilisateurs par défaut ont été 
définis: Utilisateur "engineer", mot de passe "engineer", possédant le rôle administrateur de PROCESS_ENGINEER, et 
l'utilisateur "worker", mot de passe "worker", possédent le rôle WORKER. Ces utilisateurs peuvent être connectés à plusieurs
endroits à la fois.
- Avant de pouvoir appuyer sur le bouton "Connexion", il est necéssaire d'avoir démarré le serveur de calcul, sans lequel
l'authentification échouera. Il faut aussi veiller à ce que le serveur de calcul soit bien connecté à la base de données.
- Une fois le client connecté, deux interfaces s'offrent à lui: celle du travailleur, et celle de l'administrateur.
L'interface administrateur n'est disponible que pour les utilisateurs ayant le rôle PROCESS_ENGINEER.
L'interface de visualisation des données est accessible par tous les utilisateurs.
- Après avoir rejoint l'interface Travailleur, le client est en attente de données.
- Pour obtenir des données à tracer, des données doivent être injectées dans la table INPUT_OROWAN (Reliée à STRIPS et 
STANDS) de la base de données. Pour se faire, le module OrowanInputSimulator permet à partir de données préenregistrées 
dans des fichiers, de simuler les entrées de données des machines. Ce module est à démarrer en lien avec la base de données.
La configuration du lien avec la base de données se fait également dans le fichier autogénéré (``config.properties``).
- Lorsque la configuration est correcte, des données sont ajoutées toutes les 200 ms, traitées par la serveur de calcul,
puis envoyées au client qui les tracera.
Bonne chance ☺

# Spécificités du système

Le système fonctionne de manière concurrente :
- Les machines peuvent insérer des données simultanément
- Plusieurs utilisateurs peuvent visualiser les données (seulement celles en temps réel) simultanément.

Si une modification de configuration est effectuée par un administrateur, les autres clients du système doivent simplement
appuyer sur le bouton ``Fichier`` puis ``Actualiser`` pour voir ces modifications.

Les stands affichés sur le client, sont ceux enregistrés dans la base de données STANDS. Il est possible d'en ajouter
manuellement, ou bien de laisser le simulateur le faire.

Les mots de passe sont hashés par l'algorithme SHA256, avec un sel de 16 octets pour garantir une sécurité des identifiants.

Dans la version actuelle, certaines actions ne sont pas encore disponibles :
- Les utilisateurs (WORKER et PROCESS_ENGINEER) ne peuvent pas modifier les mots de passe
- Les valeurs d'intervalle d'acceptabilité des données par Orowan ne sont pas encore modifiables par l'interface.
Cependant, modifier les intervalles au sein de la base de données (Table INPUT_DATA_RANGE) directement permettra à ces
modifications de prendre effet. Il faudra tout de même penser à redémarrer le serveur de calcul.
- La visualisation les séries temporelles des valeurs calculables ne sont pas encore implémentées.

Le reste des exigences du projet ont été respectées. 