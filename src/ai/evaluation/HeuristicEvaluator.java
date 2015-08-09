package ai.evaluation;

import java.util.ArrayList;
import java.util.List;

import action.SingletonAction;
import ai.util.NormUtil;
import game.GameState;
import libs.UnitClassLib;
import model.Card;
import model.CardType;
import model.Position;
import model.SquareType;
import model.Unit;

public class HeuristicEvaluator implements IStateEvaluator {

	private static final double MAX_VAL = 60000;
	private boolean winVal;
	private List<Position> p1Healers;
	private List<Position> p2Healers;
	public boolean positional = false;
	public int healMultiplier = 150;
	public int spreadMultiplier = 50;
	
	public HeuristicEvaluator(boolean winVal) {
		this.winVal = winVal;
		this.p1Healers = new ArrayList<Position>();
		this.p2Healers = new ArrayList<Position>();
	}

	public double eval(GameState state, boolean p1) {
		
		//findHealers(state);
		
		if (state.isTerminal){
			int winner = state.getWinner();
			if (!winVal && winner == 1)
				return p1 ? MAX_VAL : -MAX_VAL;
			else if (!winVal && winner == 2)
				return p1 ? -MAX_VAL : MAX_VAL;
			else if (winVal && winner == 1)
				return p1 ? 1 : 0;
			else if (winVal && winner == 2)
				return p1 ? 0 : 1;
			
			if (winVal)
				return 0.5;
			return 0;
		}
		
		int hpDif = hpDif(state, p1);
		
		if (!winVal)
			return hpDif;
		
		return NormUtil.normalize(hpDif, -MAX_VAL, MAX_VAL, 1, 0);

	}

	private void findHealers(GameState state) {
		p1Healers.clear();
		p2Healers.clear();
		for (int x = 0; x < state.map.width; x++)
			for (int y = 0; y < state.map.height; y++)
				if (state.units[x][y] != null && state.units[x][y].hp != 0 && state.units[x][y].unitClass.card == Card.CLERIC)
					if (state.units[x][y].p1Owner)
						p1Healers.add(SingletonAction.positions[x][y]);
					else
						p2Healers.add(SingletonAction.positions[x][y]);
	}

	private int hpDif(GameState state, boolean p1) {
		int p1Units = 0;
		int p2Units = 0;
		boolean up = true;
		for (int x = 0; x < state.map.width; x++){
			for (int y = 0; y < state.map.height; y++){
				if (state.units[x][y] != null){
					up = (state.units[x][y].hp > 0);
					if (state.units[x][y].p1Owner){
						p1Units += 
								state.units[x][y].hp
								+ state.units[x][y].unitClass.maxHP * (up ? 2 : 1)
								+ squareVal(state.map.squares[x][y], state.units[x][y].unitClass.card) * (up ? 1: 0)
								//- healDistance(state, x, y) * healMultiplier
								+ equipmentValue(state.units[x][y]) * (up ? 2 : 1)
								;
								//- spread(state, x, y) * spreadMultiplier;
					} else {
						p2Units += 
								state.units[x][y].hp
								+ state.units[x][y].unitClass.maxHP * (up ? 2 : 1)
								+ squareVal(state.map.squares[x][y], state.units[x][y].unitClass.card) * (up ? 1: 0)
								//- healDistance(state, x, y) * healMultiplier
								+ equipmentValue(state.units[x][y])  * (up ? 2 : 1)
								;
								//- spread(state, x, y) * spreadMultiplier;
					}
				}
			}
		}
		
		// DECK AND HAND UNITS
		int p1Inferno = 0;
		int p2Inferno = 0;
		int p1Potions = 0;
		int p2Potions = 0;
		for (final Card card : Card.values()){
			if (card.type != CardType.UNIT){
				if (card == Card.INFERNO){
					p1Inferno = state.p1Hand.count(card) + state.p1Deck.count(card);
					p2Inferno = state.p2Hand.count(card) + state.p2Deck.count(card);
				}
				continue;
			}
			p1Units += state.p1Deck.count(card) * UnitClassLib.lib.get(card).maxHP * 1.75;
			p2Units += state.p2Deck.count(card) * UnitClassLib.lib.get(card).maxHP * 1.75;
			p1Units += state.p1Hand.count(card) * UnitClassLib.lib.get(card).maxHP * 1.75;
			p2Units += state.p2Hand.count(card) * UnitClassLib.lib.get(card).maxHP * 1.75;
		}
		
		// INFERNO + POTIONS
		int sp1 = p1Inferno * 750 + p1Potions * 600;
		int sp2 = p2Inferno * 750 + p2Potions * 600;
		
		if (p1)
			return (p1Units + sp1) - (p2Units + sp2);
		return (p2Units + sp1) - (p1Units + sp2);
	}

	private int spread(GameState state, int x, int y) {
		if (!positional || state.units[x][y].unitClass.card == Card.CRYSTAL)
			return 0;
		int c = 0;
		boolean p1 = state.units[x][y].p1Owner;
		if (x+1 < state.map.width){
			if (y+1 < state.map.height)
				if (state.units[x+1][y+1] != null && state.units[x+1][y+1].p1Owner == p1)
					if (state.units[x+1][y+1].unitClass.card != Card.CRYSTAL)
						c+=1;
			if (y-1 >= 0)
				if (state.units[x+1][y-1] != null && state.units[x+1][y-1].p1Owner == p1)
					if (state.units[x+1][y-1].unitClass.card != Card.CRYSTAL)
						c+=1;
			if (state.units[x+1][y] != null && state.units[x+1][y].p1Owner == p1)
				if (state.units[x+1][y].unitClass.card != Card.CRYSTAL)
					c+=1;
		}
		if (x-1 >= 0){
			if (y+1 < state.map.height)
				if (state.units[x-1][y+1] != null && state.units[x-1][y+1].p1Owner == p1)
					if (state.units[x-1][y+1].unitClass.card != Card.CRYSTAL)
						c+=1;
			if (y-1 >= 0)
				if (state.units[x-1][y-1] != null && state.units[x-1][y-1].p1Owner == p1)
					if (state.units[x-1][y-1].unitClass.card != Card.CRYSTAL)
						c+=1;
			if (state.units[x-1][y] != null && state.units[x-1][y].p1Owner == p1)
				if (state.units[x-1][y].unitClass.card != Card.CRYSTAL)
					c+=1;
		}
		if (y+1 < state.map.height)
			if (state.units[x][y+1] != null && state.units[x][y+1].p1Owner == p1)
				if (state.units[x][y+1].unitClass.card != Card.CRYSTAL)
					c+=1;
		if (y-1 >= 0)
			if (state.units[x][y-1] != null && state.units[x][y-1].p1Owner == p1)
				if (state.units[x][y-1].unitClass.card != Card.CRYSTAL)
					c+=1;
		
		if (x == 0 || x == state.map.width-1)
			c+=8;
		
		return c;
	}

	private int equipmentValue(Unit unit) {
		int val = 0;
		for(Card card : unit.equipment){
			if (card == Card.DRAGONSCALE){
				if (unit.unitClass.card == Card.KNIGHT)
					val += 30;
				else if (unit.unitClass.card == Card.ARCHER)
					val += 30;
				else if (unit.unitClass.card == Card.WIZARD)
					val += 20;
				val += 30;
			} else if (card == Card.RUNEMETAL){
				if (unit.unitClass.card == Card.KNIGHT)
					val -= 50;
				else if (unit.unitClass.card == Card.ARCHER)
					val += 40;
				else if (unit.unitClass.card == Card.WIZARD)
					val += 40;
				val += 20;
			} else if (card == Card.SHINING_HELM){
				if (unit.unitClass.card == Card.NINJA)
					val += 10;
				else if (unit.unitClass.card == Card.ARCHER)
					val += 20;
				else if (unit.unitClass.card == Card.WIZARD)
					val += 20;
				val += 20;
			} else if (card == Card.SCROLL){
				if (unit.unitClass.card == Card.NINJA)
					val += 40;
				else if (unit.unitClass.card == Card.KNIGHT)
					val -= 40;
				else if (unit.unitClass.card == Card.ARCHER)
					val += 50;
				else if (unit.unitClass.card == Card.WIZARD)
					val += 50;
				val += 30;
			}
		}
		return val;
	}

	private double healDistance(GameState state, int x, int y) {
		
		if (!positional || state.units[x][y].unitClass.card == Card.CRYSTAL)
			return 0;
		boolean p1 = state.units[x][y].p1Owner;
		
		double shortest = 10000;
		double dis = 0;
		if (p1){
			for(Position pos : p1Healers){
				dis = pos.distance(SingletonAction.positions[x][y]);
				if (dis < shortest)
					shortest = dis;
			}
		} else {
			for(Position pos : p2Healers){
				dis = pos.distance(SingletonAction.positions[x][y]);
				if (dis < shortest)
					shortest = dis;
			}
		}
		
		if (p1){
			for(Position pos : state.map.p1DeploySquares)
				dis = pos.distance(SingletonAction.positions[x][y]);
				if (dis+2 < shortest)
					shortest = dis;
		}else{ 
			for(Position pos : state.map.p2DeploySquares)
				dis = pos.distance(SingletonAction.positions[x][y]);
				if (dis+2 < shortest)
					shortest = dis;
		}
		
		return Math.max(0, shortest-2);
		
	}

	private int squareVal(SquareType square, Card card) {
		
		switch(square){
		case ASSAULT : return assultValue(card);
		case DEPLOY_1 : return -75;
		case DEPLOY_2 : return -75;
		case DEFENSE : return defenseValue(card);
		case POWER : return powerValue(card);
		case NONE : return 0;
		}
		
		return 0;
		
	}
	
	private int defenseValue(Card card) {
		switch(card){
		case ARCHER : return 80;
		case WIZARD : return 70;
		case CLERIC : return 20;
		case KNIGHT : return 30;
		case NINJA : return 60;
		default : return 0;
		}
	}
	
	private int powerValue(Card card) {
		switch(card){
		case ARCHER : return 120;
		case WIZARD : return 100;
		case CLERIC : return 40;
		case KNIGHT : return 30;
		case NINJA : return 70;
		default : return 0;
		}
	}

	private int assultValue(Card card) {
		switch(card){
		case ARCHER : return 40;
		case WIZARD : return 40;
		case CLERIC : return 10;
		case KNIGHT : return 120;
		case NINJA : return 50;
		default : return 0;
		}
	}

	@Override
	public double normalize(double delta) {
		return NormUtil.normalize(delta, -MAX_VAL, MAX_VAL, 1, 0);
	}

	@Override
	public String title() {
		return "Heuristic Evaluator [positional=" + positional + ", heal="+healMultiplier+", spread="+spreadMultiplier+"]";
	}

	@Override
	public IStateEvaluator copy() {
		return new HeuristicEvaluator(winVal);
	}
}
