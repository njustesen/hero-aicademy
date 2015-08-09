package ai.evaluation;

import game.GameState;

public interface IStateEvaluator {

	public double eval(GameState state, boolean p1);

	public double normalize(double delta);

	public String title();

	public IStateEvaluator copy();
	
}
