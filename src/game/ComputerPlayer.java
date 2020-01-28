package game;

import exceptions.ExitProgram;

public class ComputerPlayer extends Player {

	/**
	 * Create a new human player.
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
