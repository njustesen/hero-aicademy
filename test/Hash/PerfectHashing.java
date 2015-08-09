package Hash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import util.MapLoader;
import game.GameState;
import model.Card;
import model.CardSet;
import model.DECK_SIZE;
import model.HaMap;
import model.Unit;

public class PerfectHashing {
	
	public static Random random;

	public static void main(String[] args) throws IOException{
		
		random = new Random();
		test();
		
	}
	
	private static void test() throws IOException {
		
		Map<Long, GameState> states = new HashMap<Long, GameState>();
		
		for(int i = 0; i < 1000000; i++){
			GameState state = randomGameState(MapLoader.get("a"));
			Long hash = state.hash();
			if (states.containsKey(hash)){
				if (states.get(hash).equals(state)){
					System.out.println("Hash + equal");
				}else{
					System.out.println("Hash");
					state.print();
					System.out.println("");
					states.get(hash).print();
					System.out.println("");
				}
			} else {
				states.put(hash, state);
			}
		}
		
		
	}

	private static GameState randomGameState(HaMap map) {
		
		GameState state = new GameState(map);
		state.init(DECK_SIZE.TINY);
		//state.APLeft = random.nextInt(5);
		
		//state.turn = random.nextInt(100);
		
		//state.isTerminal = random.nextBoolean();
		
		state.units[1][1] = randomUnit();
		/*
		state.p1Deck = new CardSet();
		state.p1Hand = new CardSet();
		state.p2Deck = new CardSet();
		state.p2Hand = new CardSet();
		*/
		
		return state;
	}

	static List<Card> units = new ArrayList<Card>();
	static {
		units.add(Card.ARCHER);
		units.add(Card.CLERIC);
		units.add(Card.CRYSTAL);
		units.add(Card.KNIGHT);
		units.add(Card.NINJA);
		units.add(Card.WIZARD);
	}
	
	private static Unit randomUnit() {
		Unit unit = new Unit(units.get(random.nextInt(units.size())), random.nextBoolean());
		unit.hp = random.nextInt(1200);
		return unit;
	}

}
