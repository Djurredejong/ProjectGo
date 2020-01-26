package game;

import com.nedap.go.gui.GoGUIIntegrator;

import exceptions.ExitProgram;
import protocol.ProtocolMessages;

public class Board {

	/**
	 * The size of the board and getter
	 */
	private int boardSize;

	public int getBoardSize() {
		return boardSize;
	}

	/**
	 * The intersections of this board. Can either be black/white stones or
	 * unoccupied
	 */
	// TODO set to public for testing
	public Intersec[] intersecs;

	/**
	 * The GUI of this board.
	 */
	// TODO set to public for testing
	public GoGUIIntegrator g;

	/**
	 * Creates an empty board, gives each intersection its initial number of
	 * liberties and its list of neighbours, starts the GUI. If gui is set to false,
	 * the GUI will not be started (useful for testing)
	 */
	public Board(int boardSize, boolean gui) {
		this.boardSize = boardSize;
		intersecs = new Intersec[boardSize * boardSize];
		int i = 0;
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				intersecs[i] = new Intersec();
				intersecs[i] = intersecs[coorToInt(col, row)];
				if (col == 0 || col == this.boardSize - 1) {
					intersecs[i].reduceLib();
				}
				if (row == 0 || row == this.boardSize - 1) {
					intersecs[i].reduceLib();
				}
				i++;
			}
		}
		i = 0;
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				if (col != 0) {
					intersecs[i].addNeighbour(intersecs[coorToInt(col - 1, row)]);
				}
				if (col != boardSize - 1) {
					intersecs[i].addNeighbour(intersecs[coorToInt(col + 1, row)]);
				}
				if (row != 0) {
					intersecs[i].addNeighbour(intersecs[coorToInt(col, row - 1)]);
				}
				if (row != boardSize - 1) {
					intersecs[i].addNeighbour(intersecs[coorToInt(col, row + 1)]);
				}
				i++;
			}
		}
		g = new GoGUIIntegrator(false, false, this.boardSize);
		if (gui) {
			g.startGUI();
		}
	}

	/**
	 * Given a column and a row, gives the corresponding intersection. Print error
	 * if column/row combination is not on the board.
	 */
	public int coorToInt(int col, int row) {
		assert !(col < 0 || col > this.boardSize || row < 0
				|| row > this.boardSize) : "Coordinates are not on the board!";
		return row * this.boardSize + col;
	}

	/**
	 * Check whether a String represents a possible board situation
	 * 
	 * @param board The String to be checked
	 * @return true if board represents a possible board situation
	 */
	public boolean checkValidBoard(String board) {
		if (board.length() != boardSize) {
			return false;
		}
		for (int i = 0; i < board.length(); i++) {
			if (board.charAt(i) != ProtocolMessages.BLACK || board.charAt(i) != ProtocolMessages.WHITE
					|| board.charAt(i) != ProtocolMessages.UNOCCUPIED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether a move is valid Calls the MoveChecker class to do so
	 */
	public boolean isValidMove(int move) {
		return false;
	}

	/**
	 * Returns a String representation of this board
	 */
	public String toString() {
		String s = "";
		for (int i = 0; i < intersecs.length; i++) {
			s = s + intersecs[i];
		}
		return s;
	}

	public boolean gameOver() {
		return false;
	}

	/**
	 * Returns true if the intersection i exists on the board and is empty.
	 */
	public boolean isUnoccupied(int i) {
		if (i < 0 || i > this.intersecs.length) {
			return false;
		}
		if (intersecs[i].getMark() == Mark.U) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds a stone to the GUI. Updates the intersection on which the stone is
	 * placed by changing the mark to the stone's colour. Then updates liberties of
	 * the stone and those of its neighbours.
	 * 
	 * @throws ExitProgram
	 */
	public void addStone(int col, int row, Mark mark) {
		int i = coorToInt(col, row);
		g.addStone(col, row, mark.bool());
		intersecs[i].setMark(mark);
		for (Intersec neighbour : intersecs[i].getNeighbours()) {
			if (neighbour.getMark() != Mark.U) {
				intersecs[i].reduceLib();
				neighbour.reduceLib();
			}
		}
	}

	/**
	 * Removes a stone from the GUI Updates the intersection on which the stone is
	 * placed by changing the mark to the stone's colour. Then updates liberties of
	 * the intersection (set to initial value) and those of the neighbouring stones.
	 */
	public void removeStone(int col, int row) {
		int i = coorToInt(col, row);
		g.removeStone(col, row);
		intersecs[i].setMark(Mark.U);
		intersecs[i].setLiberties(4);
		if (col == 0 || col == this.boardSize - 1) {
			intersecs[i].reduceLib();
		}
		if (row == 0 || row == this.boardSize - 1) {
			intersecs[i].reduceLib();
		}
		for (Intersec neighbour : intersecs[i].getNeighbours()) {
			if (neighbour.getMark() != Mark.U) {
				neighbour.increaseLib();
			}
		}
	}

	/**
	 * Put a stone on the board at the provided column and row. Create a chain with
	 * this stone. Add to this chain any neighbouring chains of the same colour.
	 * Remove any stones of the opponent player that have no liberties anymore. TODO
	 * Then remove any stones of the player with this mark that have no liberties.
	 * 
	 * @throws ExitProgram if some player (client) chooses an intersection for his
	 *                     next stone that already has a stone on there
	 */
	public void putStone(int col, int row, Mark mark) throws ExitProgram {
		int i = coorToInt(col, row);
		if (intersecs[i].getMark() != Mark.U) {
			throw new ExitProgram("Cannot place a stone at an intersection where there already is one!");
		}
		addStone(col, row, mark);
		Chain chain = new Chain(intersecs[i], mark);
		for (Intersec neighbour : intersecs[i].getNeighbours()) {
			if (neighbour.getMark() == mark && !neighbour.getChain().equals(chain)) {
				chain.joinChain(neighbour.getChain());
			} else if (neighbour.getMark() == mark.other()) {
				// checkLiberties(neighbour);
			}
		}

		// TODO noLiberties(col, row, mark);
	}

	// /**
	// * Check how many unoccupied neighbouring intersections the stone (chain) has
	// * and update the number of liberties of all stones in the chain
	// *
	// * If
	// */
	// private void updateLiberties(int col, int row, Mark mark) {// (Intersec
	// intersec) {
	// System.out.println("RECURSION: now counting lib of col " + col + " and row "
	// + row + " with a "
	// + coorToIntersec(col, row).getMark() + " stone of " + coorToIntersec(col,
	// row).getLiberties()
	// + " liberties");
	//
	// Intersec intersec = coorToIntersec(col, row);
	//
	// for (Intersec neighbour : intersec.getNeighbours()) {
	// if (neighbour.getMark() == Mark.U) {
	//
	// }
	// }
	//
	// if (coorToIntersec(col, row).getMark() == mark.U) {
	// coorToIntersec(col, row).increaseLib();
	// if (coorToIntersec(col - 1, row) == null || coorToIntersec(col - 1,
	// row).getMark() == mark.other())
	// if (coorToIntersec(col, row).getMark() == mark.other()
	// && coorToIntersec(col, row).getLiberties() == 0) {
	// removeStone(col, row);
	// if (!(col == 0)) {
	// updateLiberties(col - 1, row, mark);
	// }
	// if (!(col == boardSize - 1)) {
	// updateLiberties(col + 1, row, mark);
	// }
	// if (!(row == 0)) {
	// updateLiberties(col, row - 1, mark);
	// }
	// if (!(row == boardSize - 1)) {
	// updateLiberties(col, row + 1, mark);
	// }
	// }
	// }
	// }
	//
	// /**
	// * increases the number of liberties of all neighbouring intersections (that
	// are
	// * on the board) by one
	// */
	// private void increaseLiberties(int col, int row) {
	// if (!(col == 0)) {
	// coorToIntersec(col - 1, row).increaseLib();
	// }
	// if (!(col == boardSize - 1)) {
	// coorToIntersec(col + 1, row).increaseLib();
	// }
	// if (!(row == 0)) {
	// coorToIntersec(col, row - 1).increaseLib();
	// }
	// if (!(row == boardSize - 1)) {
	// coorToIntersec(col, row + 1).increaseLib();
	// }
	// }
	//
	// /**
	// * reduces the number of liberties of all neighbouring intersections (that are
	// * on the board) by one
	// */
	// private void reduceLiberties(int col, int row) {
	// if (!(col == 0)) {
	// coorToIntersec(col - 1, row).reduceLib();
	// }
	// if (!(col == boardSize - 1)) {
	// coorToIntersec(col + 1, row).reduceLib();
	// }
	// if (!(row == 0)) {
	// coorToIntersec(col, row - 1).reduceLib();
	// }
	// if (!(row == boardSize - 1)) {
	// coorToIntersec(col, row + 1).reduceLib();
	// }
	// }
	//
	// /**
	// * Check whether any neighbouring stones (in the same chain) are out of
	// * liberties and remove the stones (the whole chain or none) that have no
	// * liberties anymore
	// */
	// private void noLiberties(int col, int row, Mark mark) {
	// System.out.println("RECURSION: now looking at col " + col + " and row " + row
	// + " with a "
	// + coorToIntersec(col, row).getMark() + " stone of " + coorToIntersec(col,
	// row).getLiberties()
	// + " liberties");
	// if (coorToIntersec(col, row).getMark() == mark.other() && coorToIntersec(col,
	// row).getLiberties() == 0) {
	// removeStone(col, row);
	// if (!(col == 0)) {
	// noLiberties(col - 1, row, mark);
	// }
	// if (!(col == boardSize - 1)) {
	// noLiberties(col + 1, row, mark);
	// }
	// if (!(row == 0)) {
	// noLiberties(col, row - 1, mark);
	// }
	// if (!(row == boardSize - 1)) {
	// noLiberties(col, row + 1, mark);
	// }
	// }
	// }
	//
}
