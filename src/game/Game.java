package game;

import exceptions.ExitProgram;

/**
 * Starts a game of Go with two players on this computer.
 */
public class Game {

	/**
	 * The 2 players of the game.
	 */
	private Player[] players;

	/**
	 * The current player (always 0 or 1).
	 */
	private int current;

	/**
	 * The Board this game is played on.
	 */
	private Board board;

	/**
	 * Creates a new Game object, initialises players, board size and current.
	 * Parameters all received from Server, which initialises the games.
	 */
	public Game(Player player1, Player player2, int boardSize, boolean gui) {
		this.board = new Board(boardSize, gui);
		players = new Player[2];
		players[0] = player1;
		players[1] = player2;
		current = 0;
	}

	/**
	 * As long as the game has not ended, players make a move one after the other.
	 * If there are two consecutive passes in a row, the game ends.
	 */
	public void startPlay() {
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

	/**
	 * Getter method for the board this game is played on.
	 */
	public Board getBoard() {
		return this.board;
	}

}
