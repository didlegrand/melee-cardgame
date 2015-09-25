package core

/***
 * Instance of a card (physical card)
 * @author Legrand
 *
 */
class Card {
	
	Map properties = [:]
	
	Card(CardDef cd) {
		cd.properties.each { k, v ->
			this.properties[k] = v
		}
	}
	
	void set(String key, value) {
		properties[key] = value
	}
	
	Object get(String key) {
		return properties[key]
	}

}
