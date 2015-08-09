package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.HaMap;
import model.Position;

public class CachedLines {

	public static Map<Position, Map<Position, List<Position>>> posMap = new HashMap<Position, Map<Position, List<Position>>>();
	public static HaMap map;
	
	public static List<Position> supercover(Position from, Position to){
		return posMap.get(from).get(to);
	}
	
	public static void load(HaMap map){
		CachedLines.map = map;
		for(int x = 0; x < map.width; x++){
			for(int y = 0; y < map.height; y++){
				Position from = new Position(x, y);
				posMap.put(from, new HashMap<Position, List<Position>>());
				
				for(int xx = 0; xx < map.width; xx++){
					for(int yy = 0; yy < map.height; yy++){
						
						Position to = new Position(xx, yy);
						if (from.equals(to))
							continue;
						List<Position> blocked = new ArrayList<Position>();
						blocked.addAll(LineIterators.supercoverAsList(from, to));
								
						if (from.x == 1 && from.y == 1 && to.x == 4 && to.y == 5)
							System.out.println("GOT IT");
						posMap.get(from).put(to, blocked);
						
					}
				}
				
			}
		}
		
	}
	
}
