package ai.util;

import model.Card;
import model.SquareType;
import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.SwapCardAction;
import action.UnitAction;
import action.UnitActionType;

public class SimpleActionComparator extends ActionComparator {


	@Override
	public int value(Action action) {

		if (action instanceof EndTurnAction)
			if (state.APLeft == 0)
				return 10000;
			else
				return 0;

		if (action instanceof SwapCardAction)
			return 0;

		if (action instanceof DropAction) {
			final DropAction drop = ((DropAction) action);
			if (drop.type == Card.INFERNO) {
				return 3;
			} else if (drop.type == Card.REVIVE_POTION) {
				if (state.units[drop.to.x][drop.to.y].hp == 0)
					return 10;
				else
					return 3;
			} else if (drop.type == Card.SCROLL)
				return 3;
			else if (drop.type == Card.DRAGONSCALE)
				return 3;
			else if (drop.type == Card.RUNEMETAL)
				return 3;
			else if (drop.type == Card.SHINING_HELM)
				return 3;
			else
				return 6;
		} else if (action instanceof UnitAction)
			if (((UnitAction) action).type == UnitActionType.ATTACK) {
				if (state.units[((UnitAction) action).to.x][((UnitAction) action).to.y].hp == 0)
					return 10;
				else
					return 7;
			} else if (((UnitAction) action).type == UnitActionType.HEAL) {
				if (state.units[((UnitAction) action).to.x][((UnitAction) action).to.y].hp == 0)
					return 10;
				else
					return 7;
			} else if (((UnitAction) action).type == UnitActionType.SWAP)
				return 3;
			else if (((UnitAction) action).type == UnitActionType.MOVE) {
				if (state.units[((UnitAction) action).to.x][((UnitAction) action).to.y] != null)
					return 10;
				if (state.map.squares[((UnitAction) action).to.x][((UnitAction) action).to.y] == SquareType.NONE)
					return 4;
				else
					return 3;
			}
		return 0;
	}

}
