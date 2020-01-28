package game;

/**
 * Represents the contents of an intersection. There three possible values:
 * UNOCCUPIED, BLACK, WHITE.
 */
public enum Mark {

	U, B, W;

	/**
	 * If there is a stone of certain colour, it returns the other colour.
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
	 * Returns false for B, true for W. Useful for the GUI functions.
	 * 
	 * @requires this to be either B or W
	 */
	public boolean bool() {
		assert this != U : "The mark of a player cannot be U!";
		if (this == B) {
			return false;
		} else {
			return true;
		}

	}
}
