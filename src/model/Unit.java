package model;

import game.GameState;

import java.util.HashSet;
import java.util.Set;

import libs.UnitClassLib;

public class Unit {

	public int hp;
	public UnitClass unitClass;
	public Set<Card> equipment;
	public boolean p1Owner;
	public static int count = 0;

	public Unit(Card type, boolean p1Owner) {
		super();
		/*
		count++;
		if (count % 1000 == 0)
			System.out.println(count);
		*/
		if (type != null) {
			unitClass = UnitClassLib.lib.get(type);
			hp = unitClass.maxHP;
		}
		equipment = new HashSet<Card>();
		this.p1Owner = p1Owner;
	}

	public Unit(int hp, Unit type, boolean p1Owner, Set<Card> equipment) {
		super();
		// System.out.println(count++);
		unitClass = UnitClassLib.lib.get(type);
		this.hp = hp;
		this.equipment = equipment;
		this.p1Owner = p1Owner;
	}

	public Unit(int hp, UnitClass uclass, boolean p1Owner, Set<Card> equipment) {
		super();
		// System.out.println(count++);
		unitClass = uclass;
		this.hp = hp;
		this.equipment = equipment;
		this.p1Owner = p1Owner;
	}

	public void init(Card type, boolean p1Owner) {
		if (type != null) {
			unitClass = UnitClassLib.lib.get(type);
			hp = unitClass.maxHP;
		}
		equipment = new HashSet<Card>();
		this.p1Owner = p1Owner;
	}

	public void equip(Card card, GameState state) {
		if (card == Card.REVIVE_POTION) {
			if (hp == 0)
				hp += 100;
			else
				hp += 600;
		} else {
			equipment.add(card);
			if (card == Card.DRAGONSCALE || card == Card.SHINING_HELM)
				hp += hp / 10d;
		}
		hp = Math.min(hp, maxHP());
	}

	public int power(GameState state, Position pos) {

		// Initial power
		int power = unitClass.power;
		
		// Sword
		if (equipment.contains(Card.RUNEMETAL))
			power += power / 2;

		// Power boost
		if (state.map.squareAt(pos.x, pos.y) == SquareType.POWER)
			power += 100;

		// Scroll
		if (equipment.contains(Card.SCROLL))
			power *= 3;

		return power;
	}

	public int damage(GameState state, Position attPos, Unit defender,
			Position defPos) {

		if (unitClass.attack == null)
			return 0;
		
		double dam = power(state, attPos);

		if (attPos.distance(defPos) == 1)
			dam *= unitClass.attack.meleeMultiplier;
		else
			dam *= unitClass.attack.rangeMultiplier;

		final double resistance = defender.resistance(state, defPos,
				unitClass.attack.attackType);

		return (int) (dam * ((100d - resistance) / 100d));
	}

	public int maxHP() {

		int max = unitClass.maxHP;

		if (equipment.contains(Card.DRAGONSCALE))
			max += unitClass.maxHP / 10;
		if (equipment.contains(Card.SHINING_HELM))
			max += unitClass.maxHP / 10;

		return max;
	}

	public int resistance(GameState state, Position pos, AttackType attackType) {

		int res = 0;

		if (attackType == AttackType.Magical) {
			res += unitClass.magicalResistance;
			if (equipment.contains(Card.SHINING_HELM))
				res += 20;
			// TODO : also +20 for defense square?
		} else if (attackType == AttackType.Physical) {
			res += unitClass.physicalResistance;
			if (equipment.contains(Card.DRAGONSCALE))
				res += 20;
			if (state.map.squareAt(pos.x, pos.y) == SquareType.DEFENSE)
				res += 20;
		}

		return res;

	}

	public boolean fullHealth() {
		if (hp == maxHP())
			return true;
		return false;
	}

	public void heal(int health) {

		hp = Math.min(hp + health, this.maxHP());

	}

	public void imitate(Unit unit) {
		hp = unit.hp;
		unitClass = unit.unitClass;
		p1Owner = unit.p1Owner;
		equipment.clear();
		equipment.addAll(unit.equipment);
	}

	public Unit copy() {
		final Set<Card> eq = new HashSet<Card>();
		for (final Card card : equipment)
			eq.add(card);

		return new Unit(hp, unitClass, p1Owner, eq);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Unit other = (Unit) obj;
		if (equipment == null) {
			if (other.equipment != null)
				return false;
		} else if (!equipment.equals(other.equipment))
			return false;
		if (hp != other.hp)
			return false;
		if (p1Owner != other.p1Owner)
			return false;
		if (unitClass == null) {
			if (other.unitClass != null)
				return false;
		} else if (!unitClass.equals(other.unitClass))
			return false;
		return true;
	}
	
	public int hash(int x, int y) {
		int result = 1;
		result = 16 * result + equipmentHash();
		result = 6553 * result + hp;
		result = 2 * result + (p1Owner ? 0 : 1);
		result = 6 * result + unitClass.hash();
		result = 5 * result + x;
		result = 5 * result + y;
		return result;
	}
	
	public int hash() {
		int result = 1;
		result = 16 * result + equipmentHash();
		result = 1500 * result + hp;
		result = 2 * result + (p1Owner ? 0 : 1);
		result = 6 * result + unitClass.hash();
		return result;
	}

	@Override
	public String toString() {
		return "[" + (p1Owner ? 1 : 2) + "," + unitClass.card.name() + "," + hp + "," + equipment + "]";
	}

	private int equipmentHash() {
		int e = 0;
		if (equipment.contains(Card.DRAGONSCALE))
			e += 1;
		if (equipment.contains(Card.RUNEMETAL))
			e += 2;
		if (equipment.contains(Card.SHINING_HELM))
			e += 4;
		if (equipment.contains(Card.SCROLL))
			e += 8;
		return e;
	}
}
