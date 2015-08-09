package model;


public enum Card {

	KNIGHT(CardType.UNIT), 
	ARCHER(CardType.UNIT), 
	CLERIC(CardType.UNIT), 
	WIZARD(CardType.UNIT), 
	NINJA(CardType.UNIT),
	INFERNO(CardType.SPELL), 
	REVIVE_POTION(CardType.ITEM), 
	RUNEMETAL(CardType.ITEM), 
	SCROLL(CardType.ITEM), 
	DRAGONSCALE(CardType.ITEM), 
	SHINING_HELM(CardType.ITEM), 
	CRYSTAL(CardType.UNIT);
	
	public final CardType type;
	
	Card(CardType type) {
		this.type = type;
	}
	
}

