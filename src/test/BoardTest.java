package test;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game.Board;
import game.Mark;

class BoardTest {

	private Board board;

	/**
	 * creates a new 4x4 board
	 */
	@BeforeEach
	void setUp() {
		board = new Board(4, false);
	}

	@AfterEach
	void stop() {
		board.g.stopGUI();
	}

	/**
	 * Tests whether the liberties are correct after adding stones
	 */
	@Test
	void testAddLib() {
		board.addStone(0, 0, Mark.B);
		assertEquals(2, board.intersecs[0].getLiberties());
		assertEquals(3, board.intersecs[1].getLiberties());
		assertEquals(4, board.intersecs[5].getLiberties());
		board.addStone(1, 0, Mark.B);
		assertEquals(1, board.intersecs[0].getLiberties());
		assertEquals(2, board.intersecs[1].getLiberties());
		assertEquals(4, board.intersecs[5].getLiberties());
		board.addStone(3, 3, Mark.B);
		assertEquals(4, board.intersecs[10].getLiberties());
		assertEquals(3, board.intersecs[11].getLiberties());
		assertEquals(2, board.intersecs[15].getLiberties());
		board.addStone(3, 2, Mark.W);
		assertEquals(4, board.intersecs[10].getLiberties());
		assertEquals(2, board.intersecs[11].getLiberties());
		assertEquals(1, board.intersecs[15].getLiberties());
	}

	/**
	 * Tests whether the liberties are correct after removing stones
	 */
	@Test
	void testRemoveLib() {
		board.addStone(0, 0, Mark.B);
		board.addStone(1, 0, Mark.B);
		board.removeStone(1, 0);
		assertEquals(2, board.intersecs[0].getLiberties());
		assertEquals(3, board.intersecs[1].getLiberties());
		assertEquals(4, board.intersecs[5].getLiberties());
		board.addStone(3, 3, Mark.B);
		board.addStone(3, 2, Mark.W);
		board.removeStone(3, 2);
		assertEquals(4, board.intersecs[10].getLiberties());
		assertEquals(3, board.intersecs[11].getLiberties());
		assertEquals(2, board.intersecs[15].getLiberties());
	}

}
