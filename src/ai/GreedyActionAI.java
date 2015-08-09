package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import action.Action;
import ai.evaluation.IStateEvaluator;
import ai.util.ActionPruner;

public class GreedyActionAI implements AI {

	private final List<Action> actions;
	private final IStateEvaluator evaluator;
	private final ActionPruner pruner;
	
	public List<Double> branching;
	public List<Double> prunedBranching;
	
	public GreedyActionAI(IStateEvaluator evaluator) {
		super();
		this.evaluator = evaluator;
		actions = new ArrayList<Action>();
		this.pruner = new ActionPruner();
		this.branching = new ArrayList<Double>();
		this.prunedBranching = new ArrayList<Double>();
	}

	@Override
	public Action act(GameState state, long ms) {

		Action best = null;
		double bestValue = -100000000;

		actions.clear();
		state.possibleActions(actions);
		branching.add((double)actions.size());
		pruner.prune(actions, state);
		prunedBranching.add((double)actions.size());
		Collections.shuffle(actions);
		for (final Action action : actions) {

			final GameState next = state.copy();
			double val = 0.0;
			next.update(action);
			val = evaluator.eval(next, state.p1Turn);
			
			if (val > bestValue) {
				bestValue = val;
				best = action;
			}

		}
		
		return best;
	}

	@Override
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String header() {
		String name = title()+"\n";
		name += "State evaluatior = " + evaluator.title() + "\n";
		return name;
	}

	@Override
	public String title() {
		return "GreedyAction";
	}

	@Override
	public AI copy() {
		return new GreedyActionAI(evaluator.copy());
	}

}
