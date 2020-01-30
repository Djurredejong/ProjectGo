package test;

import exceptions.ExitProgram;
import game.Board;
import game.ComputerPlayer;
import game.Mark;
import game.Player;

/**
 * This class tests whether (and confirms on running it that) two AI players can
 * play against one another and whether the scoring algorithm is correctly
 * implemented.
 */
public class ScoreTest {

	/**
	 * The current player (always 0 or 1).
	 */
	private static int current;

	/**
	 * The Board this game is played on.
	 */
	private static Board board;

	/**
	 * The 2 players of the game.
	 */
	private static Player[] players;

	/**
	 * initialises the two players and starts a new game. Pick the test situation
	 * you're interested in by commenting out the other two!
	 */
	public static void main(String args[]) {
		System.out.println("Test the scoring functionality of the Go game.");
		board = new Board(4, true);
		// testSituation1();
		// testSituation2();
		testSituation3();
		Player p0 = new ComputerPlayer("AIblack", Mark.B);
		Player p1 = new ComputerPlayer("AIwhite", Mark.W);
		players = new Player[2];
		players[0] = p0;
		players[1] = p1;
		current = 0;
		startPlay();
	}

	/**
	 * Test scoring with both black and white stones on the board, with shared area
	 * and a different amount of points. Should yield B: 4.0, W: 8.5.
	 */
	private static void testSituation3() {
		for (int row = 0; row < 4; row++) {
			try {
				board.putStone(0, row, Mark.B);
			} catch (ExitProgram e) {
				e.printStackTrace();
			}
		}
		for (int row = 0; row < 4; row++) {
			try {
				board.putStone(2, row, Mark.W);
			} catch (ExitProgram e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Test scoring with both black and white stones on the board, only non-shared
	 * area and the same amount of points. Should yield B: 8.0, W: 8.5.
	 */
	private static void testSituation2() {
		for (int row = 0; row < 4; row++) {
			try {
				board.putStone(1, row, Mark.B);
			} catch (ExitProgram e) {
				e.printStackTrace();
			}
		}
		for (int row = 0; row < 4; row++) {
			try {
				board.putStone(2, row, Mark.W);
			} catch (ExitProgram e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Test scoring with only black stones on the board and a bit of empty area.
	 * Should yield B: 16.0, W: 0.5.
	 */
	private static void testSituation1() {
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 3; col++) {
				try {
					board.putStone(col, row, Mark.B);
				} catch (ExitProgram e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * As long as the game has not ended, players make a move one after the other.
	 * If there are two consecutive passes in a row, the game ends.
	 */
	public static void startPlay() {
		boolean gameOver = false;
		int consecPass = 0;
		try {
			while (!gameOver) {
				System.out.println();
				System.out.println(players[current].getName() + ", it's your turn!");
				if (players[current].makeMove(board) == -1) {
					consecPass++;
					System.out.println(players[current].getName() + " has passed.");
				} else {
					consecPass = 0;
				}
				if (consecPass == 2) {
					gameOver = true;
				} else {
					gameOver = board.gameOver();
				}
				current++;
				current = current % 2;
			}
			System.out.println("Going to count the score.");
			if (board.determineWinner()) {
				System.out.println("White has won!");
			} else {
				System.out.println("Black has won!");
			}
		} catch (ExitProgram e) {
			System.out.println("Goodbye!");
			board.getGUI().stopGUI();
		}
	}
}
