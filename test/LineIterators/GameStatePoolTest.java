package LineIterators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.GameState;
import model.Card;
import model.HaMap;
import model.Unit;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import util.MapLoader;
import util.pool.GameStateFactory;
import util.pool.UnitFactory;

public class GameStatePoolTest {

	public static void main(String[] args){
		try {
			borrowNReturnN(100);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void borrowReturn() throws IOException {
		
		ObjectPool<GameState> pool = new GenericObjectPool<GameState>(new GameStateFactory());
		GameState state = new GameState(MapLoader.get("a"));
		
		for(int i = 0; i < 100; i++){
			GameState clone = null;
			try {
				clone = pool.borrowObject();
				clone.imitate(state);
				clone.turn = (int) (Math.random() * 20);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			} finally {
				if (clone != null){
					System.out.println(i);
					try {
						pool.returnObject(clone);
					} catch (Exception e) {
						System.out.println(e);
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}
	
	private static void borrowNReturnN(int n) throws IOException {
		
		GenericObjectPool<GameState> pool = new GenericObjectPool<GameState>(new GameStateFactory());
		pool.setBlockWhenExhausted(false);
		pool.setMaxTotal(n);
		List<GameState> states = new ArrayList<GameState>();
		GameState state = new GameState(MapLoader.get("a"));
		
		for(int i = 0; i < n; i++){
			GameState clone = null;
			try {
				clone = pool.borrowObject();
				clone.imitate(state);
				clone.turn = (int) (Math.random() * 20);
				states.add(clone);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
			System.out.println(i);
		}
		
		int i=0;
		for(GameState s : states){
			//s.reset();
			try {
				pool.returnObject(s);
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
			System.out.println(i);
		}
		
	}
	
	
}
