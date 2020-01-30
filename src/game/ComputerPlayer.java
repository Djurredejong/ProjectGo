package game;

import exceptions.ExitProgram;

/**
 * Constructs a computer player, which has a certain strategy - this one always
 * passes.
 */
public class ComputerPlayer extends Player {

	/**
	 * Create a new AI player.
	 */
	public ComputerPlayer(String name, Mark mark) {
		super(name, mark);
	}

	/**
	 * This AI always passes.
	 */
	public int determineMove(Board board) throws ExitProgram {
		return -1;
	}

}
