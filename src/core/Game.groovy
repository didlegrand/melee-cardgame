package core

/**
 * Game instance of a game being played
 * 
 * 'Mélée'. Jeu de carte pour 2 joueurs de type 'duel'.
 * 
 * Les joueurs disposent de cartes représentant des combattants ou des objets.
 * 
 * Les cartes sont disposées sur le champ de bataille en 3 lignes: 1ère, 2ème et réserve.
 * Il y a 3 emplacements par ligne. Un emplacement ne peut contenir qu'une carte.
 * En 1ère et 2ème ligne, les cartes sont normalement face visible. 
 * En réserve, les cartes sont normalement face cachée.
 * Un joueur gagne si
 *  - un de ses combattants franchit les 3 lignes de l'adversaire ou
 *  - si son adversaire devrait piocher et ne peux pas (plus de ressource). 
 * 
 * Disposition du jeu: chaque joueur a
 *  - une pile de pioche pour le recrutement ou l'achat de matériel
 *  - une main avec les cartes obtenues par recrutement
 *  - des jetons d'action (face A pour le type d'action, face B pour l'action)
 *  - ses 3 lignes du champ de bataille
 *  - une pile des cartes "hors combat" (combattants KO, objets cassés, cartes défaussées...)
 * 
 * A chaque tour, les joueurs jouent une succession de phases:
 * 	- recrutement (pioche)
 *  - mouvement (déplacement des combattants/objets)
 * 	- action (combat au corps à corps, capacités spéciales, utilisation des matériels)
 *  - récupération (les combattants en 2ème ligne reprennent des forces, les matériels qui le peuvent se rechargent).
 * 
 * Etat des combattants:
 *  - santé: diminue avec les coups reçus (= 0 -> hors combat)
 *  - fatigue: augmente avec les coups portés ou capacités utilisées (= max -> état "épuisé")
 *  - force : intensité des coups portés.
 *  - armure : réduction des coups reçus.
 *  - capacité X utilisée O/N.
 *  
 * Les joueurs ne jouent pas l'un après l'autre mais en même temps:
 *  - les intentions d'actions (mouvements, attaques...) sont matérialisées par des jetons posés face cachée.
 *  - une fois que les joueurs ne veulent plus poser de jeton, les jetons sont révélés et les actions sont résolues simultanément.
 * Une action aboutissant à une situation illégale est annulée.
 * 
 * Phase de pioche:
 *  - les joueurs piochent une carte
 *  - ils peuvent choisir d'en piocher une 2ème mais devront en placer une des 2 sous la pile de pioche.
 *  - ils peuvent choisir d'en piocher une 3ème mais devront en plus en jeter une des 3 dans la défausse.
 *  
 * Phase de mouvement:
 *  - les mouvements concernent les combattants ou les matériels
 *  - un objet en mouvement peut aller 
 *  	- vers un emplacement vide ou par échange de place
 *  	- par ex. un combattant de 1ère ligne échange sa place avec le combattant de 2ème ligne derrière lui).
 *  - les mouvements autorisés sont
 *  	- en avant: de la ligne de réserve à la 2ème ligne ou de la 2ème ligne en 1ère ligne.
 *  	- en arrière : de la 1ère ligne à la 2ème ligne (normalement pas de retour en réserve)
 *  	- à droite ou à gauche en restant sur la même ligne
 *  	- arrivée d'une recrue ou d'un matériel sur la ligne de réserve depuis la main (face cachée)
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
