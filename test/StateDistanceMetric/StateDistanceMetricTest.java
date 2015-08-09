package StateDistanceMetric;

import game.GameState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import model.DECK_SIZE;
import ui.UI;
import util.CachedLines;
import util.MapLoader;
import action.Action;
import action.SingletonAction;
import ai.AI;
import ai.RandomAI;
import ai.util.RAND_METHOD;
import ai.util.StateDistanceMetric;

public class StateDistanceMetricTest {
	
	static StateDistanceMetric metric;

	public static void main(String[] args){
		
		metric = new StateDistanceMetric();
		run();
		
	}
	
	private static void run() {
		
		GameState stateA = null;
		GameState stateB = null;
		try {
			stateA = new GameState(MapLoader.get("a"));
			stateB = new GameState(MapLoader.get("a"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (CachedLines.posMap.isEmpty())
			CachedLines.load(stateA.map);
		
		SingletonAction.init(stateA.map);
		
		stateA.init(DECK_SIZE.STANDARD);
		
		AI ai = new RandomAI(RAND_METHOD.TREE);

		for(int i = 0; i < 100; i++){
			Action actionA = ai.act(stateA, -1);
			stateA.update(actionA);
		}
		
		List<Action> possible = new ArrayList<Action>();
		stateA.possibleActions(possible);
		stateB.imitate(stateA);
		System.out.println(metric.distance(stateA, stateB));
		new UI(stateA, false, false, "", "", false);
		
		for(Action action : possible){
			stateB.update(action);
			System.out.println(action);
			System.out.println(metric.distance(stateA, stateB));
			stateB.imitate(stateA);
			System.out.println("------");
		}
		
		
	}
	
}
