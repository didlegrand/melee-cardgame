package core;

import static org.junit.Assert.*

import org.junit.Test

import cards.Soldiers

class CardLibTest {

	@Test
	public void createSoldiers() {
		def soldiers = new Soldiers()
		assert soldiers.ownedCards.size() == 2
	}

}
