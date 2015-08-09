package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import action.Action;
import action.SingletonAction;
import ai.evaluation.IStateEvaluator;
import ai.evaluation.RolloutEvaluator;
import ai.evolution.OnlineIslandEvolution;
import ai.evolution.OnlineEvolution;
import ai.movesearch.BestNMovesSearcher;
import ai.movesearch.ParallelMoveSearcher;
import ai.movesearch.ValuedMove;

public class HybridAI implements AI {

	private final ParallelMoveSearcher searcher;
	private List<Action> actions;
	private List<ValuedMove> moves;
	private final IStateEvaluator evaluator;
	private int n;
	private int m;
	private RolloutEvaluator rolloutEvaluator;
	private OnlineIslandEvolution evolution;
	private int searchBudget;
	
	public HybridAI(IStateEvaluator evaluator, int searchBudget, int n, RolloutEvaluator rolloutEvaluator, int m, OnlineIslandEvolution evolution) {
		super();
		this.evaluator = evaluator;
		this.moves = new ArrayList<ValuedMove>();
		this.actions = new ArrayList<Action>();
		this.rolloutEvaluator = rolloutEvaluator;
		this.n = n;
		this.m = m;
		this.searcher = new ParallelMoveSearcher(n, searchBudget, evaluator);
		this.evolution = evolution;
		this.searchBudget = searchBudget;
	}

	@Override
	public Action act(GameState state, long ms) {

		if (!actions.isEmpty()) {
			final Action action = actions.get(0);
			actions.remove(0);
			return action;
		}
		
		// End game?
		/*
		if (endGame(state)){
			evolution.budget = searchBudget * 3;
			evolution.search(state);
			actions = evolution.actions;
			actions = moves.get(0).actions;
			final Action action = actions.get(0);
			actions.remove(0);
			return action;
		}
		*/
		
		// 1. GreedySearch
		moves = searcher.search(state);
		
		/*
		System.out.println("1: Greedy Search");
		for (ValuedMove move : moves)
			System.out.println(move);
		*/
		
		// 2. Rollout phase
		rolloutPhase(state);
		/*
		System.out.println("2: Rollouts");
		for (ValuedMove move : moves)
			System.out.println(move);
		*/
		// 3. Rolling horizon evolution
		rollingPhase(state);
		/*
		System.out.println("3: Rolling horizon");
		for (ValuedMove move : moves)
			System.out.println(move);
		
		System.out.println("---------------");
		*/

		if (moves.isEmpty())
			return SingletonAction.endTurnAction;
		
		actions = moves.get(0).actions;
		
		if (actions == null || actions.isEmpty())
			return SingletonAction.endTurnAction;
		
		final Action action = actions.get(0);
		actions.remove(0);
		
		return action;

	}

	private boolean endGame(GameState state) {
		if (!state.currentHand().hasUnits() && !state.currentDeck().hasUnits())
			return true;
		return false;
	}

	private void rollingPhase(GameState state) {
		
		GameState clone = new GameState(state.map);
		
		for(ValuedMove move : moves){
			if (!move.actions.contains(SingletonAction.endTurnAction))
				move.actions.add(SingletonAction.endTurnAction);
			clone.imitate(state);
			clone.update(move.actions);
			if (!clone.isTerminal){
				evolution.search(clone);
				if (evolution.actions.isEmpty())
					System.out.println("EMPTY");
				clone.update(evolution.actions);
			}
			move.value = evaluator.eval(clone, state.p1Turn);
		}
		
		Collections.sort(moves);
		List<ValuedMove> newMoves = new ArrayList<ValuedMove>();
		if (!moves.isEmpty())
			newMoves.addAll(moves.subList(moves.size()-1, moves.size()));
		moves = newMoves;
		
	}

	private void rolloutPhase(GameState state) {
		
		GameState clone = new GameState(null);
		
		for(ValuedMove move : moves){
			clone.imitate(state);
			clone.update(move.actions);
			move.value = rolloutEvaluator.eval(clone, state.p1Turn);
		}
		
		Collections.sort(moves);
		List<ValuedMove> newMoves = new ArrayList<ValuedMove>();
		int fromIndex = Math.max(0, moves.size()-m);
		int toIndex = moves.size();
		newMoves.addAll(moves.subList(fromIndex, toIndex));
		moves = newMoves;
		
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
		return "Hybrid";
	}

	@Override
	public AI copy() {
		return new HybridAI(evaluator.copy(), searchBudget, n, (RolloutEvaluator)(rolloutEvaluator.copy()), m, (OnlineIslandEvolution)(evolution.copy()));
	}
	
}
