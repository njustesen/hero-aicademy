package game;

import java.io.IOException;
import java.util.Stack;

import model.HaMap;
import ui.UI;
import util.CachedLines;
import util.MapLoader;
import action.Action;
import action.SingletonAction;
import action.UndoAction;
import ai.AI;
import ai.evolution.AiVisualizor;

public class Game {

	public GameState state;
	public UI ui;
	public AI player1;
	public AI player2;
	public GameArguments gameArgs;
	private Stack<GameState> history;
	private int lastTurn;
	
	public static void main(String[] args) {
	
		GameArguments gameArgs = new GameArguments(args);
		
		HaMap map;
		try{
			map = MapLoader.get(gameArgs.mapName);
		} catch (IOException e){
			System.out.println("Map not found.");
			return;
		}
		try {
			GameState state = new GameState(map);
			final Game game = new Game(state, gameArgs);
			game.run();
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	public Game(GameState state, GameArguments gameArgs) {
		this.gameArgs = gameArgs;
		this.player1 = gameArgs.players[0];
		this.player2 = gameArgs.players[1];
		
		history = new Stack<GameState>();
		if (state == null)
			this.state = new GameState(null);
		else
			this.state = state;

		// Some speed optimizations
		synchronized (Game.class) {
		
			if (this.state.map == null){
				try {
					this.state = new GameState(MapLoader.get(gameArgs.mapName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (SingletonAction.positions == null)
				SingletonAction.init(this.state.map);
			
			if (CachedLines.posMap.isEmpty() || this.state.map != CachedLines.map)
				CachedLines.load(this.state.map);
			
		}
		
		String player1Title = player1 == null ? "Human" : player1.title();
		String player2Title = player2 == null ? "Human" : player2.title();
		
		if (gameArgs.gfx){
			this.ui = new UI(this.state, (this.player1 == null), (this.player2 == null), player1Title, player2Title );
			/*
			if (player1 instanceof AiVisualizor)
				((AiVisualizor)player1).enableVisualization(ui);
			if (player2 instanceof AiVisualizor)
				((AiVisualizor)player2).enableVisualization(ui);
			*/
		}
		
		history = new Stack<GameState>();
		

	}

	public void run() {
		
		state.init(gameArgs.deckSize);
		GameState initState = new GameState(state.map);
		initState.imitate(state);
		history.add(initState);
		lastTurn = 5;

		if (player1 != null)
			player1.init(state, -1);
		if (player2 != null)
			player2.init(state, -1);

		while (!state.isTerminal) {

			if (ui != null) {
				ui.state = state.copy();
				ui.repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
			
			if (state.p1Turn && player1 != null){
				act(player1, player2);
				try {
					Thread.sleep(gameArgs.sleep);
				} catch (InterruptedException e) {
				}
			}else if (!state.p1Turn && player2 != null){
				act(player2, player1);
				try {
					Thread.sleep(gameArgs.sleep);
				} catch (InterruptedException e) {
				}
			}else if (ui.action != null) {

				if (ui.action instanceof UndoAction)
					undoAction();
				else {
					state.update(ui.action);
				}
				ui.lastAction = ui.action;
				ui.resetActions();

			}
			
			if (state.APLeft != lastTurn) {
				if (state.APLeft < lastTurn){
					GameState clone = new GameState(state.map);
					clone.imitate(state);
					history.add(clone);
				}
				lastTurn = state.APLeft;
			}

			if (state.APLeft == 5) {
				history.clear();
				GameState clone = new GameState(state.map);
				clone.imitate(state);
				history.add(clone);
				lastTurn = 5;
			}

		}
		if (ui != null) {
			ui.state = state.copy();
			ui.repaint();
		}
	}

	private void act(AI player, AI other) {
		GameState clone = new GameState(state.map);
		clone.imitate(state);
		if (!GameState.OPEN_HANDS)
			clone.hideCards(!state.p1Turn);
		// AI is responsible for returning an action within budget. No enforcement implemented.
		Action action = player.act(clone, gameArgs.budget);
		if (action == null)
			action = SingletonAction.endTurnAction;
		state.update(action);
		if (ui != null)
			ui.lastAction = action;
		if (other == null)
			try {
				Thread.sleep(gameArgs.sleep);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
	}

	private void undoAction() {

		if (state.APLeft == 5)
			return;

		if (state.isTerminal)
			return;

		if (history.size() > 1){
			history.pop();
			state.imitate(history.peek());
		}
	}

}
