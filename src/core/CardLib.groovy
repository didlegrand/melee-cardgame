package core

import groovy.lang.Closure;

/**
 * Card library - set of card definitions
 * @author Legrand
 *
 */
class CardLib {
	
	Map<CardDef, Integer> ownedCards = [:]
	
	def card(String name, Closure cardDef) {
		println "building card $name"
		CardDef c = new CardDef()
		cardDef.call(c)
		c.properties.each{ k, v ->
			println "	$k = $v"
		}
		if (ownedCards[c] == null) {
			ownedCards[c] = 1
		} else {
			ownedCards[c] ++
		}
		println "   -> nombre de $c.name = "+ownedCards[c]
	}
	
}
