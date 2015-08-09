package ai.evaluation;

import game.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LeafParallelizer implements IStateEvaluator {

	public enum LEAF_METHOD {
		AVERAGE, WORST, BEST;
	}
	
	private class RolloutThread implements Callable<Double> {
		
		public RolloutEvaluator evaluator;
		public GameState state;
		public boolean p1;
		
		public RolloutThread(RolloutEvaluator evaluator, GameState state, boolean p1) {
			super();
			this.evaluator = evaluator;
			this.state = state;
			this.p1 = p1;
		}

		@Override
		public Double call() throws Exception {
			return evaluator.eval(state, p1);
		} 
		
	}
	
	private ExecutorService executor;
	private RolloutEvaluator evaluator;
	private List<RolloutThread> threads;
	private LEAF_METHOD method;
	private List<Future<Double>> futures;
	private double best = 0;
	private double worst = 0;
	private double sum = 0;
	private double val = 0;
	
	public LeafParallelizer(RolloutEvaluator evaluator, LEAF_METHOD method) {
		super();
		this.evaluator = evaluator;
		this.method = method;
		this.threads = new ArrayList<RolloutThread>();
		this.futures = new ArrayList<Future<Double>>();
		setup();
	}
	
	private void setup() {
		
		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println("Processors: " + processors);
		this.executor = Executors.newFixedThreadPool(processors);
		for(int i=0; i < processors; i++) 
			threads.add(new RolloutThread(((RolloutEvaluator)evaluator.copy()), new GameState(null), false));
		
	}

	@Override
	public double eval(GameState state, boolean p1) {
		
		for(RolloutThread thread : threads){
			thread.state.imitate(state);
			thread.p1 = p1;
		}
		
		best = -10000000;
		worst = 10000000;
		sum = 0;
		val = 0;
		try {
			futures.clear();
			futures = executor.invokeAll(threads);
			for(Future<Double> f : futures){
				val = f.get();
				if (val > best)
					best = val;
				if (val < worst)
					worst = val;
				sum += val;
			}
			if (method == LEAF_METHOD.AVERAGE)
				return sum / threads.size();
			if (method == LEAF_METHOD.WORST)
				return worst;
			if (method == LEAF_METHOD.BEST)
				return best;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
				
		return 0;
	}
	@Override
	public double normalize(double delta) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String title() {
		return "Leaf parallelization: " + evaluator.title();
	}

	@Override
	public IStateEvaluator copy() {
		return new LeafParallelizer(((RolloutEvaluator)evaluator.copy()), method);
	}
	
	
	
}
