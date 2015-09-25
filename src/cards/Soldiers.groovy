package cards

import core.CardDef;
import core.CardLib

class Soldiers extends CardLib {

	Soldiers() {
		def lib = {
			card("red guard") {	CardDef c ->
				c.name = "Red Guard"
				c.type = "soldier"
				c.text = "Underestimate a Red Guard at your own risk..."
				c.stamina = 4
				c.power = 2
				c.defense = 6
			}
			card("soldier of fortune") { CardDef c ->
				c.name = "Soldier of Fortune"
				c.type = "soldier"
				c.text = "His fortune might be your ruin."
				c.stamina = 3
				c.power = 4
				c.defense = 2
			}
		}
		lib.run()
	}
}
