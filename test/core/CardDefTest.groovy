package core;

import static org.junit.Assert.*;

import org.junit.Test;

class CardDefTest {

	@Test
	public void createFighterDef() {
		CardDef red_guard = new CardDef(
			name:"Red Guard",
			type:"Soldier",
			text:"Underestimating a Red Guard has often proven to be a fatal error", 
			stamina:4, 
			power:2, 
			defense:6)
		Card rg = new Card(red_guard)
		assert rg != null
		assert rg.name == "Red Guard"
		assert rg.text.contains("fatal")
		assert rg.type == "Soldier"
		assert rg.stamina == 4
		assert rg.power == 2
		assert rg.defense == 6
	}

}
