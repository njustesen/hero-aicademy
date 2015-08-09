package action;

import model.Card;

public class SwapCardAction extends Action {

	public Card card;

	public SwapCardAction(Card card) {
		super();
		this.card = card;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SwapCardAction other = (SwapCardAction) obj;
		if (card != other.card)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SwapCardAction [card=" + card.name() + "]";
	}

}
