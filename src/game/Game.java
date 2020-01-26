package game;

import exceptions.ExitProgram;

//import server.ClientHandler;

public class Game {

	/**
	 * The 2 players of the game
	 */
	private Player[] players;

	/**
	 * The current player (always 0 or 1)
	 */
	private int current;

	/**
	 * The Board this game is played on
	 */
	private Board board;

	/**
	 * Creates a new Game object, initialises players, board size and current
	 * Parameters all received from Server, which initialises the games
	 */
	public Game(Player player1, Player player2, int boardSize) {
		this.board = new Board(boardSize, true);
		players = new Player[2];
		players[0] = player1;
		players[1] = player2;
		current = 0;
	}

	/**
	 * As long as the game has not ended, players make a move one after the other
	 */
	public void startPlay() throws ExitProgram {
		while (!board.gameOver()) {
			players[current].makeMove(board);
			current++;
			current = current % 2;
		}
	}

}
