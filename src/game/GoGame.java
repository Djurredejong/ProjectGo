package game;

import exceptions.ExitProgram;

public class GoGame {

	/**
	 * initialises the two players and starts a new game
	 */
	public static void main(String args[]) {
		Player p0 = new HumanPlayer("player1", Mark.B);
		Player p1 = new ComputerPlayer("player2", Mark.W);

		Game game = new Game(p0, p1, 4, true);
		try {
			game.startPlay();
		} catch (ExitProgram e) {
			// TODO exit the program
			e.printStackTrace();
		}

	}
}
