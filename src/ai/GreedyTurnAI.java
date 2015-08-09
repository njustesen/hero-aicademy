package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.List;

import action.Action;
import action.SingletonAction;
import ai.evaluation.IStateEvaluator;
import ai.movesearch.BestMoveSearcher;
import ai.movesearch.MoveSearch;
import ai.movesearch.ParallelMoveSearcher;
import ai.movesearch.ValuedMove;

public class GreedyTurnAI implements AI {

	//private final BestMoveSearcher searcher;
	private final ParallelMoveSearcher searcher;
	private List<Action> actions;
	private final IStateEvaluator evaluator;
	
	private boolean anytime;
	public int budget;

	public GreedyTurnAI(IStateEvaluator evaluator) {
		super();
		this.evaluator = evaluator;
		this.actions = new ArrayList<Action>();
		//this.searcher = new BestMoveSearcher();
		this.anytime = false;
		this.budget = -1;
		this.searcher = new ParallelMoveSearcher(1, budget, evaluator);
	}
	
	public GreedyTurnAI(IStateEvaluator evaluator, int budget) {
		super();
		this.evaluator = evaluator;
		this.actions = new ArrayList<Action>();
		this.anytime = true;
		this.budget = budget;
		this.searcher = new ParallelMoveSearcher(1, budget, evaluator);
	}
	
	public GreedyTurnAI(IStateEvaluator evaluator, int budget, boolean parallel) {
		super();
		this.evaluator = evaluator;
		this.actions = new ArrayList<Action>();
		this.anytime = true;
		this.budget = budget;
		this.searcher = new ParallelMoveSearcher(1, budget, evaluator, parallel);
	}

	@Override
	public Action act(GameState state, long ms) {

		if (!actions.isEmpty()) {
			final Action action = actions.get(0);
			actions.remove(0);
			return action;
		}
		
		List<ValuedMove> moves = searcher.search(state);
		actions.clear();
		if (!moves.isEmpty())
			actions = moves.get(0).actions;
			
		if (actions == null || actions.isEmpty())
			return SingletonAction.endTurnAction;
		
		final Action action = actions.get(0);
		actions.remove(0);
		
		return action;

	}

	@Override
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String header() {
		String name = title()+"\n";
		name += "State evaluatior = " + evaluator.title() + "\n";
		if (searcher.threads.size() > 1){
			name += "Parallelized = true\n";
			name += "Processors = " + Runtime.getRuntime().availableProcessors() + "\n";
		} else {
			name += "Parallelized = false\n";
		}
		if (budget != -1)
			name += "Budget = " + budget + " ms.\n";
		else
			name += "Budget = no limit \n";
		return name;
	}

	@Override
	public String title() {
		return "GreedyTurn";
	}

	@Override
	public AI copy() {
		return new GreedyTurnAI(evaluator.copy());
	}
	
}
