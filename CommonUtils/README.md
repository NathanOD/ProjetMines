# CommonUtils

## Description

Ce module contient des classes utiles et communes qui peuvent être utilisées par d'autres modules du projet permettant des actions telles que l'accès a la base de données et la configuration des serveurs.

Ce module contient trois configurations principales :
- **Configuration**
- **Database**
- **RPC**


## Package Configuration

Ce package permet de simplifier la gestion de configurations pour différents modules.

- **Class Configuration**
  La classe Configuration est un objet qui charge et valide un fichier de configuration externe (en dehors de l'archive jar). Elle prend en compte une configuration de fichier par défaut (dans l'archive jar) pour être utilisée en cas d'absence ou de mauvaise configuration de fichier de configuration. Cette classe est utile pour éviter la configuration en dur du code, ce qui permet de changer facilement la configuration en fonction de l'environnement d'exécution.

- **Class ConfigurationExpectation**
  Cette classe abstraite fournit une structure utile pour définir des attentes de configuration dans d'autres parties de l'application.

- **Class ConfigurationFilePathException**
  La classe ConfigurationFilePathException est une exception qui est levée lorsque le chemin du fichier de configuration est nul ou vide. Le message d'erreur généré par cette exception sera affiché pour informer l'utilisateur que le fichier de configuration ne peut pas être chargé en raison d'un chemin d'accès manquant ou invalide.

- **Class DoubleExpectation, StringExpectation, IntExpectation, ConcatenatedStringExpectation**
  Ces classes héritent de la classe ConfigurationExpectation.

## Package Database

Ce package permet d'établir les interactions avec la base de données. Il contient la classe suivante :
- **DatabaseConnection**
  Cette classe fournit une fonctionnalité pour se connecter à la base de données en utilisant l'API JDBC (Java Database Connectivity).

## Package RPC
- **OrowanRPCServer**
  Classe générique qui permet d'avoir un serveur RPC avec des méthodes pour lancer, arrêter et mettre en attente le serveur.











