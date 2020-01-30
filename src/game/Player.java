package game;

import exceptions.ExitProgram;

/**
 * Abstract class to create players from.
 */
public abstract class Player {

	/**
	 * The mark of this player, either B or W
	 */
	private Mark mark;

	/**
	 * The name of this player
	 */
	private String name;

	/**
	 * Creates a new player with the provided name and mark
	 */
	public Player(String name, Mark mark) {
		this.name = name;
		this.mark = mark;
	}

	/**
	 * Determines the next move. Returns the intersection number or -1 for pass.
	 */
	public abstract int determineMove(Board board) throws ExitProgram;

	/**
	 * Makes a move on the board.
	 */
	public int makeMove(Board board) throws ExitProgram {
		int i = determineMove(board);
		if (i == -1) {
			return i;
		} else {
			board.putStone(i % board.getBoardSize(), i / board.getBoardSize(), this.mark);
			return i;
		}
	}

	/**
	 * Getter method for the name of this player.
	 */
	public String getName() {
		return this.name;
	}

}
