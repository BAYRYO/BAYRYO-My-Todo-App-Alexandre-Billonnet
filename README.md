# My Todo App Alexandre Billonnet
### Liste des fonctionnalités
## Application :
1.	Stockage des tâches : Utilisation d'une base de données SQLite (Room) pour stocker les données des tâches.
2.	Implémentation des thèmes : Prise en charge des thèmes clairs et sombres pour l'interface utilisateur de l'application.
3.	Personnalisation de l'icône de l'application : Choix d'une icône personnalisée pour l'application.
4.	Ajout d'une police d'écriture personnalisée : Utilisation d'une police d'écriture personnalisée pour le texte de l'application.
5.	Ajout d'icônes pour aider à la compréhension : Utilisation d'icônes pour améliorer la compréhension et la convivialité de l'application.
## MainActivity :
1.	Affichage des tâches : Affichage des tâches à partir d'un layout personnalisé (TaskLayout) dans une ListView.
2.	Filtrage des tâches : Possibilité de filtrer les tâches par état.
3.	Recherche des tâches : Recherche en temps réel des tâches par nom.
4.	Bouton d'ajout de tâche : Implémentation d'un bouton pour ajouter de nouvelles tâches.
5.	Mise à jour automatique des états : Système pour mettre à jour automatiquement les états des tâches (par exemple, de "À faire" à "En retard").
## TaskLayout :
1.	Affichage des données des tâches : Affichage des détails de chaque tâche, y compris le titre, la description, la deadline et l'état.
2.	Bouton d'édition de la tâche : Ajout d'un bouton pour permettre l'édition de la tâche.
## AddTaskLayout :
1.	Ajout d'une tâche : Formulaire pour ajouter une nouvelle tâche avec un titre, une description (optionnelle), une deadline (optionnelle) et un état.
2.	Gestion des erreurs : Affichage d'un message d'erreur si des informations obligatoires sont manquantes lors de l'ajout de la tâche.
3.	Ajout de la date avec un calendrier : Possibilité de choisir la date de la tâche à l'aide d'un calendrier.
4.	Choix de l'état de la tâche : Utilisation d'un menu déroulant pour choisir l'état de la tâche lors de son ajout.
5.	Bouton d'annulation : Implémentation d'un bouton pour annuler l'ajout de la tâche.
## EditTaskLayout :
1.	Récupération des données de la tâche : Récupération des données de la tâche à partir de la base de données en utilisant l'identifiant de la tâche.
2.	Suppression de la tâche : Possibilité de supprimer la tâche en cours d'édition à l'aide d'un bouton dédié.
3.	Initialisation du calendrier à la deadline : Affichage de la deadline actuelle de la tâche dans le calendrier lors de l'édition.
4.	Bouton d'annulation de l'édition : Implémentation d'un bouton pour annuler l'édition de la tâche.
5.	Gestion des erreurs : Affichage d'un message d'erreur si des informations obligatoires sont manquantes lors de l'édition de la tâche.
6.	Ajout d'une animation lorsqu'une tâche est finie.
