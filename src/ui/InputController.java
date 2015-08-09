package ui;

import game.GameState;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import model.Position;
import model.Unit;
import action.Action;
import action.DropAction;
import action.SingletonAction;
import action.UndoAction;
import action.UnitAction;

public class InputController implements MouseListener, KeyListener,
		MouseMotionListener {

	private final boolean humanP1;
	private final boolean humanP2;
	private final int gridX;
	private final int gridY;
	private final int squareSize;
	public GameState state;
	public Position activeSquare;
	public List<Action> possibleActions;
	public Action action;
	public int activeCardIdx;
	public int mouseX;
	public int mouseY;
	private UI ui;

	public InputController(boolean humanP1, boolean humanP2, int gridX,
			int gridY, int squareSize, UI ui) {
		this.humanP1 = humanP1;
		this.humanP2 = humanP2;
		this.squareSize = squareSize;
		this.gridX = gridX;
		this.gridY = gridY;
		this.ui = ui;
		possibleActions = new ArrayList<Action>();
		activeCardIdx = -1;
	}

	public void reset() {
		state = null;
		activeCardIdx = -1;
		possibleActions.clear();
		action = null;
		activeSquare = null;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

		// System.out.println("clicked on x:" + arg0.getX() + ", y:" +
		// arg0.getY());

		final int squareX = (arg0.getX() - gridX) / squareSize;
		final int squareY = (arg0.getY() - gridY) / squareSize;
		// System.out.println("clicked on square x:" + squareX + ", y:" +
		// squareY);

		if (state == null) {
			System.out.println("Gamestate is null");
			return;
		}

		if (arg0.getY() > gridY + state.map.height * squareSize) {
			// System.out.println("Out of grid");

			// Hand
			final int start = (ui.getWidth() / 2) - ((6 * squareSize) / 2);
			final int bottom = squareSize + state.map.height * squareSize
					+ squareSize / 4;

			for (int i = 0; i < state.currentHand().size; i++) {
				final int fromX = start + squareSize * i;
				final int toX = fromX + squareSize;
				final int fromY = bottom;
				final int toY = fromY + squareSize;

				if (arg0.getX() >= fromX && arg0.getX() <= toX
						&& arg0.getY() >= fromY && arg0.getY() <= toY) {

					cardClicked(i);
					return;
				}
			}

			// End turn
			final int buttonWidth = 90;
			int buttonStart = ui.getWidth() - 24 - squareSize*2;
			if (state.map.width < 9)
				buttonStart = (int) (ui.getWidth() - squareSize*1.5);
			final int buttonHeight = 64;
			if (arg0.getX() >= buttonStart
					&& arg0.getX() <= buttonStart + buttonWidth
					&& arg0.getY() >= bottom
					&& arg0.getY() <= bottom + buttonHeight) {
				if ((state.p1Turn && humanP1) || (!state.p1Turn && humanP2) || state.isTerminal){
					if (state.isTerminal)
						action = SingletonAction.playAgainAction;
					else
						action = SingletonAction.endTurnAction;
				}
			}

			// Undo turn
			final int undoWidth = 90 - 24;
			int undoStart = squareSize + 24;
			if (state.map.width < 9)
				undoStart = squareSize;
			final int undoHeight = 64;
			if (arg0.getX() >= undoStart
					&& arg0.getX() <= undoStart + undoWidth
					&& arg0.getY() >= bottom
					&& arg0.getY() <= bottom + undoHeight) {
				if (state.APLeft != 5) {
					action = new UndoAction();
				}
			}
			
		} else {
			
			// Door
			final int doorWidth = 30;
			int doorStart = 20;
			if (!state.p1Turn)
				doorStart = squareSize + squareSize * state.map.width + 20;
			final int doorHeight = 60;
			if (arg0.getX() >= doorStart
					&& arg0.getX() <= doorStart + doorWidth
					&& arg0.getY() >= squareSize
					&& arg0.getY() <= squareSize + doorHeight) {
				if (state.APLeft > 0) {
					if (activeCardIdx >= 0) {
						action = SingletonAction.swapActions.get(state
								.currentHand().get(activeCardIdx));
					}
				}
			}

		}

		// System.out.println("click registered");

		if (arg0.getX() < gridX || arg0.getX() > gridX + state.map.width * squareSize || arg0.getY() < gridY || arg0.getY() > gridY + state.map.height * squareSize) {
			// System.out.println("Clicked out of bounds");
			return;
		}
		squareClicked(new Position(squareX, squareY));

	}

	private void cardClicked(int i) {

		if (activeCardIdx == i) {
			reset();
			return;
		}

		activeSquare = null;
		activeCardIdx = i;
		possibleActions.clear();
		state.possibleActions(state.currentHand().get(i), possibleActions);

	}

	private void squareClicked(Position position) {

		if (activeSquare != null) {
			if (activeSquare.equals(position)) {
				reset();
				return;
			}
			for (final Action a : possibleActions) {
				if (a instanceof UnitAction) {
					if (((UnitAction) a).from.equals(activeSquare)
							&& ((UnitAction) a).to.equals(position)) {
						action = a;
						// System.out.println("Returning " + a);
						return;
					}
				}
			}
			activateSquare(position);
		} else if (activeCardIdx != -1) {
			for (final Action a : possibleActions) {
				if (a instanceof DropAction) {
					if (((DropAction) a).to.equals(position)) {
						action = a;
						// System.out.println("Returning " + a);
						return;
					}
				}
			}
			activateSquare(position);
		} else {
			if (state.unitAt(position) != null) {
				activateSquare(position);
			}
		}

	}

	private void activateSquare(Position position) {

		possibleActions.clear();

		if (state.unitAt(position) != null) {

			final Unit unit = state.unitAt(position);
			activeSquare = position;
			activeCardIdx = -1;

			if ((unit.p1Owner && humanP1 && state.p1Turn)
					|| (!unit.p1Owner && humanP2 && !state.p1Turn)) {

				state.possibleActions(unit, position, possibleActions);

			}

		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

		mouseX = arg0.getPoint().x;
		mouseY = arg0.getPoint().y;

	}

}
