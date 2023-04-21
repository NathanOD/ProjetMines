
# OrowanGUI

Ce module contient l'interface utilisateur graphique pour l'application.

### OrowanGUI
Cette classe gère l'affichage des fenêtres du logiciel.

## Controller

### EngineerController
Cette classe fait le lien entres l'interface et les fonctions destinées à la gestion des utilisateurs.
### LoginController
Cette classe fait le lien entres l'interface et les fonctions destinées à la connexion des utilisateurs.
### OrowanController
Cette classe est le controller du MVC.
### SceneChoiceController
Cette classe fait le lien entres l'interface et les fonctions destinées à la connexion des utilisateurs en tant que *worker* ou *process engineer*.
### WorkerController
Cette classe fait le lien entres l'interface et les fonctions de l'interface du *worker*.

## RPC
### OrowanAuthenticatorClient
Cette classe permet au client de se connecter au logiciel.
### OrowanLiveDataClient
Cette classe permet au client de récupérer les données destinées aux courbes en direct.
### StandManagementClient
Cette classe permet au client de récupérer les données relatives aux différents stands.
### UserManagementClient
Cette classe permet au client de récupérer les données relatives aux différents utilisateurs.

## Util
### AuthenticatorTask
Tâche de fond JavaFX destinée à la gestion des connexions.
### CreateUserTask
Tâche de fond JavaFX destinée à la gestion de la création d'utilisateurs.
### DeleteUserTask
Tâche de fond JavaFX destinée à la gestion de la suppression d'utilisateurs.
### ListStandsTask
Tâche de fond JavaFX destinée à la gestion de la liste des stands.
### ListUsersTask
Tâche de fond JavaFX destinée à la gestion de la liste des utilisateurs.
### LiveDataTask
Tâche de fond JavaFX destinée à la gestion des données arrivant en direct destinées aux courbes.
### UpdateUserJobTask
Tâche de fond JavaFX destinée à la gestion des rôles des utilisateurs.
### UserManagementTask
Tâche de fond JavaFX destinée à la gestion des utilisateurs.