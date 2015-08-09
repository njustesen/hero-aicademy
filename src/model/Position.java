package model;

public class Position {

	public int x;
	public int y;

	public Position(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Position() {
		super();
		x = 0;
		y = 0;
	}

	public Direction getDirection(Position pos) {
		if (pos == null)
			return null;
		return Direction.direction(pos.x - x, pos.y - y);
	}

	public int hashCode() {
		int result = 1;
		result = 5 * result + x;
		result = 5 * result + y;
		return result;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public int distance(Position to) {
		int xx = x - to.x;
		if (xx < 0)
			xx = xx * (-1);
		int yy = y - to.y;
		if (yy < 0)
			yy = yy * (-1);
		return xx + yy;
	}

}
