package crossover;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import action.Action;
import action.DropAction;
import action.UnitAction;
import action.UnitActionType;
import model.Card;
import model.DECK_SIZE;
import model.Position;
import model.Unit;
import game.GameState;
import ui.UI;
import util.MapLoader;

public class CrossoverVisualization {

	public static void main (String[] args) throws IOException{
		
		GameState state = new GameState(MapLoader.get("a"));
		
		state.init(DECK_SIZE.STANDARD);
		state.p1Hand.clear();
		//state.p2Hand.clear();
		state.p1Hand.add(Card.ARCHER);
		state.p1Hand.add(Card.KNIGHT);
		state.p1Hand.add(Card.REVIVE_POTION);
		state.p1Hand.add(Card.RUNEMETAL);
		state.p1Hand.add(Card.RUNEMETAL);
		state.p1Hand.add(Card.SHINING_HELM);
		
		state.turn = 14;
		state.APLeft = 5;
		
		state.units[1][2] = new Unit(Card.CLERIC, true);
		state.units[2][0] = new Unit(Card.WIZARD, true);
		state.units[2][2] = new Unit(Card.ARCHER, true);
		state.units[2][2].equipment.add(Card.DRAGONSCALE);
		state.units[2][2].hp = 400;
		
		state.units[1][4] = new Unit(Card.KNIGHT, true);
		state.units[1][4].equipment.add(Card.DRAGONSCALE);
		state.units[1][4].hp = 680;
		
		state.units[7][1] = new Unit(Card.CLERIC, false);
		state.units[6][4] = new Unit(Card.WIZARD, false);
		state.units[6][4].hp = 600;
		
		state.units[8][0] = new Unit(Card.WIZARD, false);
		state.units[8][0].equipment.add(Card.SHINING_HELM);
		state.units[8][0].hp = 880;
		
		state.units[5][3] = new Unit(Card.ARCHER, false);
		state.units[5][3].equipment.add(Card.DRAGONSCALE);
		state.units[5][3].equipment.add(Card.RUNEMETAL);
		state.units[5][3].hp = 880;
		
		state.units[8][4] = new Unit(Card.KNIGHT, false);
		
		UI ui = new UI(state, false, true, "", "", false);
		
		ui.repaint();
		
		List<Action> actionsA = new ArrayList<Action>();
		actionsA.add(new UnitAction(new Position(1,2), new Position(2,2), UnitActionType.HEAL));
		actionsA.add(new UnitAction(new Position(2,2), new Position(2,3), UnitActionType.MOVE));
		actionsA.add(new UnitAction(new Position(2,3), new Position(5,3), UnitActionType.ATTACK));
		actionsA.add(new UnitAction(new Position(2,3), new Position(5,3), UnitActionType.ATTACK));
		actionsA.add(new UnitAction(new Position(2,3), new Position(5,3), UnitActionType.ATTACK));
		
		
		List<Action> actionsB = new ArrayList<Action>();
		actionsB.add(new UnitAction(new Position(1,2), new Position(2,2), UnitActionType.HEAL));
		actionsB.add(new UnitAction(new Position(1,2), new Position(1,4), UnitActionType.HEAL));
		actionsB.add(new UnitAction(new Position(2,0), new Position(4,0), UnitActionType.MOVE));
		actionsB.add(new DropAction(Card.RUNEMETAL, new Position(4,0)));
		actionsB.add(new DropAction(Card.SHINING_HELM, new Position(1,4)));
		
		List<Action> actionsC = new ArrayList<Action>();
		actionsC.add(new UnitAction(new Position(1,2), new Position(2,2), UnitActionType.HEAL));
		actionsC.add(new UnitAction(new Position(2,2), new Position(2,3), UnitActionType.MOVE));
		actionsC.add(new UnitAction(new Position(2,0), new Position(4,0), UnitActionType.MOVE));
		actionsC.add(new DropAction(Card.RUNEMETAL, new Position(4,0)));
		actionsC.add(new UnitAction(new Position(2,3), new Position(5,3), UnitActionType.ATTACK));

		ui.setActionLayer(actionsC);
		
		ui.repaint();
	}
	
}
