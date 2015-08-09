package Hash;

import java.io.IOException;
import java.util.HashSet;

import action.Action;
import ai.AI;
import ai.RandomAI;
import ai.util.RAND_METHOD;
import util.CachedLines;
import util.MapLoader;
import game.GameState;
import model.Card;
import model.DECK_SIZE;
import model.Position;
import model.Unit;

public class Hashing {

	public static void main(String[] args) throws IOException{
		
		for(int i = 0; i < 1000; i++){
			gameStates();
		}
	}
	
	private static void gameStates() throws IOException {
		
		HashSet<Long> seen = new HashSet<Long>();
		
		GameState state = new GameState(MapLoader.get("a"));
		if (CachedLines.posMap.isEmpty())
			CachedLines.load(state.map);
		
		state.init(DECK_SIZE.STANDARD);
		
		AI ai = new RandomAI(RAND_METHOD.TREE);
		
		while(!state.isTerminal){
			Action action = ai.act(state, -1);
			state.update(action);
			long hash = state.hash();
			if (!seen.contains(hash))
				seen.add(hash);
			else
				System.out.println("Collision!");
			System.out.println(hash);
		}
		
	}

	public static void unit(){
		Unit unit = new Unit(Card.CLERIC, true);
		unit.equipment.add(Card.RUNEMETAL);
		unit.equipment.add(Card.DRAGONSCALE);
		Position pos = new Position(1,0);
		System.out.println(unit.hash(2,1));
	}
	
	public static void positions(){
		for(int x = 0; x <= 9; x++)
			for(int y = 0; y < 5; y++)
				System.out.println(x + " " + y + " " + new Position(x,y).hashCode());
	}

}
