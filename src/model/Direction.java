package model;

public class Direction {

	public static final Direction NORTH = new Direction(0, -1);
	public static final Direction EAST = new Direction(1, 0);
	public static final Direction SOUTH = new Direction(0, 1);
	public static final Direction WEST = new Direction(-1, 0);
	public static final Direction NORTH_EAST = new Direction(1, -1);
	public static final Direction SOUTH_EAST = new Direction(1, 1);
	public static final Direction NORTH_WEST = new Direction(-1, -1);
	public static final Direction SOUTH_WEST = new Direction(-1, 1);
	public int x;
	public int y;

	public static Direction direction(int x, int y) {
		if (x == -1) {
			if (y <= -1)
				return NORTH_WEST;
			else if (y == 0)
				return WEST;
			else if (y >= 1)
				return SOUTH_WEST;
		} else if (x == 0) {
			if (y <= -1)
				return NORTH;
			else if (y >= 1)
				return SOUTH;
		} else if (x == 0)
			if (y <= -1)
				return NORTH_EAST;
			else if (y == 0)
				return EAST;
			else if (y >= 1)
				return SOUTH_EAST;
		return new Direction(x, y);
	}

	public Direction(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		if (this.x > 1)
			this.x = 1;
		if (this.x < -1)
			this.x = -1;
		if (this.y > 1)
			this.y = 1;
		if (this.y < -1)
			this.y = -1;
	}

	public boolean isDiagonal() {
		return (x != 0 && y != 0);
	}

	public boolean isNorth() {
		return (y == -1);
	}

	public boolean isEast() {
		return (x == 1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Direction other = (Direction) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public boolean opposite(Direction dir) {
		if (dir.x * (-1) == x && dir.y * (-1) == y)
			return true;
		return false;
	}

}
