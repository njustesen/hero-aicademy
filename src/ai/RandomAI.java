package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Card;
import model.Position;
import action.Action;
import action.SingletonAction;
import ai.util.AiStatistics;
import ai.util.RAND_METHOD;

public class RandomAI implements AI {

	private static final double PROP_HAND = 0.25;
	public RAND_METHOD randMethod;
	private final List<Integer> heightOrder;
	private final List<Integer> widthOrder;
	private final List<Integer> handOrder;
	private final List<Action> actions;
	private final List<Position> positions;
	private final List<Integer> idxs;

	public RandomAI(RAND_METHOD randMethod) {
		this.randMethod = randMethod;
		positions = new ArrayList<Position>();
		actions = new ArrayList<Action>();
		idxs = new ArrayList<Integer>();
		for (int i = 0; i < 6; i++)
			idxs.add(i);
		heightOrder = new ArrayList<Integer>();
		widthOrder = new ArrayList<Integer>();
		handOrder = new ArrayList<Integer>();
		for (int x = 0; x < 9; x++)
			widthOrder.add(x);
		for (int y = 0; y < 5; y++)
			heightOrder.add(y);
		for (int h = 0; h < 6; h++)
			handOrder.add(h);
	}

	@Override
	public Action act(GameState state, long ms) {
		Action selected = null;
		switch (randMethod) {
		case BRUTE:
			selected = getActionBrute(state);
			break;
		case TREE:
			selected = getActionLazy(state);
			break;
		}
		return selected;
	}

	private Action getActionBrute(GameState state) {
		actions.clear();
		state.possibleActions(actions);
		if (actions.isEmpty())
			return SingletonAction.endTurnAction;

		final int idx = (int) (Math.random() * actions.size());
		final Action selected = actions.get(idx);
		return selected;
	}

	public Action getActionLazy(GameState state) {

		if (state.APLeft == 0)
			return SingletonAction.endTurnAction;

		if (Math.random() < PROP_HAND) {
			final List<Integer> idxs = new ArrayList<Integer>(state
					.currentHand().size);
			for (int i = 0; i < state.currentHand().size; i++)
				idxs.add(i);
			Collections.shuffle(idxs);
			for (final Integer i : idxs) {
				actions.clear();
				state.possibleActions(state.currentHand().get(i), actions);
				if (!actions.isEmpty())
					return actions.get((int) (Math.random() * actions.size()));
			}
			positions.clear();
			for (int x = 0; x < state.map.width; x++)
				for (int y = 0; y < state.map.height; y++)
					if (state.units[x][y] != null
							&& state.units[x][y].p1Owner == state.p1Turn
							&& state.units[x][y].hp > 0
							&& state.units[x][y].unitClass.card != Card.CRYSTAL)
						positions.add(SingletonAction.positions[x][y]);
			Collections.shuffle(positions);
			for (final Position pos : positions) {
				actions.clear();
				state.possibleActions(state.units[pos.x][pos.y], pos,
						actions);
				if (!actions.isEmpty())
					return actions.get((int) (Math.random() * actions.size()));
			}
		} else {
			positions.clear();
			for (int x = 0; x < state.map.width; x++)
				for (int y = 0; y < state.map.height; y++)
					if (state.units[x][y] != null
							&& state.units[x][y].p1Owner == state.p1Turn
							&& state.units[x][y].hp > 0
							&& state.units[x][y].unitClass.card != Card.CRYSTAL)
						positions.add(SingletonAction.positions[x][y]);
			Collections.shuffle(positions);
			for (final Position pos : positions) {
				actions.clear();
				state.possibleActions(state.units[pos.x][pos.y], pos,
						actions);
				
				if (!actions.isEmpty())
					return actions.get((int) (Math.random() * actions.size()));
			}
			Collections.shuffle(idxs);
			for (final Integer i : idxs) {
				if (i >= state.currentHand().size)
					continue;
				actions.clear();
				state.possibleActions(state.currentHand().get(i), actions);
				if (!actions.isEmpty())
					return actions.get((int) (Math.random() * actions.size()));
			}
		}

		return SingletonAction.endTurnAction;
	}

	@Override
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String header() {
		String name = title()+"\n";
		name += "Randomization method = " + randMethod.name().toLowerCase() + "\n";
		return name;
	}

	@Override
	public String title() {
		return "Random";
	}

	@Override
	public AI copy() {
		return new RandomAI(randMethod);
	}

}
