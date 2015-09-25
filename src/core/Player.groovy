package core

/**
 * Person playing a game
 * @author Legrand
 *
 */
class Player {
	
	CardLib playerLib
	List<DeckDef> availableDecks
	
	Zone deck = new Zone()
	Zone hand = new Zone()
	Zone inPlay = new Zone()
	Zone graveyard = new Zone()

}
