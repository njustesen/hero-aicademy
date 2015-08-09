package ai.movesearch;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import action.Action;
import ai.evaluation.IStateEvaluator;
import ai.util.ActionPruner;

public class ParallelMoveSearcher {
	
	Map<Long, Integer> transTable = new HashMap<Long, Integer>();

	private IStateEvaluator evaluator;
	private int n;
	public int budget;
	private ExecutorService executor;
	public List<MoveThread> threads;
	private List<Future<List<ValuedMove>>> futures;
	private List<Action> rootActions;
	private ActionPruner pruner = new ActionPruner();
	
	public ParallelMoveSearcher(int n, int budget, IStateEvaluator evaluator){
		this.n = n;
		this.budget = budget;
		this.evaluator = evaluator;
		this.threads = new ArrayList<MoveThread>();
		this.futures = new ArrayList<Future<List<ValuedMove>>>();
		this.rootActions = new ArrayList<Action>();
		setup(true);
	}
	
	public ParallelMoveSearcher(int n, int budget, IStateEvaluator evaluator, boolean parallel){
		this.n = n;
		this.budget = budget;
		this.evaluator = evaluator;
		this.threads = new ArrayList<MoveThread>();
		this.futures = new ArrayList<Future<List<ValuedMove>>>();
		this.rootActions = new ArrayList<Action>();
		setup(parallel);
	}

	private void setup(boolean parallel) {
		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println("Processors: " + processors);
		if (!parallel)
			processors = 1;
		this.executor = Executors.newFixedThreadPool(processors);
		for(int i=0; i < processors; i++) {
			MoveThread thread = new MoveThread(n, evaluator.copy(), budget, this);
			threads.add(thread);
		}
	}
	
	public List<ValuedMove> search(GameState state){
		
		// Possible actions
		rootActions.clear();
		state.possibleActions(rootActions);
		pruner.prune(rootActions, state);
		
		for(MoveThread thread : threads)
			thread.state = state.copy();
		
		List<ValuedMove> moves = new ArrayList<ValuedMove>();
		try {
			futures = executor.invokeAll(threads);
			for(Future<List<ValuedMove>> f : futures)
				moves.addAll(f.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		Collections.sort(moves);
		Collections.reverse(moves);
		transTable.clear();
		List<ValuedMove> best = new ArrayList<ValuedMove>();
		for(ValuedMove m : moves)
			if (!m.actions.isEmpty() && best.size() < n)
				best.add(m);
			else
				break;
		return best;
	}
	
	public synchronized Action nextAction() {
		if (rootActions.isEmpty())
			return null;
		Action action = rootActions.get(0);
		rootActions.remove(0);
		return action;
	}

	public void visit(long hash) {
		if (visited(hash))
			transTable.put(hash, transTable.get(hash) + 1);
		else
			transTable.put(hash, 1);
	}

	public boolean visited(long hash) {
		return transTable.containsKey(hash);
	}

}