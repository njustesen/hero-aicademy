package ai.movesearch;

import java.util.List;

import action.Action;

public class ValuedMove implements Comparable<ValuedMove>{
	public List<Action> actions;
	public double value;
	public ValuedMove(List<Action> actions, double value) {
		super();
		this.actions = actions;
		this.value = value;
	}
	@Override
	public int compareTo(ValuedMove o) {
		return (int) (Math.round(value * 100) - (Math.round(((ValuedMove)o).value * 100)));
	}
	
	@Override
	public String toString() {
		return value + " -> " + actions;
	}
	
}
