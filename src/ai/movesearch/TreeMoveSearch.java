package ai.movesearch;

import java.util.ArrayList;
import java.util.List;

import action.Action;
import action.EndTurnAction;
import action.SingletonAction;
import ai.evaluation.HeuristicEvaluator;
import ai.util.ActionPruner;
import game.GameState;

public class TreeMoveSearch {

	class Node {
		public Action action;
		public Node parent;
		public List<Node> children;

		public Node(Action action, Node parent) {
			this.action = action;
			this.parent = parent;
			children = new ArrayList<Node>();
		}

		public void print(int depth) {
			for (int i = 0; i < depth; i++)
				System.out.print("\t");
			System.out.print(action);
			for (final Node node : children)
				node.print(depth++);
		}

		public int size() {
			int i = 1;
			for (final Node child : children)
				i += child.size();
			return i;
		}

		public List<List<Action>> moves(int depth) {
			final List<List<Action>> moves = new ArrayList<List<Action>>();
			if (children.isEmpty()) {
				final List<Action> actions = new ArrayList<Action>();
				actions.add(action);
				moves.add(actions);
			} else
				for (final Node child : children) {
					final List<List<Action>> newMoves = child.moves(depth + 1);
					for (final List<Action> move : newMoves) {
						final List<Action> newMove = new ArrayList<Action>();
						newMove.add(action);
						newMove.addAll(move);
						moves.add(newMove);
					}
				}
			return moves;
		}

		public void movesLeaf(List<List<Action>> moves, int depth) {
			if (children.isEmpty()) {
				final List<Action> actions = new ArrayList<Action>();
				actions.add(action);
				Node p = parent;
				while (p != null && p.action != null) {
					actions.add(p.action);
					p = p.parent;
				}
				moves.add(actions);
			} else
				for (final Node child : children)
					child.movesLeaf(moves, depth + 1);
		}
	}

	HeuristicEvaluator evalutator = new HeuristicEvaluator(false);
	ActionPruner pruner = new ActionPruner();

	public List<List<Action>> possibleMoves(GameState state) {

		final List<List<Action>> moves = new ArrayList<List<Action>>();

		Node root = null;
		try {
			root = initTree(state, 0);
		} catch (final IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (root.children.isEmpty()) {
			final List<Action> actions = new ArrayList<Action>();
			actions.add(SingletonAction.endTurnAction);
			moves.add(actions);
			return moves;
		}

		root.movesLeaf(moves, 0);

		return moves;

	}

	private Node initTree(GameState state, int depth)
			throws IllegalStateException, UnsupportedOperationException,
			Exception {

		final Node root = new Node(null, null);

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
				if (next.APLeft == state.APLeft)
					continue; // Nothing happened
				final Node node = initTree(next, depth + 1);
				root.children.add(node);
				node.parent = root;
				node.action = action;
			}
			i++;
		}

		return root;
	}
}
