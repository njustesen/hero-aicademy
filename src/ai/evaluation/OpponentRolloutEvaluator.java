package ai.evaluation;

import ai.AI;
import game.GameState;

public class OpponentRolloutEvaluator implements IStateEvaluator {

	public int rolls;
	public AI policy;
	public IStateEvaluator evaluator;
	public boolean copy;
	public boolean worst;
	
	public OpponentRolloutEvaluator(int rolls, AI policy, IStateEvaluator evaluator) {
		super();
		this.rolls = rolls;
		this.evaluator = evaluator;
		this.policy = policy;
		this.copy = false;
		if (rolls > 1)
			this.copy = true;
	}
	
	public OpponentRolloutEvaluator(int rolls, AI policy, IStateEvaluator evaluator, boolean worst) {
		super();
		this.rolls = rolls;
		this.evaluator = evaluator;
		this.policy = policy;
		if (rolls > 1)
			this.copy = true;
		this.worst = worst;
	}
	
	@Override
	public double eval(GameState state, boolean p1) {
		
		if (state.isTerminal)
			return evaluator.eval(state, p1);
		
		if (!copy)
			return simulateGame(state, p1);
		
		GameState clone = state.copy();
		double sum = 0;
		double w = 1000000;
		//List<Double> vals = new ArrayList<Double>();
		
		for(int i = 0; i < rolls; i++){
			if (i!=0)
				clone.imitate(state);
			double d = simulateGame(clone, p1);
			//vals.add(d);
			sum += d;
			if (d < w)
				w = d;
		}
		
		//System.out.println(Statistics.avg(vals) + ";" + Statistics.stdDev(vals) + ";");
		if (!worst)
			return sum / rolls;
		return w;
			
	}
	
	private double simulateGame(GameState state, boolean p1) {
		while (!state.isTerminal){
			if ((state.APLeft == GameState.ACTION_POINTS || (state.turn == 1 && state.APLeft == GameState.STARTING_AP)) && 
					state.p1Turn == p1)
				break;
			state.update(policy.act(state, -1));
		}
		return evaluator.eval(state, p1);
	}
	
	@Override
	public IStateEvaluator copy(){
		return new OpponentRolloutEvaluator(rolls, policy.copy(), evaluator.copy());
	}

	@Override
	public double normalize(double delta) {
		return evaluator.normalize(delta);
	}

	@Override
	public String title() {
		return "Opponent Rollout Evaluator [rolls="+rolls+ " policy=" + policy.title() + " evaluator=" + evaluator.title() + "]";
	}

}
