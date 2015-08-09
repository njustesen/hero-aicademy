package ai.util;

import game.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Card;
import model.Position;
import model.Unit;
import action.Action;
import action.DropAction;
import action.SwapCardAction;

public class ActionPruner {

	final Map<DropAction, List<Position>> spellTargets;
	final List<Action> pruned;

	public ActionPruner() {
		super();
		spellTargets = new HashMap<DropAction, List<Position>>();
		pruned = new ArrayList<Action>();
	}

	public void prune(List<Action> actions, GameState state) {

		pruned.clear();
		spellTargets.clear();

		for (final Action action : actions)
			if (action instanceof DropAction) {
				final DropAction dropAction = ((DropAction) action);
				if (dropAction.type == Card.INFERNO)
					spellTargets.put(((DropAction) action),
							spellTargets(dropAction.to, state));
			} else if (action instanceof SwapCardAction)
				if (onlyCard(state, ((SwapCardAction)action).card))
					pruned.add(action);

		for (final DropAction spell : spellTargets.keySet())
			if (spellTargets.get(spell).isEmpty())
				pruned.add(spell);

		for (final DropAction spell : spellTargets.keySet())
			if (!pruned.contains(spell))
				if (sameOrBetterSpellEffect(spellTargets, spell, pruned))
					pruned.add(spell);
		
		/*
		Set<Integer> states = new HashSet<Integer>();
		for (final Action action : actions){
			GameState next = state.copy();
			if (pruned.contains(action))
				continue;
			if (action instanceof UnitAction || action instanceof DropAction){
				next.update(action);
				if (states.contains(next.hash())){
					pruned.add(action);
				} else {
					states.add(next.hash());
				}
			}
		}
	*/	
		actions.removeAll(pruned);

	}

	private boolean onlyCard(GameState state, Card card) {
		
		if (state.p1Turn)
			return state.p1Hand.count(card) == 1;
		
		return state.p2Hand.count(card) == 1;	
		
	}

	private boolean sameOrBetterSpellEffect(Map<DropAction, List<Position>> spellTargets, DropAction spell, List<Action> pruned) {

		int same = 0;
		for (final Action action : spellTargets.keySet()) {
			if (action.equals(spell) || pruned.contains(action))
				continue;
			same = 0;
			for (final Position pos : spellTargets.get(spell))
				if (spellTargets.get(action).contains(pos))
					same++;
			if (same == spellTargets.get(spell).size())
				return true;
		}

		return false;
	}

	private List<Position> spellTargets(Position to, GameState state) {
		final List<Position> targets = new ArrayList<Position>();
		for (int x = to.x - 1; x <= to.x + 1; x++)
			for (int y = to.y - 1; y <= to.y + 1; y++)
				if (x >= 0 && x < state.map.width && y >= 0
						&& y < state.map.height) {
					final Unit unit = state.unitAt(x, y);
					if (unit != null && unit.p1Owner != state.p1Turn)
						targets.add(new Position(x, y));
				}
		return targets;
	}

}
