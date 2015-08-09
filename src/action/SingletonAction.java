package action;

import java.util.HashMap;
import java.util.Map;

import model.Card;
import model.HaMap;
import model.Position;

public class SingletonAction {

	public static final EndTurnAction endTurnAction = new EndTurnAction();
	public static final PlayAgainAction playAgainAction = new PlayAgainAction();
	public static final UndoAction undoAction = new UndoAction();
	public static final Map<Card, SwapCardAction> swapActions = new HashMap<Card, SwapCardAction>();
	static {
		swapActions.put(Card.ARCHER, new SwapCardAction(Card.ARCHER));
		swapActions.put(Card.CLERIC, new SwapCardAction(Card.CLERIC));
		swapActions.put(Card.DRAGONSCALE, new SwapCardAction(Card.DRAGONSCALE));
		swapActions.put(Card.INFERNO, new SwapCardAction(Card.INFERNO));
		swapActions.put(Card.KNIGHT, new SwapCardAction(Card.KNIGHT));
		swapActions.put(Card.NINJA, new SwapCardAction(Card.NINJA));
		swapActions.put(Card.REVIVE_POTION, new SwapCardAction(
				Card.REVIVE_POTION));
		swapActions.put(Card.RUNEMETAL, new SwapCardAction(Card.RUNEMETAL));
		swapActions.put(Card.SCROLL, new SwapCardAction(Card.SCROLL));
		swapActions.put(Card.SHINING_HELM,
				new SwapCardAction(Card.SHINING_HELM));
		swapActions.put(Card.WIZARD, new SwapCardAction(Card.WIZARD));
	}
	
	public static Position[][] positions;
	
	public static void init(HaMap map){
		
		positions = new Position[map.width + 10][map.height + 10];
		
		for(int x = 0; x < map.width; x++)
			for(int y = 0; y < map.height; y++)
				positions[x][y] = new Position(x,y);
		
	}

}
