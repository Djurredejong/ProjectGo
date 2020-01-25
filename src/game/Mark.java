package game;

/**
 * Represents a mark in the Game.
 * There three possible values: UNOCCUPIED, BLACK, WHITE
 */
public enum Mark {

	U, B, W;

	/**
	 * Returns the other mark.
	 */
	public Mark other() {
		if (this == B) {
			return W;
		} else if (this == W) {
			return B;
		} else {
			return U;
		}
	}

	/**
	 * returns false for B, true for W
	 * 
	 * @requires this to be either B or W !!!
	 */
	public boolean color() {
		if (this == B) {
			return false;
		} else {
			return true;
		}

	}
}
