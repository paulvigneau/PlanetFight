# Planet Fight

## Présentation du projet

> Ce projet a été réalisé lors de la troisième année de Licence Informatique à l'Université de Bordeaux. Il a été développé par Paul Vigneau et Nicolas Desclaux.

Ce jeu est réalisé en java 1.8 avec la bibliothèque graphique JavaFX. Il se compose de plusieurs classes :  
- Game : La classe principale du projet. C'est ici qu'est géré l'affichage ainsi que le déroulement du jeu.
- Universe : Cette classe correspond à l'univers. Il va regrouper tous les éléments présent dans l'espace ainsi qu'une partie de la gestion du jeu.
- Planet : Tout le traitement autour d'une planète (initialisation, changement d'état, production de vaisseaux...) se fait ici.
- Ship : Chaque vaisseau est un objet de la classe Ship. Sa création, le calcul du chemin qu'il doit emprunter, sa destruction, etc sont définis dans cette classe.
- Squadron : Les vaisseaux sont envoyés par escadron (groupes de vaisseaux). Pour cela, la classe Squadron contient une liste des vaisseaux qui composent un escadron.
- Player : Il s'agit d'un joueur. Il peut être commandé par une personne jouant au jeu ou par l'ordinateur avec un Intelligence Artificielle.
  
## Principe du jeu

Une partie se compose d'un univers. Dans cet univers sont présents plusieurs éléments : 
- Différentes planètes positionnées aléatoirement. Chaque joueur dispose au départ d'une planète source différenciable des autres par sa couleur. Les autres planètes (non contrôlées par un joueur) sont dites neutres.
- Seules les planètes appartenant à un joueur peuvent produire des vaisseaux, les autres ont un stock qui ne leur sert qu'à se défendre.
- Des vaisseaux de différents types qui partent d'une planète source et qui arrivent à une planète destination. Ces vaisseaux sont envoyés par chaque joueurs en fonction des réserves des planètes qu'il commande. Les vaisseaux  partent donc d'une planète et arrivent à une autre.
- Des zones libres pour laisser passer les vaisseaux.

Un vaisseau arrivant à destination peut avoir différentes conséquences. Si la planète sur laquelle il arrive appartient au même joueur que sa source, alors il s'agit d'un déplacement de troupes et le nombre de vaisseau de la planète est incrémenté. Si la planète est à un autre joueur, il s'agit d'une attaque kamikaze et le nombre de vaisseau de la planète destination est décrémenté.  
Si une planète se fait attaquer et que son nombre de vaisseau devient nul, la planète est alors conquise par le joueur attaquant. 
Le but du jeu est donc de conquérir toutes les planètes ennemies afin d'être le seul joueur à en posséder.

## Fonctionnalités du jeu

A la création de l'univers, plusieurs choses se passent.  
Tout d'abord, les joueurs sont créés avec une couleur qui les caractérisent les uns des autres.  
Ensuite, les planètes sont générées.
- Leur position, leur taille ainsi que leur nombre de vaisseaux sont créés aléatoirement. Cas particulier : chaque planète attribuée au commencement de la partie à un joueur contient 100 vaisseaux.
- Chaque joueur possède une seule planète au début de la partie.

Une fois l'univers créé est affiché, il est maintenant possible de jouer. La couleur du seul joueur humain est le bleu, et ça pour chaque partie.  

Il est possible d'envoyer un escadron de vaisseaux vers une autre planète. Pour cela, il suffit de cliquer sur la planète source (qui est contrôlée par le joueur en question) et de relâcher le clic sur la planète destination. Plusieurs vaisseaux sont donc créés tout autour de la planète et partent en direction de leur destination en prenant bien soin d'éviter les planètes sur leur chemin. Le nombre de vaisseaux envoyé dépend du pourcentage choisi (0-100).  
En effet un slider est présent sur le coté de la fenêtre de jeu et permet au joueur de choisir un pourcentage de vaisseaux à envoyer en fonction de la quantité que détient la planète. La valeur de ce slider peut être changée avec la souris, avec la molette ou encore avec les touches HAUT et BAS du clavier.  

Il existe 3 types de vaisseaux. Ces vaisseaux se différencient par leur forme, leur prix, leur puissance d'attaque ainsi que leur vitesse. Il est possible de changer de type à l'aide des touches de chiffres du clavier. Un descriptif des caractéristiques du type est fait sur la partie droite de la fenêtre, en dessous du slider.

Chaque seconde du jeu, toutes les planètes exceptées les neutres incrémentent de 1 leur quantité de vaisseau. Il est possible qu'une planète soit malade, et dans ce cas, sa production de vaisseau n'est pas constante.  

Lorsqu'un vaisseau part de sa planète mère, la quantité de cette dernière est décrémentée du prix du vaisseau.  

Lorsqu'un escadron est envoyé et tant que tous les vaisseax n'ont pas atteint leur destination, il est possible de rediriger ce dernier et de lui donner une autre planète cible.  
Pour cela, il faut maintenir le bouton CTRL enfoncé et à l'aide de la molette, on peut sélectionner l'escadron souhaité parmi ceux encore en présents. En gardant la touche CTRL enfoncée, on clique maintenant sur la nouvelle planète (qui peut être la planète mère) et la destination des vaisseaux encore présents dans l'escadron sera changée.  

Il est possible de changer rapidement le nombre de joueur d'une partie. Par defaut son nombre est de 2 (un humain et un ordinateur) mais des boutons "+" et "-" sont situé dans la partie option du jeu et permettent de d'ajouter ou de supprimer un joueur. Cela a aussi pour effet de relancer une nouvelle partie pour des questions d'égalité entre les joueurs.  
Le nombre de joueurs est compris entre 2 et le nombre de planètes

Dans un espace sur le coté de la fenêtre, en dessous du slider, se trouve un bouton "sauvegarder" qui permet l'enregistrement de la partie en cours.  
Il y a aussi un bouton "Charger" ayant pour rôle de rétablir une sauvegarde.  

Une partie se joue avec un joueur humain et des joueurs contrôlés par l'ordinateur.  
Nous avons donc mis en place une Intelligence Artificielle très naïve dans un premier temps permettant à un joueur non humain de sélectionner une de ses planètes et d'envoyer 50% de ses troupes sur une autre planète au hasard (exceptée la source). Un envoie est effectué par seconde uniquement si la planète choisie contient plus de 20 vaisseaux.

## Améliorations possibles

Voici une liste des améliorations envisageables pour la version améliorée :
- Ajout d'images et de textures pour rendre le jeu graphiquement plus joli.
- Permettre la sauvegarde de plusieurs parties.