package libs;

import java.util.HashMap;

import model.Attack;
import model.AttackType;
import model.Card;
import model.Heal;
import model.UnitClass;

public class UnitClassLib {

	public static HashMap<Card, UnitClass> lib = new HashMap<Card, UnitClass>();

	static {

		// Add units
		lib.put(Card.KNIGHT, new UnitClass(Card.KNIGHT, 1000, 2, 200, 20, 0,
				null, null, false));
		lib.put(Card.ARCHER, new UnitClass(Card.ARCHER, 800, 2, 300, 0, 0,
				null, null, false));
		lib.put(Card.CLERIC, new UnitClass(Card.CLERIC, 800, 2, 200, 0, 0,
				null, null, false));
		lib.put(Card.WIZARD, new UnitClass(Card.WIZARD, 800, 2, 200, 0, 10,
				null, null, false));
		lib.put(Card.NINJA, new UnitClass(Card.NINJA, 800, 3, 200, 0, 0, null,
				null, false));

		// Add crystal
		lib.put(Card.CRYSTAL, new UnitClass(Card.CRYSTAL, 4500, 0, 0, 0, 0,
				null, null, false));

		// Add attacks
		lib.get(Card.KNIGHT).attack = new Attack(1, AttackType.Physical, 200,
				1, 1, false, true);
		lib.get(Card.ARCHER).attack = new Attack(3, AttackType.Physical, 300,
				0.5, 1, false, false);
		lib.get(Card.CLERIC).attack = new Attack(2, AttackType.Magical, 200, 1,
				1, false, false);
		lib.get(Card.WIZARD).attack = new Attack(2, AttackType.Magical, 200, 1,
				1, true, false);
		lib.get(Card.NINJA).attack = new Attack(2, AttackType.Physical, 200, 2,
				1, false, false);

		// Add heal
		lib.get(Card.CLERIC).heal = new Heal(2, 3, 2);
		lib.get(Card.NINJA).swap = true;

	}

}
