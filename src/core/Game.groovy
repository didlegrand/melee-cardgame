package core

/**
 * Game being played
 * 
 * 'M�l�e'. Jeu de carte pour 2 joueurs de type 'duel'.
 * 
 * Les joueurs disposent de cartes repr�sentant des combattants ou des objets.
 * 
 * Les cartes sont dispos�es sur le champ de bataille en 3 lignes: 1�re, 2�me et r�serve.
 * Il y a 5 emplacements par ligne. Un emplacement ne peut contenir qu'une carte.
 * En 1�re et 2�me ligne, les cartes sont normalement face visible. 
 * En r�serve, les cartes sont normalement face cach�e.
 * Un joueur gagne 
 * - si l'un de ses combattants franchit les 3 lignes ou
 * - si son adversaire devrait piocher et ne peux pas. 
 * 
 * Disposition du jeu: chaque joueur a
 *  - une pile de pioche pour le recrutement ou l'achat de mat�riel
 *  - une main avec les cartes obtenues par recrutement
 *  - des jetons d'action (face A pour le type d'action, face B pour l'action)
 *  - ses 3 lignes du champ de bataille
 *  - une pile des cartes "hors combat" (combattants KO, objets cass�s, cartes d�fauss�es...)
 * 
 * A chaque tour, les joueurs jouent une succession de phases:
 * 	- recrutement (pioche)
 *  - mouvement (d�placement des combattants/objets)
 * 	- action (combat au corps � corps, capacit�s sp�ciales, utilisation des mat�riels
 *  - r�cup�ration (les combattants en 2�me ligne reprennent des forces, les mat�riels qui le peuvent se rechargent).
 * 
 * Etat des combattants:
 *  - sant�: diminue avec les coups re�us (= 0 -> hors combat)
 *  - fatigue: augmente avec les coups port�s ou capacit�s utilis�es (= max -> �tat "�puis�")
 *  - force : intensit� des coups port�s.
 *  - armure : r�duction des coups re�us.
 *  - capacit� X utilis�e O/N.
 *  
 * Les joueurs ne jouent pas l'un apr�s l'autre mais en m�me temps:
 *  - les intentions d'actions (mouvements, attaques...) sont mat�rialis�es par des jetons pos�s face cach�e.
 *  - une fois que les joueurs n'ont plus de jeton � poser, les jetons sont r�v�l�s et les actions sont r�solues simultan�ment.
 * Une action aboutissant � une situation ill�gale est annul�e.
 * 
 * Phase de mouvement:
 *  - les mouvements concernent les combattants ou les mat�riels
 *  - un objet en mouvement peut aller 
 *  	- vers un emplacement vide ou par �change de place
 *  	- par ex. un combattant de 1�re ligne �change sa place avec le combattant de 2�me ligne derri�re lui).
 *  - les mouvements autoris�s sont
 *  	- en avant: de la ligne de r�serve � la 2�me ligne ou de la 2�me ligne en 1�re ligne.
 *  	- en arri�re : de la 1�re ligne � la 2�me ligne (normalement pas de retour en r�serve)
 *  	- � droite ou � gauche en restant sur la m�me ligne
 *  	- arriv�e d'une recrue ou d'un mat�riel sur la ligne de r�serve depuis la main (face cach�e)
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
