package ai.mcts;

import game.GameState;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import util.Statistics;
import action.Action;
import action.SingletonAction;
import ai.AI;
import ai.evaluation.IStateEvaluator;
import ai.evaluation.RolloutEvaluator;
import ai.util.ActionComparator;
import ai.util.ActionPruner;
import ai.util.ComplexActionComparator;

public class RootParallelizedMcts implements AI {
	
	private class MctsThread implements Callable<MctsNode> {
		
		public Mcts mcts;
		public GameState state;
		
		public MctsThread(Mcts mcts, GameState state) {
			super();
			this.mcts = mcts;
			this.state = state;
		}

		@Override
		public MctsNode call() throws Exception {
			mcts.root = null;
			mcts.act(state, -1);
			return mcts.root;
		} 
		
	}

	public List<Double> rollouts;
	
	private double c;
	private long budget;
	private IStateEvaluator defaultPolicy;
	private List<Action> move;
	private long lastState;
	private ExecutorService executor;
	private List<MctsThread> threads;
	private List<Future<MctsNode>> futures;
	private MctsNode root;

	public boolean cut;

	public boolean collapse;

	public RootParallelizedMcts(long budget, IStateEvaluator defaultPolicy) {
		this.budget = budget;
		this.defaultPolicy = defaultPolicy;
		this.futures = new ArrayList<Future<MctsNode>>();
		this.c = 1 / Math.sqrt(2);
		this.threads = new ArrayList<RootParallelizedMcts.MctsThread>();
		this.move = new ArrayList<Action>();
		this.rollouts = new ArrayList<Double>();
		setup();
	}
	
	public RootParallelizedMcts(long budget, IStateEvaluator defaultPolicy, double c) {
		this.budget = budget;
		this.defaultPolicy = defaultPolicy;
		this.futures = new ArrayList<Future<MctsNode>>();
		this.c = c;
		this.threads = new ArrayList<RootParallelizedMcts.MctsThread>();
		this.move = new ArrayList<Action>();
		this.rollouts = new ArrayList<Double>();
		setup();
	}

	private void setup() {
		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println("Processors: " + processors);
		this.executor = Executors.newFixedThreadPool(processors);
		for(int i=0; i < processors; i++) {
			MctsThread thread = new MctsThread(new Mcts(budget, defaultPolicy.copy()), new GameState(null));
			thread.mcts.cut = cut;
			thread.mcts.cut = collapse;
			thread.mcts.c = c;
			thread.mcts.resetRoot = false;
			threads.add(thread);
		}
	}

	@Override
	public Action act(GameState state, long ms) {

		final long start = System.currentTimeMillis();

		// If move already found return next action
		if (!move.isEmpty()) {
			final Action action = move.get(0);
			move.remove(0);
			if (move.isEmpty())
				lastState = state.simpleHash();
			return action;
		}

		// Search
		for(MctsThread thread : threads)
			thread.state.imitate(state);
		
		futures.clear();
		try {
			futures = executor.invokeAll(threads);
			root = null;
			for(Future<MctsNode> f : futures){
				if (root == null)
					root = f.get();
				else {
					merge(f.get());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		move = bestMove(state);
		final Action action = move.get(0);
		move.remove(0);
		
		double rolls = 0;
		for(MctsThread thread : threads){
			thread.mcts.root = null;
			thread.mcts.move.clear();
			rolls += thread.mcts.rollouts.get(thread.mcts.rollouts.size()-1);
		}
		
		rollouts.add(rolls);
		
		return action;

	}

	private void merge(MctsNode other) {
		
		for(MctsEdge child : root.out){
			int idx = root.out.indexOf(child);
			child.visits += other.out.get(idx).visits;
			child.value += other.out.get(idx).value;
		}
		
		root.visits += other.visits;
		
	}

	private void saveTree() {
		PrintWriter out = null;
		try {
			out = new PrintWriter("mcts.xml");
			out.print(root.toXml(0, new HashSet<MctsNode>(), 12));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				out.close();
		}
	}
	
	private double uct(MctsEdge edge, MctsNode node, boolean urgent) {

		if (urgent)
			return edge.avg() + 2 * c
					* Math.sqrt((2 * Math.log(node.visits)) / (edge.visits));
		return edge.avg();

	}

	private MctsEdge best(MctsNode node, boolean urgent) {
		double bestVal = -100000;
		MctsEdge bestEdge = null;
		for (final MctsEdge edge : node.out) {
			final double val = uct(edge, node, urgent);
			if (val > bestVal) {
				bestVal = val;
				bestEdge = edge;
			}
		}

		return bestEdge;
	}

	
	private List<Action> bestMove(GameState state) {
		MctsEdge edge = best(root, false);
		final List<Action> move = new ArrayList<Action>();
		if (edge == null) {
			move.add(SingletonAction.endTurnAction);
			return move;
		}
		while (edge.p1 == state.p1Turn) {
			move.add(edge.action);
			if (edge.isLeaf())
				break;
			edge = best(edge.to, false);
			if (edge == null || edge.isLeaf())
				break;
		}
		if (!move.contains(SingletonAction.endTurnAction))
			move.add(SingletonAction.endTurnAction);
		return move;
	}

	@Override
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
	}

	@Override
	public String header() {
		String name = title()+"\n";
		name += "Time budget = " + budget + "\n";
		name += "C = " + c + "\n";
		name += "Threads = " + threads.size() + "\n";
		name += "Cut = " + cut + "\n";
		name += "Collapse = " + collapse + "\n";
		name += "Leaf evaluation = " + defaultPolicy.title() + "\n";
		return name;
	}

	@Override
	public String title() {
		return "MCTS - Root Parallelized";
	}

	@Override
	public AI copy() {
		return new RootParallelizedMcts(budget, defaultPolicy.copy(), c);
	}

}
