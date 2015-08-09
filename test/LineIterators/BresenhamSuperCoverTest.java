package LineIterators;

import java.util.List;

import model.Position;
import util.LineIterators;

public class BresenhamSuperCoverTest {

	public static void main(String[] args){
		
		// Horizontal
		test(1,1,4,1);
		test(4,1,1,1);
		
		// Vertical
		test(1,1,1,4);
		test(1,4,1,1);
		
		// Diagonal straight
		test(1,1,4,4);
		test(4,4,1,1);
		test(4,4,1,7);
		test(1,7,4,4);
		
		// Diagonal non-straight
		test(1,1,4,2);
	}

	private static void test(int x0, int y0, int x1, int y1) {
		Position from = new Position(x0,y0);
		Position to = new Position(x1,y1);
		System.out.println("From: " + from);
		System.out.println("To: " + to);
		System.out.println("----------");
		
		List<Position> los = LineIterators.supercoverAsList(from, to);
		for(Position pos : los){
			System.out.println(pos);
		}
		System.out.println("----------");
	}
	
}
