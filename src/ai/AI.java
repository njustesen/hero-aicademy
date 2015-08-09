package ai;

import game.GameState;
import action.Action;

public interface AI {
	
	public Action act(GameState state, long ms);
	
	public abstract void init(GameState state, long ms);
	
	public abstract AI copy();
	
	public abstract String header();
	
	public abstract String title();
	
}
