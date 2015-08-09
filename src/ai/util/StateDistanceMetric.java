package ai.util;

import java.util.HashMap;
import java.util.Map;

import model.Card;
import model.Unit;
import game.GameState;

public class StateDistanceMetric {

	Map<Card, Integer> p1BoardA;
	Map<Card, Integer> p2BoardA;
	Map<Card, Integer> p1BoardB;
	Map<Card, Integer> p2BoardB;
	
	public StateDistanceMetric() {
		p1BoardA = new HashMap<Card, Integer>();
		p2BoardA = new HashMap<Card, Integer>();
		p1BoardB = new HashMap<Card, Integer>();
		p2BoardB = new HashMap<Card, Integer>();
	}

	public double distance(GameState a, GameState b){
		
		//return materialDistance(a,b) + detailDistance(a,b);
		return detailDistance(a, b);
	}

	private double detailDistance(GameState a, GameState b) {
		
		double dif = 0;
		
		for(int x = 0; x < a.map.width; x++){
			for(int y = 0; y < a.map.height; y++){
				if (a.units[x][y] != null){
					if (b.units[x][y] != null)
						dif += unitDif(a.units[x][y], b.units[x][y]);
					else 
						dif += 1;
				} else if (b.units[x][y] != null)
					dif += 1;
			}
		}
		
		System.out.println("\t " + dif);
		
		return dif;
		
	}
	
	private double unitDif(Unit a, Unit b) {
		
		if (a.unitClass.card == b.unitClass.card){
			double dif = (double)Math.abs(a.hp - b.hp) / (double)Math.max(a.unitClass.maxHP, Math.max(a.hp, b.hp));
			for(Card card : a.equipment)
				if (!b.equipment.contains(card))
					dif += dif / 5;
			for(Card card : b.equipment)
				if (!a.equipment.contains(card))
					dif += dif / 5;
			return Math.min(1,dif);
		}
		return -1;
		
	}

	private double materialDistance(GameState a, GameState b) {
		
		double dif = 0;
		
		for(int x = 0; x < a.map.width; x++){
			for(int y = 0; y < a.map.height; y++){
				if (a.units[x][y] != null){
					if (a.units[x][y].p1Owner){
						if (p1BoardA.containsKey(a.units[x][y].unitClass.card))
							p1BoardA.put(a.units[x][y].unitClass.card, p1BoardA.get(a.units[x][y].unitClass.card) + 1);
						else
							p1BoardA.put(a.units[x][y].unitClass.card, 1);
					}
					if (!a.units[x][y].p1Owner){
						if (p2BoardA.containsKey(a.units[x][y].unitClass.card))
							p2BoardA.put(a.units[x][y].unitClass.card, p2BoardA.get(a.units[x][y].unitClass.card) + 1);
						else
							p2BoardA.put(a.units[x][y].unitClass.card, 1);
					}
				}
				if (b.units[x][y] != null){
					if (b.units[x][y].p1Owner){
						if (p1BoardB.containsKey(b.units[x][y].unitClass.card))
							p1BoardB.put(b.units[x][y].unitClass.card, p1BoardB.get(b.units[x][y].unitClass.card) + 1);
						else
							p1BoardB.put(b.units[x][y].unitClass.card, 1);
					}
					if (!b.units[x][y].p1Owner){
						if (p2BoardB.containsKey(b.units[x][y].unitClass.card))
							p2BoardB.put(b.units[x][y].unitClass.card, p2BoardB.get(b.units[x][y].unitClass.card) + 1);
						else
							p2BoardB.put(b.units[x][y].unitClass.card, 1);
					}
				}
			}
		}
		
		for(Card card : Card.values()){
			//dif += Math.abs(a.p1Hand.count(card) - b.p1Hand.count(card));
			//dif += Math.abs(a.p1Deck.count(card) - b.p1Deck.count(card));
			//dif += Math.abs(a.p2Hand.count(card) - b.p2Hand.count(card));
			//dif += Math.abs(a.p2Deck.count(card) - b.p2Deck.count(card));
			if (p1BoardA.containsKey(card)){
				if (p1BoardB.containsKey(card))
					dif += Math.abs(p1BoardA.get(card) - p1BoardB.get(card));
				else
					dif += Math.abs(p1BoardA.get(card));
			} else if (p1BoardB.containsKey(card)){
				if (p1BoardA.containsKey(card))
					dif += Math.abs(p1BoardA.get(card) - p1BoardB.get(card));
				else
					dif += Math.abs(p1BoardB.get(card));
			}
			if (p2BoardA.containsKey(card)){
				if (p2BoardB.containsKey(card))
					dif += Math.abs(p2BoardA.get(card) - p2BoardB.get(card));
				else
					dif += Math.abs(p2BoardA.get(card));
			} else if (p2BoardB.containsKey(card)){
				if (p2BoardA.containsKey(card))
					dif += Math.abs(p2BoardA.get(card) - p2BoardB.get(card));
				else
					dif += Math.abs(p2BoardB.get(card));
			}
		}
		
		System.out.println("\t " + dif);
		
		return dif;
				
	}
	
}