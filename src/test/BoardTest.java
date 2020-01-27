package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exceptions.ExitProgram;
import game.Board;
import game.Mark;

class BoardTest {

	private Board board;

	/**
	 * Creates a new 4x4 board.
	 */
	@BeforeEach
	void setUp() throws Exception {
		board = new Board(4, false);
	}

//	@AfterEach
//	void stop() {
//		board.g.stopGUI();
//	}

	/**
	 * Tests whether placing a stone cannot be done on top of another stone until
	 * that other stone has been removed.
	 */
	@Test
	void testPlace() throws Exception {
		board.putStone(0, 0, Mark.B);
		try {
			board.putStone(0, 0, Mark.W);
		} catch (ExitProgram e) {
		}
		assertEquals(Mark.B, board.intersecs[0].getMark());
		board.removeStone(0, 0);
		board.putStone(0, 0, Mark.W);
	}

	/**
	 * Tests whether the liberties are correct after adding stones.
	 */
	@Test
	void testAddLib() throws Exception {
		board.putStone(0, 0, Mark.B);
		assertEquals(2, board.intersecs[0].getLiberties());
		assertEquals(3, board.intersecs[1].getLiberties());
		assertEquals(4, board.intersecs[5].getLiberties());
		board.putStone(1, 0, Mark.B);
		assertEquals(1, board.intersecs[0].getLiberties());
		assertEquals(2, board.intersecs[1].getLiberties());
		assertEquals(4, board.intersecs[5].getLiberties());
		board.putStone(3, 3, Mark.B);
		assertEquals(4, board.intersecs[10].getLiberties());
		assertEquals(3, board.intersecs[11].getLiberties());
		assertEquals(2, board.intersecs[15].getLiberties());
		board.putStone(3, 2, Mark.W);
		assertEquals(4, board.intersecs[10].getLiberties());
		assertEquals(2, board.intersecs[11].getLiberties());
		assertEquals(1, board.intersecs[15].getLiberties());
	}

	/**
	 * Tests whether the liberties are correct after removing stones.
	 */
	@Test
	void testRemoveLib() throws Exception {
		board.putStone(0, 0, Mark.B);
		board.putStone(1, 0, Mark.B);
		board.removeStone(1, 0);
		assertEquals(2, board.intersecs[0].getLiberties());
		assertEquals(3, board.intersecs[1].getLiberties());
		assertEquals(4, board.intersecs[5].getLiberties());
		board.putStone(3, 3, Mark.B);
		board.putStone(3, 2, Mark.W);
		board.removeStone(3, 2);
		assertEquals(4, board.intersecs[10].getLiberties());
		assertEquals(3, board.intersecs[11].getLiberties());
		assertEquals(2, board.intersecs[15].getLiberties());
	}

	/**
	 * Tests whether a newly placed stone without neighbouring stones of the same
	 * colour is assigned its own chain and chains are correctly joined upon placing
	 * a stone next to another one of the same colour. Also tests the chain's
	 * liberties
	 */
	@Test
	void testJoinChains() throws Exception {
		board.putStone(0, 0, Mark.B);
		board.putStone(1, 0, Mark.B);
		board.putStone(2, 0, Mark.W);
		assertEquals(board.intersecs[0].getChain(), board.intersecs[1].getChain());
		assertEquals(2, board.intersecs[0].getChain().chainLib());
		board.putStone(3, 0, Mark.B);
		assertNotEquals(board.intersecs[0].getChain(), board.intersecs[3].getChain());
		assertEquals(2, board.intersecs[0].getChain().chainLib());
		board.removeStone(2, 0);
		assertNotEquals(board.intersecs[0].getChain(), board.intersecs[3].getChain());
		assertEquals(3, board.intersecs[0].getChain().chainLib());
		board.putStone(2, 1, Mark.B);
		board.putStone(2, 0, Mark.B);
		assertEquals(board.intersecs[0].getChain(), board.intersecs[3].getChain());
		assertEquals(board.intersecs[6].getChain(), board.intersecs[3].getChain());
		// TODO don't count liberties twice!
		assertEquals(4, board.intersecs[0].getChain().chainLib());
	}

	/**
	 * Tests whether a chain of stones is removed when captured. Alsoests whether
	 * the liberties are correct after removing a chain of stones.
	 */
	@Test
	void testRemoveChain() throws Exception {
		board.putStone(0, 0, Mark.B);
		board.putStone(0, 1, Mark.B);
		board.putStone(1, 0, Mark.B);
		board.putStone(2, 0, Mark.W);
		board.putStone(1, 1, Mark.W);
		board.putStone(0, 2, Mark.W);
		assertEquals(Mark.U, board.intersecs[0].getMark());
		assertEquals(Mark.U, board.intersecs[1].getMark());
		assertEquals(Mark.U, board.intersecs[3].getMark());
		assertEquals(2, board.intersecs[0].getLiberties());
		assertEquals(3, board.intersecs[1].getLiberties());
		assertEquals(3, board.intersecs[2].getLiberties());
		// assertEquals(3, board.intersecs[4].getLiberties());
		// assertEquals(4, board.intersecs[5].getLiberties());
	}

}
