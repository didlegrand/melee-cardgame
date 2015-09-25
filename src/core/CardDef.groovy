package core

import java.util.Map;

/**
 * Card definition - used to build physical cards
 * @author Legrand
 *
 */
class CardDef {
	
	Map properties = [:]
	
	void set(String key, value) {
		properties[key] = value
	}
	
	Object get(String key) {
		return properties[key]
	}



}
