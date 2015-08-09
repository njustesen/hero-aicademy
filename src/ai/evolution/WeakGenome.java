package ai.evolution;

import java.util.ArrayList;

import action.Action;

public class WeakGenome extends Genome {

	public WeakGenome() {
		super();
		actions = new ArrayList<Action>();
		value = 0;
		visits = 0;
	}

	public double fitness() {
		return value;
	}

}
