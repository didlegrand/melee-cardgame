package core

/**
 * Instance of a game being played
 * 
 * Rules:
 * 
 * 'Mélée'. Jeu de carte pour 2 joueurs de type 'duel'.
 * 
 * Le jeu représente l'affrontement de 2 généraux incarnés par les joueurs.
 * 
 * Les joueurs disposent 
 * - de cartes représentant des unités combattantes,
 * - de jetons à poser représentant des intentions d'action.
 * 
 * Les 2 joueurs jouent en même temps.
 * Ils posent des jetons phase cachée sur les cartes pour indiquer l'action ou le déplacement à réaliser.
 * Lorsqu'aucun joueur ne souhaite plus poser de jeton, on retourne tous les jetons pour résoudre les actions.
 * Une action aboutissant à une situation illégale est annulée.
 *   
 * En cours de partie, les cartes se trouvent soit
 * - dans un camp ou sur le champ de bataille
 * - dans une pile de pioche (recrutement)
 * - dans la main du joueur (réserves)
 * - dans un cimetière.
 * 
 * Chaque joueur a devant lui une ligne de 5 emplacements qui représente son camp.
 * Devant le camp, se trouve une autre ligne de 5 emplacements qui représente sa moitié du champ de bataille.
 * En face se trouve la ligne qui représente la moitié du champ de bataille de l'adversaire.
 * Derrière se trouve le camp de l'adversaire.
 * Il y a donc 4 x 5 = 20 emplacements sur la table, un emplacement ne pouvant contenir qu'une carte.
 * 
 * Sur le champ de bataille, les cartes sont posées face visible.
 * Dans les camps, les cartes sont posées face cachée (l'adversaire ne les connait pas).
 * 
 * Un joueur gagne si une de ses unités se trouve dans le camp adverse en fin de tour.
 * 
 * A chaque tour, les joueurs jouent une succession de phases:
 * 	- recrutement (pioche)
 *  - mouvement (déplacement des combattants/objets)
 * 	- action (combat au corps à corps, capacités spéciales, utilisation des matériels)
 *  - fin de tour (et éventuellement fin de partie).
 *  
 * L'état des combattants ou des matériels varie pendant le tour:
 *  - santé: diminue avec les coups reçus ou avec la fatigue (= 0 -> hors combat)
 *  - force : mesure l'intensité des coups portés.
 *  - armure : vient en réduction des coups reçus.
 *  
 * Phase de pioche:
 *  - Chaque joueur pioche une, deux ou trois cartes.
 *  - S'il a choisi de piocher 2 cartes, il en choisit une qu'il place sous la pile et met l'autre dans sa main.
 *  - S'il a choisi de piocher 3 cartes, il en place une sous la pile, s'en défausse d'une et met la dernière dans sa main.
 *  
 * Phase de mouvement:
 *  - pendant cette phase, les unités peuvent
 *  	- arriver dans le camp face cachée depuis la réserve (la main) pour occuper des emplacements vides
 *   	- avancer ou reculer d'une ligne à l'autre (camp -> champ de bataille -> camp)
 *   	- échanger leur place avec une autre unité amie située devant, derrière, à gauche ou à droite
 *   	- se déplacer à gauche ou à droite sur la même ligne.
 * 
 *  - en début de phase, les joueurs placent des jetons mouvement face cachée qui représentent l'ordre de mouvement.
 *  - lorsqu'aucun joueur ne souhaite plus poser de jeton mouvement, on révèle tous les ordres de mouvement.
 *  - les mouvements sont exécutés 
 *  	- de gauche à droite sur les champ de bataille puis
 *  	- de gauche à droite dans les camps puis
 *  	- en faisant rentrer les unités de réserve dans les camps.
 *  - si un mouvement devait aboutir à une situation illégale, typiquement 2 cartes sur le même emplacement
 *    ou une carte hors limite, ce mouvement est annulé.
 *  - les jetons sont enlevés lorsque le mouvement est réalisé ou annulé.
 *  
 *   Notes:
 *   	- Il est possible - et recommandé - de tromper l'adversaire en posant un jeton "sans mouvement" sur les objets qu'on n'a pas l'intention de déplacer.
 *   	- Tant qu'un joueur pose des jetons, son adversaire peut en poser aussi.
 *   	- Sauf mention contraire, on ne pose qu'un jeton mouvement par carte.
 *   	- On ne peut pas enlever ou changer un jeton posé.
 *   
 * Phase d'action:
 * 	- Les joueurs posent des jetons action face cachée sur les unités du champ de bataille et dans les camps.
 *  - Sauf mention contraire, on ne peut poser qu'un jeton par carte.
 *  - Les actions sont soit des actions de combat, soit des actions spéciales prévues sur la carte.
 *  - Dès lors qu'aucun joueur ne souhaite plus poser de jeton action, 
 *  	1) on révèle les jetons action
 *  	2) on exécute les actions dans le même ordre que pour les déplacements (de gauche à droite, champs de bataille puis camp)
 *  - A tout moment un joueur peut jouer un coup fourré (pas besoin de jeton d'action):
 *  	- il révèle la carte si elle est face cachée,
 *  	- il joue l'action prévue
 *  	- la carte est mise dans la défausse. 
 *  
 *  Actions de combat:
 *  - Attaque : inflige des blessures à l'unité en face. TODO détailler 
 *  - Défense : se protège de l'attaque en face. TODO détailler
 *  - exemples:
 *  	"A: En cas de combat: Fatigue 1. Inflige 3 blessures à l'unité ennemie d'en face"
 *  	"D: En cas de combat: Fatigue 1. Prévient 3 blessures de combat"
 *  	"A: En cas de combat: Fatigue 3. Inflige 6 blessures à l'unité ennemie d'en face"
 *  	
 *  Actions spéciales:
 *   - Les actions spéciales sont indiquées sur la carte.
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
 *      "Mine. Lorsqu'une unité attaque la mine, la mine lui inflige 5 dégats et se détruit en même temps."
 *      "Lunette. Lorsque l'adversaire pose une unité de réserve en face de la lunette, révélez-là".
 *            
 *   Coup-fourrés: 
 *      "Urgence. Révélez cette carte: Lorsqu'une unité devrait être détruite, vous pouvez la sauver et lui redonner sa santé maximum. Détruisez cette carte".
 *      "Fumigène. Révèlez cette carte: les unités amies qui parent au combat ne reçoivent pas de blessure ce tour-ci et ne se fatiguent pas. Détruisez cette carte."
 *      "Incendie. Révèlez cette carte: Les matériels en bois sont détruits. Détruisez cette carte"
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
