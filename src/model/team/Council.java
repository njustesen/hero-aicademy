package model.team;

import java.util.ArrayList;
import java.util.List;

import model.Card;
import model.DECK_SIZE;

public class Council {

	public static List<Card> deck;
	static {
		deck = new ArrayList<Card>();

		deck.add(Card.KNIGHT);
		deck.add(Card.KNIGHT);
		deck.add(Card.KNIGHT);
		// deck.add(Card.KNIGHT);

		deck.add(Card.ARCHER);
		deck.add(Card.ARCHER);
		deck.add(Card.ARCHER);
		// deck.add(Card.ARCHER);

		deck.add(Card.CLERIC);
		deck.add(Card.CLERIC);
		deck.add(Card.CLERIC);
		// deck.add(Card.CLERIC);

		// for(int i = 0; i < 100; i++)
		deck.add(Card.WIZARD);
		deck.add(Card.WIZARD);
		deck.add(Card.WIZARD);
		// for(int i = 0; i < 10; i++)
		deck.add(Card.NINJA);

		deck.add(Card.INFERNO);
		deck.add(Card.INFERNO);

		// for(int i = 0; i < 10; i++)
		// deck.add(Card.INFERNO);

		deck.add(Card.RUNEMETAL);
		deck.add(Card.RUNEMETAL);
		deck.add(Card.RUNEMETAL);

		deck.add(Card.DRAGONSCALE);
		deck.add(Card.DRAGONSCALE);
		deck.add(Card.DRAGONSCALE);

		deck.add(Card.SHINING_HELM);
		deck.add(Card.SHINING_HELM);
		deck.add(Card.SHINING_HELM);

		// for(int i = 0; i < 6; i++)
		deck.add(Card.REVIVE_POTION);
		deck.add(Card.REVIVE_POTION);

		deck.add(Card.SCROLL);
		deck.add(Card.SCROLL);

	}
	
	public static List<Card> smallDeck;
	static {
		smallDeck = new ArrayList<Card>();

		smallDeck.add(Card.KNIGHT);
		smallDeck.add(Card.KNIGHT);

		smallDeck.add(Card.ARCHER);
		smallDeck.add(Card.ARCHER);

		smallDeck.add(Card.CLERIC);
		smallDeck.add(Card.CLERIC);

		smallDeck.add(Card.WIZARD);
		smallDeck.add(Card.WIZARD);
		
		smallDeck.add(Card.NINJA);

		smallDeck.add(Card.INFERNO);

		smallDeck.add(Card.RUNEMETAL);
		smallDeck.add(Card.RUNEMETAL);

		smallDeck.add(Card.DRAGONSCALE);
		smallDeck.add(Card.DRAGONSCALE);

		smallDeck.add(Card.SHINING_HELM);
		smallDeck.add(Card.SHINING_HELM);

		smallDeck.add(Card.REVIVE_POTION);

		smallDeck.add(Card.SCROLL);

	}

	public static List<Card> tinyDeck;
	static {
		tinyDeck = new ArrayList<Card>();

		tinyDeck.add(Card.KNIGHT);

		tinyDeck.add(Card.ARCHER);

		tinyDeck.add(Card.CLERIC);

		tinyDeck.add(Card.WIZARD);
		
		tinyDeck.add(Card.NINJA);

		tinyDeck.add(Card.INFERNO);

		tinyDeck.add(Card.RUNEMETAL);

		tinyDeck.add(Card.DRAGONSCALE);

		tinyDeck.add(Card.SHINING_HELM);

		tinyDeck.add(Card.REVIVE_POTION);

		tinyDeck.add(Card.SCROLL);

	}
	public static List<Card> deck(DECK_SIZE deckSize) {
		if (deckSize == DECK_SIZE.STANDARD)
			return deck;
		if (deckSize == DECK_SIZE.SMALL)
			return smallDeck;
		if (deckSize == DECK_SIZE.TINY)
			return tinyDeck;
		return deck;
	}
	
}
