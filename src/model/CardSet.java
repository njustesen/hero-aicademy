package model;

import java.util.Arrays;

public class CardSet {

	public int[] cards;
	public int size;
	public int seed;

	public CardSet() {
		cards = new int[Card.values().length];
		size = 0;
		seed = (int) (Math.random() * 1000);
	}

	public CardSet(int seed) {
		cards = new int[Card.values().length];
		size = 0;
		this.seed = seed;
	}

	public Card determined() {
		if (seed > 100000)
			seed = (int) (seed * 0.1);
		else
			seed = (int) (seed * 1.3 + 7);
		return get(seed % size);
	}

	public Card random() {
		return get((int) Math.floor(Math.random() * size));
	}

	public Card get(Integer r) {
		int c = 0;
		int i = 0;
		while (true) {
			c += cards[i];
			if (c > r)
				break;
			i++;
		}
		return Card.values()[i];
	}

	public void add(Card card) {
		cards[card.ordinal()]++;
		size++;
	}

	public void remove(Card card) {
		if (cards[card.ordinal()] > 0) {
			cards[card.ordinal()]--;
			size--;
		}
	}

	public boolean hasUnits() {
		if (units() > 0)
			return true;
		return false;
	}

	public void addAll(CardSet other) {
		size += other.size;
		for (int i = 0; i < other.cards.length; i++)
			cards[i] += other.cards[i];
	}

	public void clear() {
		size = 0;
		Arrays.fill(cards, 0);
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void imitate(CardSet p1Hand) {
		clear();
		addAll(p1Hand);
		seed = p1Hand.seed;
	}

	public int units() {
		int units = 0;
		for (final Card card : Card.values())
			if (card.type == CardType.UNIT && card != Card.CRYSTAL)
				units += cards[card.ordinal()];
		return units;
	}
	

	public boolean has(Card card) {
		if (cards.length > card.ordinal())
			return cards[card.ordinal()] > 0 ? true : false;
		return false;
	}

	public boolean contains(Card card) {
		if (cards.length > card.ordinal())
			return cards[card.ordinal()] > 0;
		return false;
	}

	public int count(Card card) {
		if (cards.length > card.ordinal())
			return cards[card.ordinal()];
		return 0;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		final int prime = 7;
		for (int i = 0; i < cards.length; i++)
			hash = hash * prime + cards[i];
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CardSet other = (CardSet) obj;
		if (!Arrays.equals(cards, other.cards))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Arrays.toString(cards).replaceAll(" ", "");
	}


}
