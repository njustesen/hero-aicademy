package ai.movesearch;

import game.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;

import action.Action;
import action.SingletonAction;
import ai.evaluation.IStateEvaluator;
import ai.util.ActionPruner;

public class MoveThread implements Callable<List<ValuedMove>>{
	
	public ParallelMoveSearcher searcher;
	public GameState state;

	private ActionPruner pruner;
	private int n;
	private PriorityQueue<ValuedMove> valuedMoves;
	private int budget;
	private IStateEvaluator evaluator;
	private long start;
	private GameState clone;
	
	public MoveThread(int n, IStateEvaluator evaluator, int budget, ParallelMoveSearcher searcher){
		this.evaluator = evaluator;
		this.budget = budget;
		this.searcher = searcher;
		this.clone = new GameState(null);
		this.valuedMoves = new PriorityQueue<ValuedMove>();
		this.n = n;
		this.pruner = new ActionPruner();
	}

	@Override
	public List<ValuedMove> call() throws Exception {
		return bestMoves();
	}
	
	public List<ValuedMove> bestMoves() {
		
		this.start = System.currentTimeMillis();
		Action action = searcher.nextAction();
		
		while(action != null && (budget == -1 || System.currentTimeMillis() < start + budget)){
			List<Action> move = new ArrayList<Action>();
			move.add(action);
			clone.imitate(state);
			clone.update(action);
			addMoves(clone, move, 0);
			action = searcher.nextAction();
		}

		List<ValuedMove> moves = new ArrayList<ValuedMove>();
		moves.addAll(valuedMoves);
		valuedMoves.clear();
		return moves;

	}

	private void addMoves(GameState s, List<Action> move, int depth) {

		// End turn
		if (s.APLeft == 0 || move.size() > 5) {
			final double value = evaluator.eval(s, state.p1Turn);
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
		s.possibleActions(actions);
		pruner.prune(actions, s);

		// final GameState next = state.copy();
		final GameState next = new GameState(null);
		next.imitate(s);

		int i = 0;
		for (final Action action : actions) {
			if (budget != -1 && System.currentTimeMillis() > start + budget)
				return;

			if (i > 0)
				next.imitate(s);
			next.update(action);

			final long hash = next.hash();
			synchronized (searcher) {
				if (searcher.visited(hash))
					searcher.visit(hash);
				else {
					searcher.visit(hash);
					final List<Action> nextMove = clone(move);
					nextMove.add(action);
					addMoves(next, nextMove, depth + 1);
				}
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
