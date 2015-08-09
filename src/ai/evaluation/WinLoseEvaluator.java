package ai.evaluation;

import game.GameState;

public class WinLoseEvaluator implements IStateEvaluator {
	
	public WinLoseEvaluator() {
		super();
	}
	
	@Override
	public double eval(GameState state, boolean p1) {
		
		if (state.isTerminal){
			int i = 0;
			int winner = state.getWinner();
			if (winner == 1)
				if (p1)
					return 1;
				else
					return 0;
			else if (winner == 2)
				if (p1)
					return 0;
				else
					return 1;
			else
				return 0.5;
		}
		
		return 0.5;
		
	}

	@Override
	public double normalize(double delta) {
		return (delta+1)/2;
	}

	@Override
	public String title() {
		return "Win Lose Evaluator";
	}

	@Override
	public IStateEvaluator copy() {
		return new WinLoseEvaluator();
	}

}
