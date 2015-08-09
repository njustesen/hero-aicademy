package model;


public class UnitClass {

	public Card card;
	public int maxHP;
	public int speed;
	public int physicalResistance;
	public int magicalResistance;
	public int power;
	public Attack attack;
	public Heal heal;
	public boolean swap;

	public UnitClass(Card card, int maxHP, int speed, int power,
			int physicalResistance, int magicalResistance, Attack attack,
			Heal heal, boolean swap) {
		super();

		this.card = card;
		this.maxHP = maxHP;
		this.speed = speed;
		this.power = power;
		this.attack = attack;
		this.heal = heal;
		this.physicalResistance = physicalResistance;
		this.magicalResistance = magicalResistance;
		this.swap = swap;
	}

	public int hash() {
		switch(card){
		case ARCHER : return 0;
		case CRYSTAL : return 1;
		case KNIGHT : return 2;
		case NINJA : return 3;
		case CLERIC : return 4;
		case WIZARD : return 5;
		}
		return 6;
	}
}
