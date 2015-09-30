package core

/**
 * Instance of a game being played
 * 
 * Rules:
 * 
 * 'M�l�e'. Jeu de carte pour 2 joueurs de type 'duel'.
 * 
 * Le jeu repr�sente l'affrontement de 2 g�n�raux incarn�s par les joueurs.
 * 
 * Les joueurs disposent 
 * - de cartes repr�sentant des unit�s combattantes,
 * - de jetons � poser repr�sentant des intentions d'action.
 * 
 * Les 2 joueurs jouent en m�me temps.
 * Ils posent des jetons phase cach�e sur les cartes pour indiquer l'action ou le d�placement � r�aliser.
 * Lorsqu'aucun joueur ne souhaite plus poser de jeton, on retourne tous les jetons pour r�soudre les actions.
 * Une action aboutissant � une situation ill�gale est annul�e.
 *   
 * En cours de partie, les cartes se trouvent soit
 * - dans un camp ou sur le champ de bataille
 * - dans une pile de pioche (recrutement)
 * - dans la main du joueur (r�serves)
 * - dans un cimeti�re.
 * 
 * Chaque joueur a devant lui une ligne de 5 emplacements qui repr�sente son camp.
 * Devant le camp, se trouve une autre ligne de 5 emplacements qui repr�sente sa moiti� du champ de bataille.
 * En face se trouve la ligne qui repr�sente la moiti� du champ de bataille de l'adversaire.
 * Derri�re se trouve le camp de l'adversaire.
 * Il y a donc 4 x 5 = 20 emplacements sur la table, un emplacement ne pouvant contenir qu'une carte.
 * 
 * Sur le champ de bataille, les cartes sont pos�es face visible.
 * Dans les camps, les cartes sont pos�es face cach�e (l'adversaire ne les connait pas).
 * 
 * Un joueur gagne si une de ses unit�s se trouve dans le camp adverse en fin de tour.
 * 
 * A chaque tour, les joueurs jouent une succession de phases:
 * 	- recrutement (pioche)
 *  - mouvement (d�placement des combattants/objets)
 * 	- action (combat au corps � corps, capacit�s sp�ciales, utilisation des mat�riels)
 *  - fin de tour (et �ventuellement fin de partie).
 *  
 * L'�tat des combattants ou des mat�riels varie pendant le tour:
 *  - sant�: diminue avec les coups re�us ou avec la fatigue (= 0 -> hors combat)
 *  - force : mesure l'intensit� des coups port�s.
 *  - armure : vient en r�duction des coups re�us.
 *  
 * Phase de pioche:
 *  - Chaque joueur pioche une, deux ou trois cartes.
 *  - S'il a choisi de piocher 2 cartes, il en choisit une qu'il place sous la pile et met l'autre dans sa main.
 *  - S'il a choisi de piocher 3 cartes, il en place une sous la pile, s'en d�fausse d'une et met la derni�re dans sa main.
 *  
 * Phase de mouvement:
 *  - pendant cette phase, les unit�s peuvent
 *  	- arriver dans le camp face cach�e depuis la r�serve (la main) pour occuper des emplacements vides
 *   	- avancer ou reculer d'une ligne � l'autre (camp -> champ de bataille -> camp)
 *   	- �changer leur place avec une autre unit� amie situ�e devant, derri�re, � gauche ou � droite
 *   	- se d�placer � gauche ou � droite sur la m�me ligne.
 * 
 *  - en d�but de phase, les joueurs placent des jetons mouvement face cach�e qui repr�sentent l'ordre de mouvement.
 *  - lorsqu'aucun joueur ne souhaite plus poser de jeton mouvement, on r�v�le tous les ordres de mouvement.
 *  - les mouvements sont ex�cut�s 
 *  	- de gauche � droite sur les champ de bataille puis
 *  	- de gauche � droite dans les camps puis
 *  	- en faisant rentrer les unit�s de r�serve dans les camps.
 *  - si un mouvement devait aboutir � une situation ill�gale, typiquement 2 cartes sur le m�me emplacement
 *    ou une carte hors limite, ce mouvement est annul�.
 *  - les jetons sont enlev�s lorsque le mouvement est r�alis� ou annul�.
 *  
 *   Notes:
 *   	- Il est possible - et recommand� - de tromper l'adversaire en posant un jeton "sans mouvement" sur les objets qu'on n'a pas l'intention de d�placer.
 *   	- Tant qu'un joueur pose des jetons, son adversaire peut en poser aussi.
 *   	- Sauf mention contraire, on ne pose qu'un jeton mouvement par carte.
 *   	- On ne peut pas enlever ou changer un jeton pos�.
 *   
 * Phase d'action:
 * 	- Les joueurs posent des jetons action face cach�e sur les unit�s du champ de bataille et dans les camps.
 *  - Sauf mention contraire, on ne peut poser qu'un jeton par carte.
 *  - Les actions sont soit des actions de combat, soit des actions sp�ciales pr�vues sur la carte.
 *  - D�s lors qu'aucun joueur ne souhaite plus poser de jeton action, 
 *  	1) on r�v�le les jetons action
 *  	2) on ex�cute les actions dans le m�me ordre que pour les d�placements (de gauche � droite, champs de bataille puis camp)
 *  - A tout moment un joueur peut jouer un coup fourr� (pas besoin de jeton d'action):
 *  	- il r�v�le la carte si elle est face cach�e,
 *  	- il joue l'action pr�vue
 *  	- la carte est mise dans la d�fausse. 
 *  
 *  Actions de combat:
 *  - Attaque : inflige des blessures � l'unit� en face. TODO d�tailler: prend sa place si l'unit� attaqu�e meure ?
 *  - D�fense : se prot�ge de l'attaque en face. TODO d�tailler
 *  - exemples:
 *  	"A: En cas de combat: Fatigue 1. Inflige 3 blessures � l'unit� ennemie d'en face"
 *  	"D: En cas de combat: Fatigue 1. Pr�vient 3 blessures de combat"
 *  	"A: En cas de combat: Fatigue 3. Inflige 6 blessures � l'unit� ennemie d'en face"
 *  	
 *  Actions sp�ciales:
 *   - Les actions sp�ciales sont indiqu�es sur la carte.
 *   - les actions sp�ciales sont rep�r�es par un "S" suivi d'un num�ro, ex "S1"
 *   - Certaines actions fatiguent le combattant et comportent la mention "Fatigue X" o� X est le nombre de points de fatigue � ajouter au compteur courant.
 *   - exemples:
 *   	"S1: Vol. Fatigue 1. Le combattant s'�l�ve dans les airs avec un cerf-volant. L'adversaire r�v�le les cartes de sa ligne de r�serve"
 *   	"S1: Ravitaillement. Fatigue 1. R�duisez de 1 la fatigue du combattant devant vous."
 *   	"S2: Soins. Fatigue 1. R�duisez de 2 la fatigue du combattant devant vous."
 *   	"S1: Cannonade. Fatigue 2. Infligez 3 d�gats � l'ennemi situ� 2 lignes devant vous.
 *   	"S2: Cannonade. Fatigue 2. Infligez 2 d�gats � l'ennemi situ� 3 lignes devant vous.
 *   	"S1: Assassinat. Fatigue 5. Glissez vous sans �tre rep�r� dans les lignes ennemis et mettez hors-combat un combattant. Quittez le champ de bataille"  
 *   	"S2: Sabotage. Fatigue 5. Glissez vous sans �tre rep�r� dans les lignes ennemis et d�truisez un mat�riel. Quittez le champ de bataille"
 *   	"S1: R�paration. Fatigue 1. R�parez le mat�riel devant vous: sa fatigue diminue de 1."
 *      "S1: Renfort. Fatigue 1. Diminuez la fatigue des combattants � droite et � gauche."
 *      "S1: Sape. Fatigue 3. Creusez un tunnel et d�truisez la mine devant vous."
 *      "S1: Terreur. Fatigue 1. L'unit� ennemie devant vous ne peut faire aucune action de combat ou action sp�ciale jusqu'� la fin du tour. 
 *      
 *   Actions automatiques: il s'agit d'action qui ne sont pas d�clench�es par le joueur mais par un �v�nement.
 *      "Mine. Lorsqu'une unit� attaque la mine, la mine lui inflige 5 d�gats et se d�truit en m�me temps."
 *      "Lunette. Lorsque l'adversaire pose une unit� de r�serve en face de la lunette, r�v�lez-l�".
 *            
 *   Coup-fourr�s: 
 *      "Urgence. R�v�lez cette carte: Lorsqu'une unit� devrait �tre d�truite, vous pouvez la sauver et lui redonner sa sant� maximum. D�truisez cette carte".
 *      "Fumig�ne. R�v�lez cette carte: les unit�s amies qui parent au combat ne re�oivent pas de blessure ce tour-ci et ne se fatiguent pas. D�truisez cette carte."
 *      "Incendie. R�v�lez cette carte: Les mat�riels en bois sont d�truits. D�truisez cette carte"
 *      "Camouflage. R�v�lez cette carte: Redisposez les unit�s de r�serve o� vous voulez, face cach�e. D�truisez cette carte" 
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
