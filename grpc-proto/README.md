# Package Proto

Ce dossier contient tous les fichiers `.proto` utilisés pour définir les services et les messages pour la communication via gRPC.

Les fichiers `.proto` définissent la structure et le format des données échangées entre le client et le serveur, ainsi que les méthodes disponibles pour effectuer ces échanges. Ils sont utilisés pour générer du code source qui permet de simplifier la communication entre les différentes parties de l'application.
Les fichiers `.proto` contenus dans ce package sont organisés en fonction des fonctionnalités qu'ils décrivent, pour faciliter la lecture et la maintenance du code. 


-**OrowanAuthenticator.proto**
Le fichier proto définit un service d'authentification des utilisateurs appelé OrowanAuthenticator. Ce service contient une méthode rpc nommée AuthenticateUser


-**UserManagement**
Ce fichier proto est utilisé pour gérer les utilisateurs d'un système. Il définit les messages et les services nécessaires pour créer, supprimer et mettre à jour les informations d'un utilisateur.

-**OrowanOperation**
Ce fichier proto définit un message "OrowanOperationResult" qui contient le résultat d'une opération. Il définit un énuméré "OperationResult" qui indique si l'opération a réussi ou a échoué.

-**OrowanLiveDataProvider**
Ce fichier proto est utilisé pour fournir des données en temps réel. Il définit plusieurs messages et services nécessaires pour fournir ces données.

-**StandManagement**
Ce fichier proto est utilisé pour gérer les emplacements de "stand".il définit plusieurs messages et services nécessaires pour permettre la gestion des stands.

-**InputRangeEditor**
Ce fichier proto est utilisé pour gérer les intervalles des valeurs.  






