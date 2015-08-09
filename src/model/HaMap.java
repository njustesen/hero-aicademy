package model;

import java.util.ArrayList;
import java.util.List;

public class HaMap {

	public String name;
	public int width;
	public int height;
	public SquareType[][] squares;
	public List<Position> assaultSquares;
	public List<Position> p1DeploySquares;
	public List<Position> p2DeploySquares;
	public List<Position> p1Crystals;
	public List<Position> p2Crystals;

	public HaMap(int width, int height, SquareType[][] squares, String name) {
		super();
		this.name = name;
		this.width = width;
		this.height = height;
		this.squares = squares;
		assaultSquares = new ArrayList<Position>();
		p1DeploySquares = new ArrayList<Position>();
		p2DeploySquares = new ArrayList<Position>();
		p1Crystals = new ArrayList<Position>();
		p2Crystals = new ArrayList<Position>();
		for (int x = 0; x < squares.length; x++)
			for (int y = 0; y < squares[0].length; y++) {
				if (squares[x][y] == SquareType.DEPLOY_1)
					p1DeploySquares.add(new Position(x, y));
				if (squares[x][y] == SquareType.DEPLOY_2)
					p2DeploySquares.add(new Position(x, y));
				if (squares[x][y] == SquareType.ASSAULT)
					assaultSquares.add(new Position(x, y));
			}
	}

	public SquareType squareAt(int x, int y) {

		return squares[x][y];

	}

	public SquareType squareAt(Position position) {
		return squareAt(position.x, position.y);
	}

}
