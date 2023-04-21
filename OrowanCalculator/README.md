# OrowanCalculator

Ce module calcule la sortie d'Orowan dès qu'il reçoit une donnée à partir des paramètres d'entrée fournis. Il fait office de serveur pour les clients.

### OrowanCalculator
Cette classe se connecte à la base de données et récupère une ligne afin de l'entrer dans Orowan et de renvoyer la sortie d'Orowan à la base de données.

## Database

### AuthenticatorRequests
Cette classe gère les requêtes de connexion à la base de données.
### CalculatorDatabaseFacade
Cette classe permet d'accéder à toutes les méthodes SQL implémentées pour les différents services.
### InputRangeRequests
Cette classe gère les requêtes vers la base de données concernant les valeurs limites des différents paramètres.
### OrowanDataRequests
Cette classe gère les requêtes vers la base de données visant à récupérer les données nécessaires à rentrer dans Orowan.
### StandRequests
Cette classe gère les requêtes vers la base de données concernant les valeurs selon les différents stands.
### UserManagingRequests
Cette classe gère les requêtes vers la base de données concernant la gestion des utilisateurs.

## Model
### OrowanDataOutput
Cette classe permet de récupérer les données de sortie d'Orowan.
### OrowanInputDataRange
Cette classe permet de vérifier que les données d'entrée d'Orowan appartiennent bien à l'intervalle de valeurs spécifié.
### OrowanSensorData
Cette classe permet de récupérer les données utiles fournies dans les capteurs afin de les entrer dans Orowan.

## RPC

### CalculatorServers
Cette classe lance le serveur de calcul d'Orowan.
### DatabaseNotifierClient
Cette classer indique au client si une ligne à été entrée dans la base de données.
### InputRangeEditorService
Cette classe permet à l'utilisateur de changer l'intervalle dans lequel les valeurs d'entrées doivent se trouver pour être entrées dans Orowan.
### OrowanAuthenticatorService
Cette classe permet au client de se connecter à Orowan.
### OrowanLiveDataService
Cette classe permet au client de recevoir les valeurs en direct pour qu'il puisse les afficher sur les courbes.
### StandManagementService
Cette classe permet de lister, d'ajouter et de supprimer des stands.
### UserManagementService
Cette classe permet de lister, d'ajouter, de supprimer et de changer le rôle des utilisateurs.
## Util
### DataFormatter
Cette classe permet de formater les données pour que Orowan les accepte.