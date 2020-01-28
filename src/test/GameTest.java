package test;

import org.junit.jupiter.api.BeforeEach;

import game.Board;
import game.Game;
import game.HumanPlayer;
import game.Mark;
import game.Player;

class GameTest {

	private Game game;

	private Board board;

	/**
	 * Creates a new game on a 4x4 board.
	 */
	@BeforeEach
	void setUp() throws Exception {
		Player p0 = new HumanPlayer("player1", Mark.B);
		Player p1 = new HumanPlayer("player2", Mark.W);
		Game game = new Game(p0, p1, 4, false);
		this.board = game.getBoard();
	}

}
