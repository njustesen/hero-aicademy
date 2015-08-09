package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Card;
import model.Position;
import action.Action;
import action.SingletonAction;
import ai.util.ActionComparator;
import ai.util.ComplexActionComparator;

public class RandomHeuristicAI implements AI {

	private static final double PROP_HAND = 0.25;
	private final List<Integer> heightOrder;
	private final List<Integer> widthOrder;
	private final List<Integer> handOrder;
	private final List<Action> actions;
	private final List<Position> positions;
	private final List<Integer> idxs;
	private final ActionComparator comparator;
	private double prob;

	public RandomHeuristicAI(double prob) {
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
		this.comparator = new ComplexActionComparator();
		this.prob = prob;
	}

	@Override
	public Action act(GameState state, long ms) {
		return getActionLazy(state);
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
				if (!actions.isEmpty()){
					return semiRandom(actions,state);
				}
			}
			positions.clear();
			for (int x = 0; x < state.map.width; x++)
				for (int y = 0; y < state.map.height; y++)
					if (state.units[x][y] != null
							&& state.units[x][y].p1Owner == state.p1Turn
							&& state.units[x][y].hp > 0
							&& state.units[x][y].unitClass.card != Card.CRYSTAL)
						positions.add(new Position(x, y));
			Collections.shuffle(positions);
			for (final Position pos : positions) {
				actions.clear();
				state.possibleActions(state.units[pos.x][pos.y], pos,
						actions);
				if (!actions.isEmpty()){
					return semiRandom(actions, state);
				}
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
				state.possibleActions(state.units[pos.x][pos.y], pos, actions);
				if (!actions.isEmpty()){
					return semiRandom(actions, state);
				}
			}
			Collections.shuffle(idxs);
			for (final Integer i : idxs) {
				if (i >= state.currentHand().size)
					continue;
				actions.clear();
				state.possibleActions(state.currentHand().get(i), actions);
				if (!actions.isEmpty()){
					return semiRandom(actions, state);
				}
			}
		}

		return SingletonAction.endTurnAction;
	}

	private Action semiRandom(List<Action> actions, GameState state) {
		if (Math.random() < prob){
			comparator.sort(actions, state);
			return actions.get(0);
		}
		else return actions.get((int) (Math.random() * actions.size()));
	}

	@Override
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String header() {
		String name = title()+"\n";
		name += "Probability = " + prob + "\n";
		return name;
	}

	@Override
	public String title() {
		return "Random heuristic [prob:" + prob + "]";
	}

	@Override
	public AI copy() {
		return new RandomHeuristicAI(prob);
	}

}
