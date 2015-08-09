package ai.evaluation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import util.MapLoader;
import ai.util.NormUtil;
import game.GameState;
import libs.UnitClassLib;
import model.Card;
import model.CardType;
import model.DECK_SIZE;
import model.Position;

public class MeanEvaluator implements IStateEvaluator {

	public static double MAX_UNIT_TINY = 0;
	public static double MAX_CRYSTAL_TINY = 0;
	public static double MAX_UNIT_SMALL = 0;
	public static double MAX_CRYSTAL_SMALL = 0;
	public static double MAX_UNIT_STANDARD = 0;
	public static double MAX_CRYSTAL_STANDARD = 0;
	
	private static Map<Card, Double> values;
	static {
		values = new HashMap<Card, Double>();
		values.put(Card.ARCHER, 1.1);
		values.put(Card.CLERIC, 1.2);
		values.put(Card.DRAGONSCALE, .4);
		values.put(Card.INFERNO, 1.6);
		values.put(Card.KNIGHT, 1.0);
		values.put(Card.NINJA, 1.5);
		values.put(Card.REVIVE_POTION, .9);
		values.put(Card.RUNEMETAL, .4);
		values.put(Card.SCROLL, .9);
		values.put(Card.SHINING_HELM, .4);
		values.put(Card.WIZARD, 1.1);
		try {
			GameState tiny = new GameState(MapLoader.get("a-tiny"));
			tiny.init(DECK_SIZE.TINY);
			GameState small = new GameState(MapLoader.get("a-small"));
			small.init(DECK_SIZE.SMALL);
			GameState standard = new GameState(MapLoader.get("a"));
			standard.init(DECK_SIZE.STANDARD);
			
			for (Card card : Card.values()){
				if (values.containsKey(card)){
					MAX_UNIT_TINY += values.get(card) * tiny.p1Deck.count(card);
					MAX_UNIT_TINY += values.get(card) * tiny.p1Hand.count(card);
				}
			}
			for (Card card : Card.values()){
				if (values.containsKey(card)){
					MAX_UNIT_SMALL += values.get(card) * small.p1Deck.count(card);
					MAX_UNIT_SMALL += values.get(card) * small.p1Hand.count(card);
				}
			}
			for (Card card : Card.values()){
				if (values.containsKey(card)){
					MAX_UNIT_STANDARD += values.get(card) * standard.p1Deck.count(card);
					MAX_UNIT_STANDARD += values.get(card) * standard.p1Hand.count(card);
				}
			}
			
			for(Position pos : tiny.map.p1Crystals)
				MAX_CRYSTAL_TINY += tiny.units[pos.x][pos.y].hp;
			
			for(Position pos : small.map.p1Crystals)
				MAX_CRYSTAL_SMALL += small.units[pos.x][pos.y].hp;
			
			for(Position pos : standard.map.p1Crystals)
				MAX_CRYSTAL_STANDARD += standard.units[pos.x][pos.y].hp;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public MeanEvaluator() {
	}

	public double eval(GameState state, boolean p1) {

		if (state.isTerminal){
			int winner = state.getWinner();
			if ((winner == 1) && p1 || (winner == 2 && !p1))
				return 1.0;
			return 0;
		}
		
		double delta = dif(state, p1);
		
		return delta;
		
	}

	private double dif(GameState state, boolean p1) {
		double p1Cards = 0;
		double p1Crystals = 0;
		double p2Cards = 0;
		double p2Crystals = 0;
		double p1UnitsBoard = 0;
		double p2UnitsBoard = 0;
		for (int x = 0; x < state.map.width; x++){
			for (int y = 0; y < state.map.height; y++){
				if (state.units[x][y] != null){
					if (state.units[x][y].p1Owner){
						if (state.units[x][y].unitClass.card == Card.CRYSTAL)
							p1Crystals += (double)state.units[x][y].hp;
						else{
							double hp = (double)state.units[x][y].hp / (double)state.units[x][y].unitClass.maxHP;
							hp = hp / 2;
							if (hp != 0)
								hp += .5;
							p1UnitsBoard += values.get(state.units[x][y].unitClass.card) * hp;
							for(Card card : state.units[x][y].equipment)
								p1UnitsBoard += values.get(card) * hp;
						}
					} else {
						if (state.units[x][y].unitClass.card == Card.CRYSTAL)
							p2Crystals += (double)state.units[x][y].hp;
						else {
							double hp = (double)state.units[x][y].hp / (double)state.units[x][y].unitClass.maxHP;
							hp = hp / 2;
							if (hp != 0)
								hp += .5;
							p2UnitsBoard += values.get(state.units[x][y].unitClass.card) * hp;
							for(Card card : state.units[x][y].equipment)
								p2UnitsBoard += values.get(card) * hp;
						}
					}
				}
			}
		}
		// TODO: Opponent hand should be hidden
		for (final Card card : Card.values()){
			if (card == Card.CRYSTAL)
				continue;
			p1Cards += values.get(card) * state.p1Deck.count(card);
			p2Cards += values.get(card) * state.p2Deck.count(card);
			p1Cards += values.get(card) * state.p1Hand.count(card);
			p2Cards += values.get(card) * state.p2Hand.count(card);
		}

		if (state.map.name.equals("a")){
			p1Crystals = p1Crystals / MAX_CRYSTAL_STANDARD;
			p2Crystals = p2Crystals / MAX_CRYSTAL_STANDARD;
			p1Cards = p1Cards / MAX_UNIT_STANDARD;
			p2Cards = p2Cards / MAX_UNIT_STANDARD;
			p1UnitsBoard = p1UnitsBoard / MAX_UNIT_STANDARD;
			p2UnitsBoard = p2UnitsBoard / MAX_UNIT_STANDARD;
		} else if (state.map.name.equals("a-small")){
			p1Crystals = p1Crystals / MAX_CRYSTAL_SMALL;
			p2Crystals = p2Crystals / MAX_CRYSTAL_SMALL;
			p1Cards = p1Cards / MAX_UNIT_SMALL;
			p2Cards = p2Cards / MAX_UNIT_SMALL;
			p1UnitsBoard = p1UnitsBoard / MAX_UNIT_SMALL;
			p2UnitsBoard = p2UnitsBoard / MAX_UNIT_SMALL;
		} else if (state.map.name.equals("a-tiny")){
			p1Crystals = p1Crystals / MAX_CRYSTAL_TINY;
			p2Crystals = p2Crystals / MAX_CRYSTAL_TINY;
			p1Cards = p1Cards / MAX_UNIT_TINY;
			p2Cards = p2Cards / MAX_UNIT_TINY;
			p1UnitsBoard = p1UnitsBoard / MAX_UNIT_TINY;
			p2UnitsBoard = p2UnitsBoard / MAX_UNIT_TINY;
		}
		
		//double p1Val = (p1Cards + p1UnitsBoard + p1Crystals) / 3;
		//double p2Val = (p2Cards + p2UnitsBoard + p2Crystals) / 3;
		double p1Val = ((p1UnitsBoard + p1Cards) * p1UnitsBoard * p1Crystals);
		double p2Val = ((p2UnitsBoard + p2Cards) * p2UnitsBoard * p2Crystals);
				
		if (p1)
			return p1Val - p2Val;
		return p2Val - p1Val;
	}

	@Override
	public double normalize(double delta) {
		return NormUtil.normalize(delta, 1, -1, 1, 0);
	}

	@Override
	public String title() {
		return "Material Balance Evaluator";
	}

	@Override
	public IStateEvaluator copy() {
		return new MeanEvaluator();
	}
	
	

}
