package ai.util;

import java.util.ArrayList;
import java.util.List;

import model.Card;
import model.Position;
import model.SquareType;
import model.Unit;
import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.SwapCardAction;
import action.UnitAction;
import action.UnitActionType;

public class ComplexActionComparator extends ActionComparator {

	final List<Position> targets = new ArrayList<Position>();

	@Override
	public int value(Action action) {

		if (action instanceof EndTurnAction)
			if (state.APLeft == 0)
				return 10000;
			else
				return 0;

		if (action instanceof SwapCardAction)
			return 0;

		if (action instanceof DropAction) {
			final DropAction drop = ((DropAction) action);
			if (drop.type == Card.INFERNO) {
				targets.clear();
				spellTargets(drop.to, targets);
				int val = -500;
				for (final Position pos : targets)
					if (state.units[pos.x][pos.y].hp == 0)
						val += state.units[pos.x][pos.y].maxHP() * 2;
					else
						val += 300;
				return val;
			} else if (drop.type == Card.REVIVE_POTION) {
				if (state.units[drop.to.x][drop.to.y].hp == 0)
					return state.units[drop.to.x][drop.to.y].maxHP()
							+ state.units[drop.to.x][drop.to.y].equipment.size() * 200;
				else
					return state.units[drop.to.x][drop.to.y].maxHP() - state.units[drop.to.x][drop.to.y].hp - 300;
			} else if (drop.type == Card.SCROLL)
				return state.units[drop.to.x][drop.to.y].power(state, drop.to) * (state.units[drop.to.x][drop.to.y].hp / state.units[drop.to.x][drop.to.y].maxHP());
			else if (drop.type == Card.DRAGONSCALE)
				return state.units[drop.to.x][drop.to.y].power(state, drop.to) * (state.units[drop.to.x][drop.to.y].hp / state.units[drop.to.x][drop.to.y].maxHP());
			else if (drop.type == Card.RUNEMETAL)
				return state.units[drop.to.x][drop.to.y].power(state, drop.to) * (state.units[drop.to.x][drop.to.y].hp / state.units[drop.to.x][drop.to.y].maxHP());
			else if (drop.type == Card.SHINING_HELM)
				return state.units[drop.to.x][drop.to.y].power(state, drop.to) * (state.units[drop.to.x][drop.to.y].hp / state.units[drop.to.x][drop.to.y].maxHP());
			else
				return 200;
		} else if (action instanceof UnitAction)
			if (((UnitAction) action).type == UnitActionType.ATTACK) {
				final Unit defender = state.units[((UnitAction) action).to.x][((UnitAction) action).to.y];
				final Unit attacker = state.units[((UnitAction) action).from.x][((UnitAction) action).from.y];
				if (attacker.unitClass.attack.chain) {	
					if (defender.hp == 0)
						return defender.maxHP() * 2 + 200;
					else
						return attacker.power(state, ((UnitAction) action).from) + 200;
				} else if (defender.hp == 0)
					return defender.maxHP() * 2;
				else
					return attacker.power(state, ((UnitAction) action).from);
			} else if (((UnitAction) action).type == UnitActionType.HEAL) {
				final Unit target = state.units[((UnitAction) action).to.x][((UnitAction) action).to.y];
				if (target.hp == 0)
					return 1400;
				else
					return target.maxHP() - target.hp;
			} else if (((UnitAction) action).type == UnitActionType.SWAP)
				return 0;
			else if (((UnitAction) action).type == UnitActionType.MOVE) {
				if (state.units[((UnitAction) action).to.x][((UnitAction) action).to.y] != null)
					return state.units[((UnitAction) action).to.x][((UnitAction) action).to.y].maxHP() * 2;
				if (state.map.squares[((UnitAction) action).to.x][((UnitAction) action).to.y] == SquareType.NONE)
					return 0;
				else
					return 30;
			}
		return 0;
	}

	private void spellTargets(Position to, List<Position> spellTargets) {
		for (int x = to.x - 1; x <= to.x + 1; x++)
			for (int y = to.y - 1; y <= to.y + 1; y++)
				if (x >= 0 && x < state.map.width && y >= 0
						&& y < state.map.height) {
					final Unit unit = state.unitAt(x, y);
					if (unit != null && unit.p1Owner != state.p1Turn)
						spellTargets.add(new Position(x, y));
				}
	}
}