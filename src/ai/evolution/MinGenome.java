package ai.evolution;

import java.util.ArrayList;

import action.Action;

public class MinGenome extends Genome {

	public MinGenome() {
		super();
		actions = new ArrayList<Action>();
		value = 0;
		visits = 0;
	}

	public double fitness() {
		return -value;
	}

}
