package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import action.Action;
import action.EndTurnAction;
import action.SingletonAction;
import ai.util.ActionPruner;
import ai.util.ComplexActionComparator;

public abstract class Genome implements Comparable<Genome> {

	private ActionPruner pruner;
	private ComplexActionComparator comparator;

	public static Random random;
	public List<Action> actions;
	public double value;
	public int visits;


	public Genome() {
		super();
		pruner = new ActionPruner();
		comparator = new ComplexActionComparator();
		actions = new ArrayList<Action>();
		random = new Random();
		value = 0;
		visits = 0;
	}

	public void random(GameState state) {
		final List<Action> possible = new ArrayList<Action>();
		actions.clear();
		visits = 0;
		value = 0;
		final boolean p1Turn = state.p1Turn;
		while (!state.isTerminal && p1Turn == state.p1Turn) {
			state.possibleActions(possible);
			pruner.prune(possible, state);
			if (p1Turn == state.p1Turn && possible.isEmpty()) {
				actions.add(SingletonAction.endTurnAction);
				break;
			}
			/*
			if (random.nextFloat() <= 0.2){
				comparator.state = state;
				Collections.sort(possible, comparator);
				actions.add(possible.get(0));
			} else {*/
				final int idx = (int) (Math.random() * possible.size());
				actions.add(possible.get(idx));
				state.update(possible.get(idx));
			//}
		}

	}

	public void crossover(Genome a, Genome b, GameState state) {
		actions.clear();
		visits = 0;
		value = 0;
		final ArrayList<Action> possible = new ArrayList<Action>();
		for (int i = 0; i < Math.max(a.actions.size(), b.actions.size()); i++) {
			state.possibleActions(possible);
			pruner.prune(possible, state);
			if (possible.isEmpty()){
				actions.add(new EndTurnAction());
				break;
			}
			if (random.nextBoolean() && hasAction(a, possible, i))
				actions.add(a.actions.get(i));
			else if (hasAction(b, possible, i))
				actions.add(b.actions.get(i));
			else if (hasAction(a, possible, i))
				actions.add(a.actions.get(i));
			else {
				if (hasAction(a, possible, i+1))
					actions.add(a.actions.get(i+1));
				else if (hasAction(b, possible, i+1))
					actions.add(b.actions.get(i+1));
				else
					actions.add(possible.get(random.nextInt(possible.size())));
			}
				
			state.update(actions.get(i));
		}
	}

	private boolean hasAction(Genome genome, ArrayList<Action> possible, int index) {
		if (genome.actions.size() <= index)
			return false;

		if (possible.contains(genome.actions.get(index)))
			return true;

		return false;
	}

	public void mutate(GameState state) {

		if (actions.isEmpty())
			return;
		
		final int mutIdx = random.nextInt(actions.size());
		final List<Action> possible = new ArrayList<Action>();
		int i = 0;
		for (final Action action : actions) {
			if (i == mutIdx)
				actions.set(mutIdx, newAction(state, action));
			else if (i > mutIdx) {
				state.possibleActions(possible);
				if (!possible.contains(action))
					if (actions.size() < i+1 && possible.contains(actions.get(i+1))) // Might never be activated
						actions.set(i, actions.get(i+1));
					else
						actions.set(i, newAction(state, action));
			}
			state.update(actions.get(i));
			i++;
		}
		
	}

	private Action newAction(GameState state, Action action) {

		final List<Action> possibleActions = new ArrayList<Action>();
		state.possibleActions(possibleActions);
		pruner.prune(possibleActions, state);
		possibleActions.remove(action);

		if (possibleActions.isEmpty())
			return SingletonAction.endTurnAction;
		/*
		if (random.nextFloat() <= 0.4){
			comparator.state = state;
			Collections.sort(possibleActions, comparator);
			return possibleActions.get(0);
		}
		*/
		final int idx = (int) (Math.random() * possibleActions.size());

		return possibleActions.get(idx);
	}

	@Override
	public int compareTo(Genome other) {
		if (fitness() == other.fitness())
			return 0;
		if (fitness() > other.fitness())
			return -1;
		return 1;
	}

	public abstract double fitness();
	
	public double avgValue() {
		if (visits == 0)
			return 0;
		return value / visits;
	}

	public boolean isLegal(GameState clone) {
		final ArrayList<Action> possible = new ArrayList<Action>();
		for (final Action action : actions) {
			clone.possibleActions(possible);
			if (!possible.contains(action))
				return false;
			clone.update(action);
		}
		return true;
	}

	@Override
	public String toString() {
		return "Genome [value=" + value + ", visits="+ visits + ", fitness=" + fitness() + "]";
	}

	
	
}
