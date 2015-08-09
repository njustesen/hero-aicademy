package ai.movesearch;

import java.util.ArrayList;
import java.util.List;

import action.Action;
import action.EndTurnAction;
import action.SingletonAction;
import ai.evaluation.HeuristicEvaluator;
import ai.util.ActionPruner;
import game.GameState;

public class MoveSearch {

	HeuristicEvaluator evalutator = new HeuristicEvaluator(false);
	ActionPruner pruner = new ActionPruner();
	List<List<Action>> moves;
	
	public List<List<Action>> possibleMoves(GameState state) {

		moves = new ArrayList<List<Action>>();
		try {
			addMoves(state, new ArrayList<Action>(), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(moves.size() + " moves found!");
		
		return moves;

	}

	private void addMoves(GameState state, List<Action> move, int depth) throws IllegalStateException, UnsupportedOperationException, Exception {

		final List<Action> actions = new ArrayList<Action>();
		state.possibleActions(actions);
		pruner.prune(actions, state);
		int i = 1;
		for (final Action action : actions) {
			if (depth == 0)
				System.out.println(i + "/" + actions.size());
			if (depth < 5 && !(action instanceof EndTurnAction)) {
				GameState next;
				next = new GameState(null);
				next.imitate(state);
				next.update(action);
				//if (next.APLeft == state.APLeft)
				//	continue; // Nothing happened
				List<Action> clone = clone(move);
				clone.add(action);
				addMoves(next, clone, depth + 1);
			} else {
				move.add(action);
				moves.add(move);
			}
			i++;
		}
		
	}

	private List<Action> clone(List<Action> move) {
		List<Action> actions = new ArrayList<Action>();
		actions.addAll(move);
		return actions;
	}
}
