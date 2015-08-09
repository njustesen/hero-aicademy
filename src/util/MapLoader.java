package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.HaMap;
import model.Position;
import model.SquareType;

public class MapLoader {
	
	private static final char P1CRYSTAL = 'c';
	private static final char P2CRYSTAL = 'C';
	static Map<Character, SquareType> codes = new HashMap<Character, SquareType>();
	static {
		codes.put('0', SquareType.NONE);
		codes.put('d', SquareType.DEPLOY_1);
		codes.put('D', SquareType.DEPLOY_2);
		codes.put('S', SquareType.DEFENSE);
		codes.put('A', SquareType.ASSAULT);
		codes.put('P', SquareType.POWER);
		codes.put('c', SquareType.NONE);
		codes.put('C', SquareType.NONE);
		//codes.put('H', SquareType.);
	}
	static Map<String, HaMap> maps = new HashMap<String, HaMap>();

	public static HaMap get(String name) throws IOException{

		if (maps.containsKey(name)) 
			return maps.get(name);
		else
			load (name);
		
		return get(name);
	}
	
	public static void load(String name) throws IOException{
	
		String basePath = PathHelper.basePath();
		List<String> lines = readLines(basePath + "/maps/"+name+".mhap");
	
		List<Position> p1Crystals = new ArrayList<Position>();
		List<Position> p2Crystals = new ArrayList<Position>();
		List<List<SquareType>> squareLists = new ArrayList<List<SquareType>>();
		int y = 0;
		for(String line : lines){
			if (line.length() == 0 || line.charAt(0) == '#')
				continue;
			List<SquareType> squareList = new ArrayList<SquareType>();
			
			for(int x = 0; x < line.length(); x++){
				if (line.charAt(x) == P1CRYSTAL)
					p1Crystals.add(new Position(x, y));
				else if (line.charAt(x) == P2CRYSTAL)
					p2Crystals.add(new Position(x, y));
				squareList.add(codes.get(line.charAt(x)));
			}
			squareLists.add(squareList);
			y++;
		}
		
		int height = squareLists.size();
		int width = squareLists.get(0).size();
		
		final SquareType[][] grid = new SquareType[width][height];
		for(y = 0; y < height; y++)
			for(int x = 0; x < width; x++)
				grid[x][y] = squareLists.get(y).get(x);
		
		final HaMap map = new HaMap(width, height, grid, name);
		
		for (Position p : p1Crystals)
			map.p1Crystals.add(p);
		for (Position p : p2Crystals)
			map.p2Crystals.add(p);
		
		maps.put(name, map);
	}

	static List<String> readLines( String filename ) throws IOException {
	    BufferedReader	reader = new BufferedReader( new FileReader (filename));
	    String       	line = null;
	    List<String> 	lines = new ArrayList<String>();

	    while( ( line = reader.readLine() ) != null ) {
	        lines.add(line);
	    }

	    reader.close();
	    
	    return lines;
	}
	
}
