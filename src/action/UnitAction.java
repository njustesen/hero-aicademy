package action;

import model.Position;

public class UnitAction extends Action {

	public Position from;
	public Position to;
	public UnitActionType type;

	public UnitAction(Position from, Position to, UnitActionType type) {
		super();
		this.from = from;
		this.to = to;
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnitAction other = (UnitAction) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		if (type != other.type)
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "UnitAction [from=" + from + ", to=" + to + "]";
	}

}
