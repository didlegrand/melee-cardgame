package core

/**
 * Game instance of a game being played
 * 
 * 'Mélée'. Jeu de carte pour 2 joueurs de type 'duel'.
 * 
 * Le jeu représente l'affrontement de 2 généraux incarnés par les joueurs. 
 * Les joueurs disposent de cartes représentant des combattants ou des objets.
 * 
 * Les cartes sont disposées sur le champ de bataille sur 3 lignes.
 * Les combattants en 1ère ligne sont au contact des combattants de la 1ère ligne adverse.
 * Il y a 3 emplacements par ligne (ou 4 ou 5 ???). 
 * Un emplacement ne peut contenir qu'une carte.
 * En 1ère et 2ème ligne, les cartes sont normalement face visible. 
 * En réserve, les cartes sont normalement face cachée.
 * Un joueur gagne si
 *  - un de ses combattants franchit les 3 lignes de l'adversaire ou
 *  - si l'adversaire devrait piocher mais ne peux pas (plus de ressource). 
 * 
 * Chaque joueur a devant lui
 *  - une pile de pioche, pour le recrutement ou l'achat de matériel.
 *  - des cartes en main, obtenues par la pioche.
 *  - des jetons d'intention (une face pour le type d'action dite 'face cachée', une face pour l'action dite 'face visible')
 *  - 3 lignes de champ de bataille notées
 *  	- "1" pour la 1ère ligne
 *  	- "2" pour la 2ème ligne
 *  	- "R" pour la ligne de réserve.
 *  - une pile des cartes "hors combat" (combattants KO, objets cassés, cartes défaussées...)
 * 
 * A chaque tour, les joueurs jouent une succession de phases:
 * 	- recrutement (pioche)
 *  - mouvement (déplacement des combattants/objets)
 * 	- action (combat au corps à corps, capacités spéciales, utilisation des matériels)
 *  - récupération (les combattants en 2ème ligne reprennent des forces, les matériels qui le peuvent se rechargent).
 *  
 * Les joueurs ne jouent pas l'un après l'autre mais en même temps:
 *  - les intentions d'actions (mouvements, attaques...) sont matérialisées par des jetons posés face cachée.
 *  - une fois que les joueurs ne veulent plus poser de jeton, les jetons sont révélés et les actions sont résolues simultanément.
 * Une action aboutissant à une situation illégale est annulée.
 *  
 * L'état des combattants ou des matériels varie pendant le tour:
 *  - santé: diminue avec les coups reçus (= 0 -> hors combat)
 *  - fatigue: augmente avec les coups portés ou les capacités utilisées (= max -> état "épuisé")
 *  - force : mesure l'intensité des coups portés.
 *  - armure : vient en réduction des coups reçus.
 *  
 * Phase de pioche:
 *  - les joueurs piochent une carte
 *  - ils peuvent choisir d'en piocher une 2ème mais devront en placer une des 2 sous la pile de pioche en fin de phase.
 *  - ils peuvent choisir d'en piocher une 3ème mais devront en plus en jeter une des 3 dans la défausse en fin de phase.
 *  
 * Phase de mouvement:
 *  - les mouvements concernent 
 *  	- les combattants ou les matériels sur le champ de bataille.
 *  	- les cartes en main qui peuvent arriver face cachée sur la ligne de réserve.
 *  - les mouvements se font vers l'avant, l'arrière, la gauche ou la droite mais pas en diagonale.
 *  - il n'est pas possible de faire reculer un objet de la ligne de réserve.
 *  
 *  - un objet en mouvement peut 
 *  	- aller vers un emplacement vide 
 *  	- échanger sa place avec un autre objet, par exemple un combattant de 1ère ligne échange sa place avec le combattant de 2ème ligne derrière lui).
 *  
 *  - les mouvements autorisés sont donc
 *  	- en avant: de réserve en 2ème ligne ou de 2ème ligne en 1ère ligne.
 *  	- en arrière : de la 1ère ligne à la 2ème ligne (pas de retour en réserve)
 *  	- à droite ou à gauche en restant sur la même ligne
 *  	- depuis la main vers la ligne de réserve face cachée.
 *  
 *   Chaque intention de mouvement se matérialise par un jeton posé face caché sur l'objet à déplacer.
 *   
 *   Il est possible - et recommandé - de tromper l'adversaire en posant un jeton "sans mouvement" sur les objets qu'on n'a pas l'intention de déplacer.
 *   Tant qu'un joueur pose des jetons, son adversaire peut en poser aussi.
 *   
 *   On ne pose qu'un jeton mouvement par carte.
 *   Il est interdit d'enlever ou de changer un jeton posé.
 *   
 *   Lorsque les 2 joueurs conviennent qu'ils ont fini de poser leurs jetons, on révèle les jetons en les retournant.
 *   Les mouvement sont exécutés de gauche à droite, de la 1ère ligne à la réserve.
 *   Les entrées en réserve depuis la main sont jouées en dernier, toujours de gauche à droite.
 *   Si un mouvement est illégal, il est annulé.
 *   Les jetons mouvement sont enlevés.
 *   
 *   Cas des lignes enfoncées:
 *   - une ligne est enfoncée lorsque des combattants ennemis sont présents sur une des lignes d'un joueur.
 *   - ces combattants ne se déplacent pas d'une ligne à l'autre pendant la phase de mouvement.
 *   - ils pourront avancer ou reculer pendant la phase "action" (charge ou retraite).
 *  
 * Phase d'action:
 * 	Les joueurs posent des jetons action face cachée sur les cartes en ligne 1 et 2.
 *  On ne peut normalement poser qu'un jeton par carte.
 *  Les actions sont soit des actions de combat, soit des actions spéciales prévues sur la carte.
 *  
 *  Actions de combat:
 *  - Attaque : inflige des blessures à l'adversaire en face (seulement de ligne 1 à ligne 1). Coûte 1 Fatigue.
 *  - Parade : se protège de l'attaque en face (seulement de ligne 1 à ligne 1). Coûte 1 Fatigue.
 *  - Charge : Attaque possible lorsque le combattant a enfoncé les lignes ennemies 
 *  	(attaque + mouvement vers l'avant si l'adversaire est KO).
 *  	La charge coûte 2 points de Fatigue.
 *  - Retraite : recul possible depuis les lignes ennemies (d'une ligne).
 *  	
 *  Actions spéciales:
 *   - comme indiqué sur la carte.
 *   - les actions spéciales sont repérées par un "S" suivi d'un numéro, ex "S1"
 *   - Certaines actions fatiguent le combattant et comportent la mention "Fatigue X" où X est le nombre de points de fatigue à ajouter au compteur courant.
 *   - exemples:
 *   	"S1: Vol. Fatigue 1. Le combattant s'élève dans les airs avec un cerf-volant. L'adversaire révéle les cartes de sa ligne de réserve"
 *   	"S1: Ravitaillement. Fatigue 1. Réduisez de 1 la fatigue du combattant devant vous."
 *   	"S2: Soins. Fatigue 1. Réduisez de 2 la fatigue du combattant devant vous."
 *   	"S1: Cannonade. Fatigue 2. Infligez 3 dégats à l'ennemi situé 2 lignes devant vous.
 *   	"S2: Cannonade. Fatigue 2. Infligez 2 dégats à l'ennemi situé 3 lignes devant vous.
 *   	"S1: Assassinat. Fatigue 5. Glissez vous sans être repéré dans les lignes ennemis et mettez hors-combat un combattant. Quittez le champ de bataille"  
 *   	"S2: Sabotage. Fatigue 5. Glissez vous sans être repéré dans les lignes ennemis et détruisez un matériel. Quittez le champ de bataille"
 *   	"S1: Réparation. Fatigue 1. Réparez le matériel devant vous: sa fatigue diminue de 1."
 *      "S1: Renfort. Fatigue 1. Diminuez la fatigue des combattants à droite et à gauche."
 *      "S1: Sape. Fatigue 3. Creusez un tunnel et détruisez la mine devant vous."
 *      "S1: Terreur. Fatigue 1. L'unité ennemie devant vous ne peut faire aucune action de combat ou action spéciale jusqu'à la fin du tour. 
 *      
 *   Actions automatiques: il s'agit d'action qui ne sont pas déclenchées par le joueur mais par un évènement.
 *      "Mine. Lorsqu'une unité attaque la mine, elle lui inflige 5 dégats et se détruit en même temps."
 *      "Lunette. Lorsque l'adversaire pose une unité de réserve en face de la lunette, révélez-là".
 *            
 *   Coup-fourrés: 
 *      "Fumigène. Révèlez cette carte: les unités amies qui parent au combat ne reçoivent pas de blessure ce tour-ci et ne se fatiguent pas. Détruisez cette carte."
 *      "Incendie. Révèlez cette carte: Les matériels en bois sont détruits. Détruisez cette carte"
 *      "Foudre. Révèlez cette carte: Les matériels en métal sont détruits. Détruisez cette carte"
 *      "Camouflage. Révélez cette carte: Redisposez les unités de réserve où vous voulez, face cachée. Détruisez cette carte" 
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
