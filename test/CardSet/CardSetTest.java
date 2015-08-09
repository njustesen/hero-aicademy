package CardSet;

import model.Card;
import model.CardSet;

public class CardSetTest {

	public static void main(String[] args){
		
		CardSet set = new CardSet();
		set.add(Card.ARCHER);
		set.add(Card.CLERIC);
		set.add(Card.CLERIC);
		set.add(Card.ARCHER);
		set.add(Card.WIZARD);
		set.add(Card.NINJA);
		set.add(Card.DRAGONSCALE);
		set.add(Card.DRAGONSCALE);
		set.add(Card.DRAGONSCALE);
		set.add(Card.SCROLL);
		System.out.println(set.hashCode());
		
	}
	
}
