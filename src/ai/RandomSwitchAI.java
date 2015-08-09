package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Card;
import model.Position;
import action.Action;
import action.SingletonAction;
import ai.util.ActionComparator;
import ai.util.ComplexActionComparator;

public class RandomSwitchAI implements AI {

	public double prob;
	private AI a;
	private AI b;
	
	public RandomSwitchAI(double prob, AI a, AI b) {
		this.a = a;
		this.b = b;
		this.prob = prob;
	}

	@Override
	public Action act(GameState state, long ms) {
		if (state.APLeft == 0)
			return SingletonAction.endTurnAction;
		
		if (Math.random() > prob)
			return a.act(state, -1);
	
		return b.act(state, -1);
	}

	@Override
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String header() {
		String name = title()+"\n";
		name += "Probability = " + prob + "\n";
		name += "1. AI = " + a.title() + "\n";
		name += "2. AI = " + b.title() + "\n";
		
		return name;
		
	}

	@Override
	public String title() {
		return "Random switch ai";
	}

	@Override
	public AI copy() {
		return new RandomSwitchAI(prob, a.copy(), b.copy());
	}

}
