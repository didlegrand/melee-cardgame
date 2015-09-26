package core

/**
 * Game instance of a game being played
 * 
 * 'M�l�e'. Jeu de carte pour 2 joueurs de type 'duel'.
 * 
 * Le jeu repr�sente l'affrontement de 2 g�n�raux incarn�s par les joueurs. 
 * Les joueurs disposent de cartes repr�sentant des combattants ou des objets.
 * 
 * Les cartes sont dispos�es sur le champ de bataille sur 3 lignes.
 * Les combattants en 1�re ligne sont au contact des combattants de la 1�re ligne adverse.
 * Il y a 3 emplacements par ligne (ou 4 ou 5 ???). 
 * Un emplacement ne peut contenir qu'une carte.
 * En 1�re et 2�me ligne, les cartes sont normalement face visible. 
 * En r�serve, les cartes sont normalement face cach�e.
 * Un joueur gagne si
 *  - un de ses combattants franchit les 3 lignes de l'adversaire ou
 *  - si l'adversaire devrait piocher mais ne peux pas (plus de ressource). 
 * 
 * Chaque joueur a devant lui
 *  - une pile de pioche, pour le recrutement ou l'achat de mat�riel.
 *  - des cartes en main, obtenues par la pioche.
 *  - des jetons d'intention (une face pour le type d'action dite 'face cach�e', une face pour l'action dite 'face visible')
 *  - 3 lignes de champ de bataille not�es
 *  	- "1" pour la 1�re ligne
 *  	- "2" pour la 2�me ligne
 *  	- "R" pour la ligne de r�serve.
 *  - une pile des cartes "hors combat" (combattants KO, objets cass�s, cartes d�fauss�es...)
 * 
 * A chaque tour, les joueurs jouent une succession de phases:
 * 	- recrutement (pioche)
 *  - mouvement (d�placement des combattants/objets)
 * 	- action (combat au corps � corps, capacit�s sp�ciales, utilisation des mat�riels)
 *  - r�cup�ration (les combattants en 2�me ligne reprennent des forces, les mat�riels qui le peuvent se rechargent).
 *  
 * Les joueurs ne jouent pas l'un apr�s l'autre mais en m�me temps:
 *  - les intentions d'actions (mouvements, attaques...) sont mat�rialis�es par des jetons pos�s face cach�e.
 *  - une fois que les joueurs ne veulent plus poser de jeton, les jetons sont r�v�l�s et les actions sont r�solues simultan�ment.
 * Une action aboutissant � une situation ill�gale est annul�e.
 *  
 * L'�tat des combattants ou des mat�riels varie pendant le tour:
 *  - sant�: diminue avec les coups re�us (= 0 -> hors combat)
 *  - fatigue: augmente avec les coups port�s ou les capacit�s utilis�es (= max -> �tat "�puis�")
 *  - force : mesure l'intensit� des coups port�s.
 *  - armure : vient en r�duction des coups re�us.
 *  
 * Phase de pioche:
 *  - les joueurs piochent une carte
 *  - ils peuvent choisir d'en piocher une 2�me mais devront en placer une des 2 sous la pile de pioche en fin de phase.
 *  - ils peuvent choisir d'en piocher une 3�me mais devront en plus en jeter une des 3 dans la d�fausse en fin de phase.
 *  
 * Phase de mouvement:
 *  - les mouvements concernent 
 *  	- les combattants ou les mat�riels sur le champ de bataille.
 *  	- les cartes en main qui peuvent arriver face cach�e sur la ligne de r�serve.
 *  - les mouvements se font vers l'avant, l'arri�re, la gauche ou la droite mais pas en diagonale.
 *  - il n'est pas possible de faire reculer un objet de la ligne de r�serve.
 *  
 *  - un objet en mouvement peut 
 *  	- aller vers un emplacement vide 
 *  	- �changer sa place avec un autre objet, par exemple un combattant de 1�re ligne �change sa place avec le combattant de 2�me ligne derri�re lui).
 *  
 *  - les mouvements autoris�s sont donc
 *  	- en avant: de r�serve en 2�me ligne ou de 2�me ligne en 1�re ligne.
 *  	- en arri�re : de la 1�re ligne � la 2�me ligne (pas de retour en r�serve)
 *  	- � droite ou � gauche en restant sur la m�me ligne
 *  	- depuis la main vers la ligne de r�serve face cach�e.
 *  
 *   Chaque intention de mouvement se mat�rialise par un jeton pos� face cach� sur l'objet � d�placer.
 *   
 *   Il est possible - et recommand� - de tromper l'adversaire en posant un jeton "sans mouvement" sur les objets qu'on n'a pas l'intention de d�placer.
 *   Tant qu'un joueur pose des jetons, son adversaire peut en poser aussi.
 *   
 *   On ne pose qu'un jeton mouvement par carte.
 *   Il est interdit d'enlever ou de changer un jeton pos�.
 *   
 *   Lorsque les 2 joueurs conviennent qu'ils ont fini de poser leurs jetons, on r�v�le les jetons en les retournant.
 *   Les mouvement sont ex�cut�s de gauche � droite, de la 1�re ligne � la r�serve.
 *   Les entr�es en r�serve depuis la main sont jou�es en dernier, toujours de gauche � droite.
 *   Si un mouvement est ill�gal, il est annul�.
 *   Les jetons mouvement sont enlev�s.
 *   
 *   Cas des lignes enfonc�es:
 *   - une ligne est enfonc�e lorsque des combattants ennemis sont pr�sents sur une des lignes d'un joueur.
 *   - ces combattants ne se d�placent pas d'une ligne � l'autre pendant la phase de mouvement.
 *   - ils pourront avancer ou reculer pendant la phase "action" (charge ou retraite).
 *  
 * Phase d'action:
 * 	Les joueurs posent des jetons action face cach�e sur les cartes en ligne 1 et 2.
 *  On ne peut normalement poser qu'un jeton par carte.
 *  Les actions sont soit des actions de combat, soit des actions sp�ciales pr�vues sur la carte.
 *  
 *  Actions de combat:
 *  - Attaque : inflige des blessures � l'adversaire en face (seulement de ligne 1 � ligne 1). Co�te 1 Fatigue.
 *  - Parade : se prot�ge de l'attaque en face (seulement de ligne 1 � ligne 1). Co�te 1 Fatigue.
 *  - Charge : Attaque possible lorsque le combattant a enfonc� les lignes ennemies 
 *  	(attaque + mouvement vers l'avant si l'adversaire est KO).
 *  	La charge co�te 2 points de Fatigue.
 *  - Retraite : recul possible depuis les lignes ennemies (d'une ligne).
 *  	
 *  Actions sp�ciales:
 *   - comme indiqu� sur la carte.
 *   - les actions sp�ciales sont rep�r�es par un "S" suivi d'un num�ro, ex "S1"
 *   - Certaines actions fatiguent le combattant et comportent la mention "Fatigue X" o� X est le nombre de points de fatigue � ajouter au compteur courant.
 *   - exemples:
 *   	"S1: Vol. Fatigue 1. El�vez-vous dans les airs avec un cerf-volant. L'adversaire r�v�le les cartes de la ligne de r�serve"
 *   	"S1: Ravitaillement. Fatigue 1. R�duisez de 1 la fatigue du combattant devant vous."
 *   	"S2: Soins. Fatigue 1. R�duisez de 2 la fatigue du combattant devant vous."
 *   	"S1: Cannonade. Fatigue 2. Infligez 3 d�gats � l'ennemi situ� 2 lignes devant vous.
 *   	"S2: Cannonade. Fatigue 2. Infligez 2 d�gats � l'ennemi situ� 3 lignes devant vous.
 *   	"S1: Assassinat. Fatigue 5. Glissez vous sans �tre rep�r� dans les lignes ennemis et mettez hors-combat un combattant. Quittez le champ de bataille"  
 *   	"S2: Sabotage. Fatigue 5. Glissez vous sans �tre rep�r� dans les lignes ennemis et d�truisez un mat�riel. Quittez le champ de bataille"
 *   	"S1: R�paration. Fatigue 1. R�parez le mat�riel devant vous: sa fatigue diminue de 1."
 *   	
 *   
 * @author Legrand
 *
 */
class Game {
	
	Random rand
	List<Player> players
	
	Game() {
		rand = new Random()
	}
	
	Game(long seed) {
		rand = new Random(seed)
	}

}
