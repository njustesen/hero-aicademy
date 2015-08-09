package ai.util;

import game.GameState;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import action.Action;

public abstract class ActionComparator implements Comparator<Action> {

	public GameState state;

	@Override
	public int compare(Action o1, Action o2) {

		final int val1 = value(o1);
		final int val2 = value(o2);

		if (val1 > val2)
			return -1;
		else if (val2 > val1)
			return 1;
		else if (o1.hashCode() > o2.hashCode())
			return -1;
		else if (o1.hashCode() < o2.hashCode())
			return 1;
		else
			return 0;
	}
	
	public abstract int value(Action action);

	public void sort(List<Action> actions, GameState state) {
		this.state = state;
		Collections.sort(actions, this);
	}

}