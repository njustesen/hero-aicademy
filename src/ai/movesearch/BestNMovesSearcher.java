package ai.movesearch;

import game.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import action.Action;
import action.SingletonAction;
import ai.evaluation.HeuristicEvaluator;
import ai.evaluation.IStateEvaluator;
import ai.util.ActionPruner;

public class BestNMovesSearcher {

	Map<Long, Integer> transTable = new HashMap<Long, Integer>();

	HeuristicEvaluator evalutator = new HeuristicEvaluator(false);
	ActionPruner pruner = new ActionPruner();
	private IStateEvaluator evaluator;
	//public int moves;
	private int n;
	private PriorityQueue<ValuedMove> valuedMoves;
	private int budget;
	private long start;
	
	public BestNMovesSearcher(int n){
		this.n = n;
		this.valuedMoves = new PriorityQueue<ValuedMove>();
	}

	public List<ValuedMove> bestMoves(GameState state, IStateEvaluator evaluator, int budget) {
		this.evaluator = evaluator;
		this.budget = budget;
		this.start = System.currentTimeMillis();
		//moves = 0;
		addMoves(state, new ArrayList<Action>(), 0);
		transTable.clear();
		
		List<ValuedMove> moves = new ArrayList<ValuedMove>();
		for(ValuedMove move : valuedMoves)
			moves.add(move);
		
		return moves;

	}

	private void addMoves(GameState state, List<Action> move, int depth) {

		// End turn
		if (state.APLeft == 0 || move.size() > 5) {
			//moves++;
			final double value = evaluator.eval(state, state.p1Turn);
			ValuedMove posMove = new ValuedMove(null, value);
			valuedMoves.add(posMove);
			if (valuedMoves.size() > n)
				valuedMoves.poll();
			if (valuedMoves.contains(posMove)){
				final List<Action> nextMove = clone(move);
				nextMove.add(SingletonAction.endTurnAction);
				posMove.actions = nextMove;
			}
			return;
		}

		// Possible actions
		final List<Action> actions = new ArrayList<Action>();
		state.possibleActions(actions);
		pruner.prune(actions, state);

		// final GameState next = state.copy();
		final GameState next = new GameState(null);
		next.imitate(state);

		int i = 0;
		for (final Action action : actions) {
			if (depth == 0 && budget != -1 && System.currentTimeMillis() > start + budget)
				return;

			if (i > 0)
				next.imitate(state);
			next.update(action);

			final long hash = next.hash();
			if (transTable.containsKey(hash))
				transTable.put(hash, transTable.get(hash) + 1);
			else {
				transTable.put(hash, 1);
				final List<Action> nextMove = clone(move);
				nextMove.add(action);
				addMoves(next, nextMove, depth + 1);
			}
			i++;
		}

	}

	private List<Action> clone(List<Action> move) {
		final List<Action> actions = new ArrayList<Action>();
		actions.addAll(move);
		return actions;
	}
}