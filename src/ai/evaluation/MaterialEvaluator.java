package ai.evaluation;

import java.util.HashMap;
import java.util.Map;

import ai.util.NormUtil;
import game.GameState;
import model.Card;
import model.CardType;

public class MaterialEvaluator implements IStateEvaluator {

	private static int MAX_VAL = 0;
	private static Map<Card, Double> values;
	static {
		values = new HashMap<Card, Double>();
		values.put(Card.ARCHER, 1.1);
		MAX_VAL += 1.1*3;
		values.put(Card.CLERIC, 1.2);
		MAX_VAL += 1.2*3;
		values.put(Card.DRAGONSCALE, .4);
		MAX_VAL += .4*3;
		values.put(Card.INFERNO, 1.2);
		MAX_VAL += 1.2*2;
		values.put(Card.KNIGHT, 1.0);
		MAX_VAL += 1*3;
		values.put(Card.NINJA, 1.5);
		MAX_VAL += 1.5;
		values.put(Card.REVIVE_POTION, .9);
		MAX_VAL += .9*2;
		values.put(Card.RUNEMETAL, .4);
		MAX_VAL += .4*3;
		values.put(Card.SCROLL, .9);
		MAX_VAL += .9*2;
		values.put(Card.SHINING_HELM, .4);
		MAX_VAL += .4*3;
		values.put(Card.WIZARD, 1.1);
		MAX_VAL += 1.1*3;
	}

	private boolean winVal;
	
	public MaterialEvaluator(boolean winVal) {
		this.winVal = winVal;
	}

	public double eval(GameState state, boolean p1) {

		if (state.isTerminal){
			int winner = state.getWinner();
			if (winner == 0){
				if (winVal)
					return 0.5;
				return 0;
			} else if (winVal){
				if (winner == 1)
					if (p1)
						return 1;
					else
						return 0;
				else
					if (p1)
						return 0;
					else
						return 1;
			}
			return matDif(state, p1) * 2;
		}
		
		if (!winVal)
			return matDif(state, p1);
		
		double delta = matDif(state, p1);
		if (delta == 0)
			delta = 0.5;
		else if (delta > 0)
			return 1;
		
		return 0;
	}

	private int matDif(GameState state, boolean p1) {
		int p1Units = 0;
		int p2Units = 0;
		for (int x = 0; x < state.map.width; x++){
			for (int y = 0; y < state.map.height; y++){
				if (state.units[x][y] != null){
					if (state.units[x][y].p1Owner)
						p1Units += values.get(state.units[x][y].unitClass.card);
					else
						p2Units += values.get(state.units[x][y].unitClass.card);
				}
			}
		}
		// TODO: Opponent hand should be hidden
		for (final Card card : Card.values()){
			if (card.type != CardType.UNIT)
				continue;
			p1Units += values.get(card) * state.p1Deck.count(card);
			p2Units += values.get(card) * state.p2Deck.count(card);
			p1Units += values.get(card) * state.p1Hand.count(card);
			p2Units += values.get(card) * state.p2Hand.count(card);
		}
		
		if (p1)
			return p1Units - p2Units;
		return p2Units - p1Units;
	}

	@Override
	public double normalize(double delta) {
		return NormUtil.normalize(delta, -MAX_VAL, MAX_VAL, 1, 0);
	}

	@Override
	public String title() {
		return "Material Evaluator";
	}

	@Override
	public IStateEvaluator copy() {
		return new MaterialEvaluator(winVal);
	}

}
