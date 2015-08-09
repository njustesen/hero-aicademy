package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.AttackType;
import model.Card;
import model.CardSet;
import model.CardType;
import model.DECK_SIZE;
import model.Direction;
import model.HaMap;
import model.Position;
import model.SquareType;
import model.Unit;
import model.team.Council;
import util.CachedLines;
import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.SingletonAction;
import action.SwapCardAction;
import action.UnitAction;
import action.UnitActionType;

public class GameState {
	
	public static int TURN_LIMIT = 100;
	public static boolean RANDOMNESS = false;
	public static int STARTING_AP = 3;
	public static int ACTION_POINTS = 5;
	
	private static final int ASSAULT_BONUS = 300;
	private static final double INFERNO_DAMAGE = 350;
	private static final int REQUIRED_UNITS = 3;
	private static final int POTION_REVIVE = 100;
	private static final int POTION_HEAL = 1000;
	public static final boolean OPEN_HANDS = false;

	public HaMap map;
	public boolean p1Turn;
	public int turn;
	public int APLeft;
	public Unit[][] units;
	public CardSet p1Deck;
	public CardSet p2Deck;
	public CardSet p1Hand;
	public CardSet p2Hand;
	public boolean isTerminal;

	public List<Position> chainTargets;

	public GameState(HaMap map) {
		super();
		isTerminal = false;
		this.map = map;
		p1Turn = true;
		turn = 1;
		APLeft = STARTING_AP;
		p1Hand = new CardSet();
		p2Hand = new CardSet();
		p1Deck = new CardSet();
		p2Deck = new CardSet();
		chainTargets = new ArrayList<Position>();
		if (map != null)
			units = new Unit[map.width][map.height];
		else
			units = new Unit[0][0];
	}

	public GameState(HaMap map, boolean p1Turn, int turn, int APLeft,
			Unit[][] units, CardSet p1Hand, CardSet p2Hand, CardSet p1Deck,
			CardSet p2Deck, List<Position> chainTargets, boolean isTerminal) {
		super();
		this.map = map;
		this.p1Turn = p1Turn;
		this.turn = turn;
		this.APLeft = APLeft;
		this.units = units;
		this.p1Hand = p1Hand;
		this.p2Hand = p2Hand;
		this.p1Deck = p1Deck;
		this.p2Deck = p2Deck;
		this.chainTargets = chainTargets;
		this.isTerminal = isTerminal;
		
	}

	public void init(DECK_SIZE deckSize) {
		p1Hand = new CardSet();
		p2Hand = new CardSet();
		p1Deck = new CardSet();
		p2Deck = new CardSet();
		shuffleDecks(deckSize);
		dealCards();
		for (final Position pos : map.p1Crystals) {
			units[pos.x][pos.y] = new Unit(Card.CRYSTAL, true);
			units[pos.x][pos.y].init(Card.CRYSTAL, true);
		}
		for (final Position pos : map.p2Crystals) {
			units[pos.x][pos.y] = new Unit(Card.CRYSTAL, false);
			units[pos.x][pos.y].init(Card.CRYSTAL, false);
		}
	}

	private void shuffleDecks(DECK_SIZE deckSize) {
		for (final Card type : Council.deck(deckSize)) {
			p1Deck.add(type);
			p2Deck.add(type);
		}
	}

	public void possibleActions(List<Action> actions) {

		actions.clear();

		if (APLeft == 0) {
			actions.add(SingletonAction.endTurnAction);
			return;
		}

		for (int x = 0; x < map.width; x++)
			for (int y = 0; y < map.height; y++)
				if (units[x][y] != null)
					possibleActions(units[x][y], SingletonAction.positions[x][y], actions);

		final List<Card> visited = new ArrayList<Card>();
		for (final Card card : Card.values())
			if (currentHand().contains(card)) {
				possibleActions(card, actions);
				visited.add(card);
			}

	}

	public void possibleActions(Card card, List<Action> actions) {

		if (APLeft == 0)
			return;

		if (card.type == CardType.ITEM) {
			for (int x = 0; x < map.width; x++)
				for (int y = 0; y < map.height; y++)
					if (units[x][y] != null
							&& units[x][y].unitClass.card != Card.CRYSTAL) {
						if (units[x][y].equipment.contains(card))
							continue;
						if (units[x][y].p1Owner == p1Turn) {
							if (card == Card.REVIVE_POTION
									&& units[x][y].fullHealth())
								continue;
							if (card != Card.REVIVE_POTION
									&& units[x][y].hp == 0)
								continue;
							actions.add(new DropAction(card, SingletonAction.positions[x][y]));
						}
					}
		} else if (card.type == CardType.SPELL)
			for (int x = 0; x < map.width; x++)
				for (int y = 0; y < map.height; y++)
					actions.add(new DropAction(card, SingletonAction.positions[x][y]));
		else if (card.type == CardType.UNIT)
			if (p1Turn) {
				for (final Position pos : map.p1DeploySquares)
					if (units[pos.x][pos.y] == null
							|| units[pos.x][pos.y].hp == 0)
						actions.add(new DropAction(card, SingletonAction.positions[pos.x][pos.y]));
			} else
				for (final Position pos : map.p2DeploySquares)
					if (units[pos.x][pos.y] == null
							|| units[pos.x][pos.y].hp == 0)
						actions.add(new DropAction(card, SingletonAction.positions[pos.x][pos.y]));

		if (!currentDeck().isEmpty())
			actions.add(SingletonAction.swapActions.get(card));

	}

	public void possibleActions(Unit unit, Position from, List<Action> actions) {

		if (unit.unitClass.card == Card.CRYSTAL)
			return;

		if (APLeft == 0 || unit.hp == 0 || APLeft == 0
				|| unit.p1Owner != p1Turn)
			return;

		// Movement and attack
		int d = unit.unitClass.speed;
		if (unit.unitClass.heal != null && unit.unitClass.heal.range > d)
			d = unit.unitClass.heal.range;
		if (unit.unitClass.attack != null && unit.unitClass.attack.range > d)
			d = unit.unitClass.attack.range;
		if (unit.unitClass.swap)
			d = Math.max(map.width, map.height);
		for (int x = d * (-1); x <= d; x++)
			for (int y = d * (-1); y <= d; y++) {
				if (from.x + x >= map.width || from.x + x < 0 || from.y + y >= map.height || from.y + y < 0)
					continue;
				
				final Position to = SingletonAction.positions[from.x + x][from.y +y];
				
				if (to.equals(from))
					continue;

				if (units[to.x][to.y] != null) {

					if (units[to.x][to.y].hp == 0) {

						if ((map.squares[to.x][to.y] == SquareType.DEPLOY_1 && !p1Turn)
								|| (map.squares[to.x][to.y] == SquareType.DEPLOY_2 && p1Turn)) {
							// NOT ALLOWED!
						} else if (unit.unitClass.heal != null
								&& from.distance(to) <= unit.unitClass.heal.range)
							actions.add(new UnitAction(from, to,
									UnitActionType.HEAL));
						else if (from.distance(to) <= unit.unitClass.speed)
							actions.add(new UnitAction(from, to,
									UnitActionType.MOVE));
					} else {
						final int distance = from.distance(to);
						if (unit.p1Owner != units[to.x][to.y].p1Owner
								&& distance <= unit.unitClass.attack.range) {
							if (!(distance > 1 && losBlocked(p1Turn, from, to)))
								actions.add(new UnitAction(from, to,
										UnitActionType.ATTACK));
						} else if (unit.p1Owner == units[to.x][to.y].p1Owner
								&& unit.unitClass.heal != null
								&& from.distance(to) <= unit.unitClass.heal.range
								&& !units[to.x][to.y].fullHealth()
								&& units[to.x][to.y].unitClass.card != Card.CRYSTAL)
							actions.add(new UnitAction(from, to,
									UnitActionType.HEAL));
						else if (unit.p1Owner == units[to.x][to.y].p1Owner
								&& unit.unitClass.swap
								&& units[to.x][to.y].unitClass.card != Card.CRYSTAL)
							actions.add(new UnitAction(from, to,
									UnitActionType.SWAP));
					}

				} else if (from.distance(to) <= unit.unitClass.speed)
					if ((map.squares[to.x][to.y] == SquareType.DEPLOY_1 && !p1Turn)
							|| (map.squares[to.x][to.y] == SquareType.DEPLOY_2 && p1Turn)) {
						// NOT ALLOWED!
					} else
						actions.add(new UnitAction(from, to,
								UnitActionType.MOVE));
			}
	}

	public void update(List<Action> actions) {
		for (final Action action : actions)
			update(action);
	}

	public void update(Action action) {

		try {
			chainTargets.clear();

			if (action instanceof EndTurnAction || APLeft <= 0)
				endTurn();

			if (action instanceof DropAction) {

				final DropAction drop = (DropAction) action;

				// Not a type in current players hand
				if (!currentHand().contains(drop.type))
					return;

				// Unit
				if (drop.type.type == CardType.UNIT) {

					// Not current players deploy square
					if (map.squares[drop.to.x][drop.to.y] == SquareType.DEPLOY_1
							&& !p1Turn)
						return;
					if (map.squares[drop.to.x][drop.to.y] == SquareType.DEPLOY_2
							&& p1Turn)
						return;

					deploy(drop.type, drop.to);

				}

				// Equipment
				if (drop.type.type == CardType.ITEM) {

					// Not a unit square or crystal
					if (units[drop.to.x][drop.to.y] == null
							|| units[drop.to.x][drop.to.y].unitClass.card == Card.CRYSTAL)
						return;

					if (units[drop.to.x][drop.to.y].p1Owner != p1Turn)
						return;

					if (drop.type == Card.REVIVE_POTION
							&& (units[drop.to.x][drop.to.y].unitClass.card == Card.CRYSTAL || units[drop.to.x][drop.to.y]
									.fullHealth()))
						return;

					if (drop.type != Card.REVIVE_POTION
							&& units[drop.to.x][drop.to.y].hp == 0)
						return;

					if (units[drop.to.x][drop.to.y].equipment
							.contains(drop.type))
						return;

					equip(drop.type, drop.to);

				}

				// Spell
				if (drop.type.type == CardType.SPELL)
					dropInferno(drop.to);

				return;

			}

			if (action instanceof UnitAction) {

				final UnitAction ua = (UnitAction) action;

				if (units[ua.from.x][ua.from.y] == null)
					return;

				final Unit unit = units[ua.from.x][ua.from.y];

				if (unit == null)
					return;
				
				if (unit.p1Owner != p1Turn)
					return;

				if (unit.hp == 0)
					return;

				// Move
				if (units[ua.to.x][ua.to.y] == null
						|| (unit.p1Owner == units[ua.to.x][ua.to.y].p1Owner
								&& units[ua.to.x][ua.to.y].hp == 0 && unit.unitClass.heal == null)
						|| (unit.p1Owner != units[ua.to.x][ua.to.y].p1Owner && units[ua.to.x][ua.to.y].hp == 0)) {

					if (ua.from.distance(ua.to) > units[ua.from.x][ua.from.y].unitClass.speed)
						return;

					if (map.squares[ua.to.x][ua.to.y] == SquareType.DEPLOY_1
							&& !unit.p1Owner)
						return;

					if (map.squares[ua.to.x][ua.to.y] == SquareType.DEPLOY_2
							&& unit.p1Owner)
						return;

					move(unit, ua.from, ua.to);
					return;

				} else {

					final Unit other = units[ua.to.x][ua.to.y];

					// Swap and heal
					if (unit.p1Owner == other.p1Owner) {
						if (unit.unitClass.swap
								&& units[ua.to.x][ua.to.y].unitClass.card != Card.CRYSTAL
								&& units[ua.to.x][ua.to.y].hp != 0) {
							swap(unit, ua.from, other, ua.to);
							return;
						}
						if (unit.unitClass.heal == null)
							return;
						if (ua.from.distance(ua.to) > unit.unitClass.heal.range)
							return;
						if (other.fullHealth())
							return;
						heal(unit, ua.from, other);
						return;
					}

					// Attack
					final int distance = ua.from.distance(ua.to);
					if (unit.unitClass.attack != null
							&& distance > unit.unitClass.attack.range)
						return;

					if (distance > 1 && losBlocked(p1Turn, ua.from, ua.to))
						return;

					if (other == null)
						return;
					
					attack(unit, ua.from, other, ua.to);
					return;

				}
			}

			if (action instanceof SwapCardAction) {

				final Card card = ((SwapCardAction) action).card;

				if (currentHand().contains(card)) {

					currentDeck().add(card);
					currentHand().remove(card);
					APLeft--;

				}

			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Using the Bresenham-based super-cover line algorithm
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean losBlocked(boolean p1, Position from, Position to) {

		if (from.distance(to) == 1
				|| (from.getDirection(to).isDiagonal() && from.distance(to) == 2))
			return false;

		for (final Position pos : CachedLines.supercover(from, to)) {
			if (pos.equals(from) || pos.equals(to))
				continue;

			if (units[pos.x][pos.y] != null
					&& units[pos.x][pos.y].p1Owner != p1
					&& units[pos.x][pos.y].hp != 0)
				return true;
		}

		return false;

	}

	private void dropInferno(Position to) throws Exception {

		for (int x = -1; x <= 1; x++)
			for (int y = -1; y <= 1; y++) {
				if (to.x + x < 0 || to.x + x >= map.width || to.y + y < 0 || to.y + y >= map.height)
					continue;
				final Position pos = SingletonAction.positions[to.x + x][to.y + y];
				if (units[pos.x][pos.y] != null
						&& units[pos.x][pos.y].p1Owner != p1Turn) {
					if (units[pos.x][pos.y].hp == 0) {
						units[pos.x][pos.y] = null;
						continue;
					}
					double damage = INFERNO_DAMAGE;
					if (units[pos.x][pos.y].unitClass.card == Card.CRYSTAL) {
						final int bonus = assaultBonus();
						damage += bonus;
					}
					final double resistance = units[pos.x][pos.y].resistance(
							this, pos, AttackType.Magical);
					damage = damage * ((100d - resistance) / 100d);
					units[pos.x][pos.y].hp -= damage;
					if (units[pos.x][pos.y].hp <= 0)
						if (units[pos.x][pos.y].unitClass.card == Card.CRYSTAL) {
							checkWinOnCrystals(p1Turn ? 2 : 1);
							units[pos.x][pos.y] = null;
						} else {
							units[pos.x][pos.y].hp = 0;
							checkWinOnUnits(p1Turn ? 2 : 1);
						}
				}
			}

		currentHand().remove(Card.INFERNO);
		APLeft--;

	}

	private void attack(Unit attacker, Position attPos, Unit defender, Position defPos) throws Exception {
		if (attacker.unitClass.attack == null)
			return;
		if (defender.hp == 0) {
			units[defPos.x][defPos.y] = null;
			move(attacker, attPos, defPos);
			checkWinOnUnits(p1Turn ? 2 : 1);
		} else {
			int damage = attacker.damage(this, attPos, defender, defPos);
			if (defender.unitClass.card == Card.CRYSTAL) {
				final int bonus = assaultBonus();
				damage += bonus;
			}
			defender.hp -= damage;
			if (defender.hp <= 0) {
				defender.hp = 0;
				if (defender.unitClass.card == Card.CRYSTAL) {
					units[defPos.x][defPos.y] = null;
					checkWinOnCrystals(p1Turn ? 2 : 1);
				} else {
					units[defPos.x][defPos.y].hp = 0;
					checkWinOnUnits(p1Turn ? 2 : 1);
				}
			}
			if (attacker.unitClass.attack.push)
				push(defender, attPos, defPos);
			if (attacker.unitClass.attack.chain)
				chain(attacker,
						attPos,
						defPos,
						Direction.direction(defPos.x - attPos.x, defPos.y
								- attPos.y), 1);
		}
		attacker.equipment.remove(Card.SCROLL);
		APLeft--;
	}

	private void chain(Unit attacker, Position attPos, Position from,
			Direction dir, int jump) throws Exception {

		if (jump >= 3)
			return;

		final Position bestPos = nextJump(from, dir);

		// Attack
		if (bestPos != null) {
			chainTargets.add(bestPos);
			int damage = attacker.damage(this, attPos,
					units[bestPos.x][bestPos.y], bestPos);
			if (jump == 1)
				damage = (int) (damage * 0.75);
			else if (jump == 2)
				damage = (int) (damage * 0.56);
			else
				System.out.println("Illegal number of jumps!");

			if (units[bestPos.x][bestPos.y].unitClass.card == Card.CRYSTAL)
				damage += assaultBonus();

			units[bestPos.x][bestPos.y].hp -= damage;
			if (units[bestPos.x][bestPos.y].hp <= 0) {
				units[bestPos.x][bestPos.y].hp = 0;
				if (units[bestPos.x][bestPos.y].unitClass.card == Card.CRYSTAL) {
					checkWinOnCrystals(p1Turn ? 2 : 1);
					units[bestPos.x][bestPos.y] = null;
				} else {
					units[bestPos.x][bestPos.y].hp = 0;
					checkWinOnUnits(p1Turn ? 2 : 1);
				}
			}
			chain(attacker, attPos, bestPos, from.getDirection(bestPos),
					jump + 1);
		}

	}

	private Position nextJump(Position from, Direction dir) {

		int bestValue = 0;
		Position bestPos = null;

		// Find best target
		for (int newDirX = -1; newDirX <= 1; newDirX++)
			for (int newDirY = -1; newDirY <= 1; newDirY++) {
				if (newDirX == 0 && newDirY == 0)
					continue;

				if (from.x + newDirX < 0 || from.x + newDirX >= map.width || from.y + newDirY < 0 || from.y + newDirY >= map.height)
					continue;
				
				final Position newPos = SingletonAction.positions[from.x + newDirX][from.y + newDirY];
				
				if (units[newPos.x][newPos.y] != null
						&& units[newPos.x][newPos.y].p1Owner != p1Turn
						&& units[newPos.x][newPos.y].hp > 0) {

					final Direction newDir = Direction.direction(newDirX,
							newDirY);

					if (newDir.opposite(dir))
						continue;

					final int chainValue = chainValue(dir, newDir);

					if (chainValue > bestValue) {
						bestPos = newPos;
						bestValue = chainValue;
					}
				}
			}

		return bestPos;
	}

	private int chainValue(Direction dir, Direction newDir) {

		if (dir.equals(newDir))
			return 10;

		int value = 1;

		if (!newDir.isDiagonal())
			value += 4;

		if (newDir.isNorth())
			value += 2;

		if (newDir.isEast())
			value += 1;

		return value;

	}

	private int assaultBonus() {
		int bonus = 0;
		for (final Position pos : map.assaultSquares)
			if (units[pos.x][pos.y] != null
					&& units[pos.x][pos.y].p1Owner == p1Turn
					&& units[pos.x][pos.y].hp != 0)
				bonus += ASSAULT_BONUS;
		return bonus;
	}

	private void checkWinOnUnits(int p) {

		if (!aliveOnUnits(p))
			isTerminal = true;

	}

	private void checkWinOnCrystals(int p) {

		if (!aliveOnCrystals(p))
			isTerminal = true;

	}

	public int getWinner() {

		if (turn >= TURN_LIMIT)
			return 0;
		
		boolean p1Alive = true;
		boolean p2Alive = true;

		if (!aliveOnCrystals(1) || !aliveOnUnits(1))
			p1Alive = false;

		if (!aliveOnCrystals(2) || !aliveOnUnits(2))
			p2Alive = false;

		if (p1Alive == p2Alive)
			return 0;

		if (p1Alive)
			return 1;

		if (p2Alive)
			return 2;

		return 0;

	}

	private boolean aliveOnCrystals(int player) {

		for (final Position pos : crystals(player))
			if (units[pos.x][pos.y] != null
					&& units[pos.x][pos.y].unitClass.card == Card.CRYSTAL
					&& units[pos.x][pos.y].hp > 0)
				return true;

		return false;

	}

	private List<Position> crystals(int player) {
		if (player == 1)
			return map.p1Crystals;
		if (player == 2)
			return map.p2Crystals;
		return null;
	}

	private boolean aliveOnUnits(int player) {

		if (deck(player).hasUnits() || hand(player).hasUnits())
			return true;

		for (int x = 0; x < map.width; x++)
			for (int y = 0; y < map.height; y++)
				if (units[x][y] != null && units[x][y].p1Owner == (player == 1)
						&& units[x][y].unitClass.card != Card.CRYSTAL)
					return true;

		return false;

	}

	private CardSet deck(int player) {
		if (player == 1)
			return p1Deck;
		if (player == 2)
			return p2Deck;
		return null;
	}

	private CardSet hand(int player) {
		if (player == 1)
			return p1Hand;
		if (player == 2)
			return p2Hand;
		return null;
	}

	private void push(Unit defender, Position attPos, Position defPos)
			throws Exception {

		if (defender.unitClass.card == Card.CRYSTAL)
			return;

		int x = 0;
		int y = 0;

		if (attPos.x > defPos.x)
			x = -1;
		if (attPos.x < defPos.x)
			x = 1;
		if (attPos.y > defPos.y)
			y = -1;
		if (attPos.y < defPos.y)
			y = 1;

		if (defPos.x + x >= map.width || defPos.x + x < 0 || defPos.y + y >= map.height || defPos.y + y < 0)
			return;

		final Position newPos = SingletonAction.positions[defPos.x + x][defPos.y + y];
		
		if (units[newPos.x][newPos.y] != null
				&& units[newPos.x][newPos.y].hp > 0)
			return;

		if (map.squareAt(newPos) == SquareType.DEPLOY_1 && !defender.p1Owner)
			return;

		if (map.squareAt(newPos) == SquareType.DEPLOY_2 && defender.p1Owner)
			return;

		if (units[defPos.x][defPos.y] != null) {
			units[defPos.x][defPos.y] = null;
		}

		units[newPos.x][newPos.y] = defender;

	}

	private void heal(Unit healer, Position pos, Unit unitTo) {

		int power = healer.power(this, pos);

		if (unitTo.hp == 0)
			power *= healer.unitClass.heal.revive;
		else
			power *= healer.unitClass.heal.heal;
		
		unitTo.heal(power);
		healer.equipment.remove(Card.SCROLL);
		
		APLeft--;

	}

	private void swap(Unit unitFrom, Position from, Unit unitTo, Position to) {
		units[from.x][from.y] = unitTo;
		units[to.x][to.y] = unitFrom;
		APLeft--;
	}

	private void move(Unit unit, Position from, Position to) throws Exception {
		units[from.x][from.y] = null;
		units[to.x][to.y] = unit;
		APLeft--;
	}

	private void equip(Card card, Position pos) {
		if (card == Card.REVIVE_POTION) {
			if (units[pos.x][pos.y].hp == 0)
				units[pos.x][pos.y].heal(POTION_REVIVE);
			else
				units[pos.x][pos.y].heal(POTION_HEAL);
		} else
			units[pos.x][pos.y].equip(card, this);
		currentHand().remove(card);
		APLeft--;
	}

	private void deploy(Card card, Position pos) {
		units[pos.x][pos.y] = new Unit(card, p1Turn);
		currentHand().remove(card);
		APLeft--;
	}

	private void endTurn() throws Exception {
		removeDying(p1Turn);
		checkWinOnUnits(1);
		checkWinOnUnits(2);
		checkWinOnCrystals(p1Turn ? 2 : 1);
		if (turn >= TURN_LIMIT)
			isTerminal = true;
		if (!isTerminal) {
			drawCards();
			p1Turn = !p1Turn;
			drawCards();
			APLeft = ACTION_POINTS;
			turn++;
		}
		
	}

	public void dealCards() {
		while (!legalStartingHand(p1Hand))
			dealCards(1);

		while (!legalStartingHand(p2Hand))
			dealCards(2);
	}

	private void dealCards(int player) {
		if (player == 1) {
			p1Deck.addAll(p1Hand);
			p1Hand.clear();
			// Collections.shuffle(p1Deck);
			drawHandFrom(p1Deck, p1Hand);
		} else if (player == 2) {
			p2Deck.addAll(p2Hand);
			p2Hand.clear();
			// Collections.shuffle(p2Hand);
			drawHandFrom(p2Deck, p2Hand);
		}

	}

	private boolean legalStartingHand(CardSet hand) {
		if (hand.size != 6)
			return false;

		if (hand.units() == REQUIRED_UNITS)
			return true;

		return false;

	}

	private void drawHandFrom(CardSet deck, CardSet hand) {

		Card card = null;
		while (!deck.isEmpty() && hand.size < 6) {
			if (RANDOMNESS)
				card = deck.random();
			else
				card = deck.determined();
			hand.add(card);
			deck.remove(card);
		}

	}

	private void drawCards() {

		drawHandFrom(currentDeck(), currentHand());

	}

	public CardSet currentHand() {
		if (p1Turn)
			return p1Hand;
		return p2Hand;
	}

	public CardSet currentDeck() {
		if (p1Turn)
			return p1Deck;
		return p2Deck;
	}

	public void removeDying(boolean p1) throws Exception {
		for (int x = 0; x < map.width; x++)
			for (int y = 0; y < map.height; y++)
				if (units[x][y] != null && units[x][y].p1Owner == p1
						&& units[x][y].hp == 0) {
					units[x][y] = null;
				}
	}

	public GameState copy() {
		final Unit[][] un = new Unit[map.width][map.height];
		for (int x = 0; x < map.width; x++)
			for (int y = 0; y < map.height; y++)
				if (units[x][y] != null)
					un[x][y] = units[x][y].copy();
		final CardSet p1h = new CardSet(p1Hand.seed);
		p1h.imitate(p1Hand);
		final CardSet p2h = new CardSet(p2Hand.seed);
		p2h.imitate(p2Hand);
		final CardSet p1d = new CardSet(p1Deck.seed);
		p1d.imitate(p1Deck);
		final CardSet p2d = new CardSet(p2Deck.seed);
		p2d.imitate(p2Deck);

		return new GameState(map, p1Turn, turn, APLeft, un, p1h, p2h, p1d, p2d,
				chainTargets, isTerminal);
	}

	public void imitate(GameState state) {

		map = state.map;
		if (units.length != state.units.length
				|| (state.units.length > 0 && units[0].length != state.units[0].length))
			units = new Unit[map.width][map.height];

		for (int x = 0; x < map.width; x++)
			for (int y = 0; y < map.height; y++) {
				if (units[x][y] != null) 
					units[x][y] = null;
				if (state.units[x][y] != null) {
					units[x][y] = new Unit(null, false);
					units[x][y].imitate(state.units[x][y]);
				}
			}
		p1Hand.imitate(state.p1Hand);
		p2Hand.imitate(state.p2Hand);
		p1Deck.imitate(state.p1Deck);
		p2Deck.imitate(state.p2Deck);
		isTerminal = state.isTerminal;
		p1Turn = state.p1Turn;
		turn = state.turn;
		APLeft = state.APLeft;
		map = state.map;
		// chainTargets.clear();
		// chainTargets.addAll(state.chainTargets); // NOT NECESSARY

	}
	
	public long simpleHash() {
		final int prime = 1193;
		long result = 1;
		result = prime * result + (isTerminal ? 0 : 1);
		result = prime * result + p1Hand.hashCode();
		result = prime * result + p2Deck.hashCode();
		result = prime * result + p2Hand.hashCode();
		result = prime * result + p1Deck.hashCode();

		for (int x = 0; x < map.width; x++)
			for (int y = 0; y < map.height; y++)
				if (units[x][y] != null)
					result = prime * result + units[x][y].hash(x, y);

		return result;
	}

	public long hash() {
		final int prime = 1193;
		long result = 1;
		result = prime * result + APLeft;
		result = prime * result + turn;
		result = prime * result + (isTerminal ? 0 : 1);
		result = prime * result + p1Hand.hashCode();
		result = prime * result + p2Deck.hashCode();
		result = prime * result + p2Hand.hashCode();
		result = prime * result + p1Deck.hashCode();

		for (int x = 0; x < map.width; x++)
			for (int y = 0; y < map.height; y++)
				if (units[x][y] != null)
					result = prime * result + units[x][y].hash(x, y);

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GameState other = (GameState) obj;
		if (APLeft != other.APLeft)
			return false;
		if (isTerminal != other.isTerminal)
			return false;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		if (p1Deck == null) {
			if (other.p1Deck != null)
				return false;
		} else if (!p1Deck.equals(other.p1Deck))
			return false;
		if (p1Hand == null) {
			if (other.p1Hand != null)
				return false;
		} else if (!p1Hand.equals(other.p1Hand))
			return false;
		if (p1Turn != other.p1Turn)
			return false;
		if (p2Deck == null) {
			if (other.p2Deck != null)
				return false;
		} else if (!p2Deck.equals(other.p2Deck))
			return false;
		if (p2Hand == null) {
			if (other.p2Hand != null)
				return false;
		} else if (!p2Hand.equals(other.p2Hand))
			return false;
		if (!Arrays.deepEquals(units, other.units))
			return false;
		if (turn != other.turn)
			return false;
		return true;
	}

	public void print() {
		final List<Unit> p1Units = new ArrayList<Unit>();
		final List<Unit> p2Units = new ArrayList<Unit>();
		for (int y = 0; y < units[0].length; y++) {
			System.out.print("|");
			for (int x = 0; x < units.length; x++)
				if (units[x][y] == null)
					System.out.print("__|");
				else {
					System.out.print(units[x][y].unitClass.card.name()
							.substring(0, 2) + "|");
					if (units[x][y].p1Owner)
						p1Units.add(units[x][y]);
					else
						p2Units.add(units[x][y]);
				}
			System.out.print("\n");
		}
		System.out.println(p1Hand);
		for (final Unit unit : p1Units)
			System.out.println(unit);
		System.out.println(p2Hand);
		for (final Unit unit : p2Units)
			System.out.println(unit);

	}

	public SquareType squareAt(Position pos) {
		return map.squares[pos.x][pos.y];
	}

	public Unit unitAt(Position pos) {
		return units[pos.x][pos.y];
	}

	public Unit unitAt(int x, int y) {
		return units[x][y];
	}

	public int cardsLeft(int p) {
		if (p == 1)
			return p1Deck.size + p1Hand.size;
		else if (p == 2)
			return p2Deck.size + p2Hand.size;
		return -1;
	}

	public void returnUnits() {
		for (int x = 0; x < map.width; x++)
			for (int y = 0; y < map.height; y++)
				if (units[x][y] != null) 
					units[x][y] = null;
	}

	public void hideCards(boolean p1) {
		if (p1){
			for(Card card : Card.values())
				for(int i = 0; i < p1Hand.count(card); i++)
					p1Deck.add(card);
			p1Hand.clear();
		} else {
			for(Card card : Card.values())
				for(int i = 0; i < p2Hand.count(card); i++)
					p2Deck.add(card);
			p2Hand.clear();
		}
	}

}
