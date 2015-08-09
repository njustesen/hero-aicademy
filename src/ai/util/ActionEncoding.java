package ai.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import model.Card;
import model.Position;
import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.SingletonAction;
import action.SwapCardAction;
import action.UnitAction;
import action.UnitActionType;

public class ActionEncoding {
	
	private static Map<String, Card> cardCodes = new HashMap<String, Card>();
	static {
		for (Card card : Card.values()) 
			cardCodes.put(card.name().substring(0, 2), card);
	}
	private static Map<String, UnitActionType> actionTypeCodes = new HashMap<String, UnitActionType>();
	static {
		for (UnitActionType type : UnitActionType.values()) 
			actionTypeCodes.put(type.name().substring(0, 1), type);
	}

	public static Action decode(String action){
		
		if (action.startsWith("e")){
			return SingletonAction.endTurnAction;
		} else if (action.startsWith("s")){
			return SingletonAction.swapActions.get(cardCodes.get(action.split("s")[1]));
		} else if (action.startsWith("d")){
			return new DropAction(cardCodes.get(action.split("d")[1].substring(0, 2)), 
					position(action.split("<")[1].split(">")[0]));
		} else if (action.startsWith("u")){
			return new UnitAction(position(action.split("<")[1].split(">")[0]), 
					position(action.split("<")[2].split(">")[0]), actionTypeCodes.get(action.split(">")[2].substring(0, 1)));
		}
		
		return null;
		
	}
	
	public static List<Action> decodeMove (String moveStr){
		List<Action> move = new ArrayList<Action>();
		for(String action : moveStr.split(" "))
			move.add(decode(action));
		return move;
	}

	private static Position position(String pos) {
		return new Position(Integer.parseInt(pos.split(",")[0]), Integer.parseInt(pos.split(",")[1]));
	}

	public static String encode(Action action){
		
		if (action instanceof EndTurnAction){
			return "e";
		} else if (action instanceof SwapCardAction){
			return "s" + ((SwapCardAction)action).card.name().substring(0, 2);
		} else if (action instanceof DropAction){
			return "d" + ((DropAction)action).type.name().substring(0, 2) + ((DropAction)action).to.toString();
		} else if (action instanceof UnitAction){
			return "u" + ((UnitAction)action).from.toString() + ((UnitAction)action).to.toString() + 
					((UnitAction)action).type.name().substring(0, 1);
		}
		
		return "!!!!!!";
		
	}
	
}
