package game;

import com.nedap.go.gui.GoGUIIntegrator;

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
	 * The intersections of this board Can either be black/white stones or
	 * unoccupied
	 */
	private Intersec[] intersecs;

	/**
	 * Gets the content of intersection i
	 */
	public Mark getIntersecMark(int i) {
		return intersecs[i].getMark();
	}

	/**
	 * The GUI of this board and getter
	 */
	private GoGUIIntegrator g;

	/**
	 * Creates an empty board, gives each intersection its initial number of
	 * liberties and its list of neighbours, starts the GUI
	 */
	public Board(int boardSize) {
		this.boardSize = boardSize;
		intersecs = new Intersec[boardSize * boardSize];
		int i = 0;
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				intersecs[i] = new Intersec();
				intersecs[i] = coorToIntersec(col, row);
				if (col == 0 || col == this.boardSize) {
					intersecs[i].reduceLib();
				}
				if (row == 0 || row == this.boardSize) {
					intersecs[i].reduceLib();
				}
				i++;
			}
		}
		i = 0;
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				if (col != 0) {
					intersecs[i].addNeighbour(coorToIntersec(col - 1, row));
				}
				if (col != boardSize) {
					intersecs[i].addNeighbour(coorToIntersec(col + 1, row));
				}
				if (row != 0) {
					intersecs[i].addNeighbour(coorToIntersec(col, row - 1));
				}
				if (row != boardSize) {
					intersecs[i].addNeighbour(coorToIntersec(col, row + 1));
				}
				i++;
			}
		}

		// for (int i = 0; i < (boardSize * boardSize); i++) {
		// intersecs[i] = new Intersec();
		// if (i % boardSize == 0 || (i - 1) % boardSize == 0) {
		// intersecs[i].reduceLib();
		// }
		// if (i < boardSize || (boardSize * boardSize - i) < boardSize) {
		// intersecs[i].reduceLib();
		// }
		// }
		g = new GoGUIIntegrator(false, false, this.boardSize);
		g.startGUI();

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
	 * Given a column and a row, gives the corresponding intersection Return null
	 * when the column/row combination is not on the board
	 */
	public Intersec coorToIntersec(int col, int row) {
		if (col < 0 || col > this.boardSize || row < 0 || row > this.boardSize) {
			return null;
		} else {
			return intersecs[row * this.boardSize + col];
		}
	}

	/**
	 * Put a stone on the board at the provided column and row Remove any stones of
	 * the opponent player that have no liberties Remove any stones of player with
	 * Mark mark that have no liberties
	 */
	public void putStone(int col, int row, Mark mark) {
		addStone(col, row, mark);
		if (!(col == 0)) {
			noLiberties(col - 1, row, mark.other());
		}
		if (!(col == boardSize - 1)) {
			noLiberties(col + 1, row, mark.other());
		}
		if (!(row == 0)) {
			noLiberties(col, row - 1, mark.other());
		}
		if (!(row == boardSize - 1)) {
			noLiberties(col, row + 1, mark.other());
		}

		// TODO noLiberties(col, row, mark);
	}

	/**
	 * Adds a stone to the GUI Updates the intersection on which the stone is placed
	 * - change the mark to the stone's colour - update liberties of it and the
	 * chain it belongs to - update the liberties of its neighbouring chains of the
	 * opposite colour TODO not looking at chains of opposite colour multiple times
	 */
	private void addStone(int col, int row, Mark mark) {
		g.addStone(col, row, mark.bool());
		Intersec intersec = coorToIntersec(col, row);
		intersec.setMark(mark);
		updateLiberties(col, row, mark);
		if (!(col == 0) && coorToIntersec(col - 1, row).getMark() == mark.other()) {
			updateLiberties(col - 1, row, mark);
		}
		if (!(col == boardSize - 1) && coorToIntersec(col + 1, row).getMark() == mark.other()) {
			updateLiberties(col + 1, row, mark);
		}
		if (!(row == 0) && coorToIntersec(col, row - 1).getMark() == mark.other()) {
			updateLiberties(col, row - 1, mark);
		}
		if (!(row == boardSize - 1) && coorToIntersec(col, row + 1).getMark() == mark.other()) {
			updateLiberties(col, row + 1, mark);
		}
	}

	/**
	 * Removes a stone from the GUI Also updates the intersections array The
	 * liberties
	 */
	private void removeStone(int col, int row) {
		g.removeStone(col, row);
		intersecs[row * this.boardSize + col].setMark(Mark.U);
		increaseLiberties(col, row);
	}

	/**
	 * Check how many unoccupied neighbouring intersections the stone (chain) has
	 * and update the number of liberties of all stones in the chain
	 * 
	 * If
	 */
	private void updateLiberties(int col, int row, Mark mark) {// (Intersec intersec) {
		System.out.println("RECURSION: now counting lib of col " + col + " and row " + row + " with a "
				+ coorToIntersec(col, row).getMark() + " stone of " + coorToIntersec(col, row).getLiberties()
				+ " liberties");

		Intersec intersec = coorToIntersec(col, row);
		
		L
		
		for (Intersec neighbour : intersec.getNeighbours()) {
			if (neighbour.getMark() == Mark.U) {
				
			}
		}
		
		if (coorToIntersec(col, row).getMark() == mark.U) {
			coorToIntersec(col, row).increaseLib();
			if (coorToIntersec(col - 1, row) == null || coorToIntersec(col - 1, row).getMark() == mark.other())
				if (coorToIntersec(col, row).getMark() == mark.other()
						&& coorToIntersec(col, row).getLiberties() == 0) {
					removeStone(col, row);
					if (!(col == 0)) {
						updateLiberties(col - 1, row, mark);
					}
					if (!(col == boardSize - 1)) {
						updateLiberties(col + 1, row, mark);
					}
					if (!(row == 0)) {
						updateLiberties(col, row - 1, mark);
					}
					if (!(row == boardSize - 1)) {
						updateLiberties(col, row + 1, mark);
					}
				}
		}
	}

	/**
	 * increases the number of liberties of all neighbouring intersections (that are
	 * on the board) by one
	 */
	private void increaseLiberties(int col, int row) {
		if (!(col == 0)) {
			coorToIntersec(col - 1, row).increaseLib();
		}
		if (!(col == boardSize - 1)) {
			coorToIntersec(col + 1, row).increaseLib();
		}
		if (!(row == 0)) {
			coorToIntersec(col, row - 1).increaseLib();
		}
		if (!(row == boardSize - 1)) {
			coorToIntersec(col, row + 1).increaseLib();
		}
	}

	/**
	 * reduces the number of liberties of all neighbouring intersections (that are
	 * on the board) by one
	 */
	private void reduceLiberties(int col, int row) {
		if (!(col == 0)) {
			coorToIntersec(col - 1, row).reduceLib();
		}
		if (!(col == boardSize - 1)) {
			coorToIntersec(col + 1, row).reduceLib();
		}
		if (!(row == 0)) {
			coorToIntersec(col, row - 1).reduceLib();
		}
		if (!(row == boardSize - 1)) {
			coorToIntersec(col, row + 1).reduceLib();
		}
	}

	/**
	 * Check whether any neighbouring stones (in the same chain) are out of
	 * liberties and remove the stones (the whole chain or none) that have no
	 * liberties anymore
	 */
	private void noLiberties(int col, int row, Mark mark) {
		System.out.println("RECURSION: now looking at col " + col + " and row " + row + " with a "
				+ coorToIntersec(col, row).getMark() + " stone of " + coorToIntersec(col, row).getLiberties()
				+ " liberties");
		if (coorToIntersec(col, row).getMark() == mark.other() && coorToIntersec(col, row).getLiberties() == 0) {
			removeStone(col, row);
			if (!(col == 0)) {
				noLiberties(col - 1, row, mark);
			}
			if (!(col == boardSize - 1)) {
				noLiberties(col + 1, row, mark);
			}
			if (!(row == 0)) {
				noLiberties(col, row - 1, mark);
			}
			if (!(row == boardSize - 1)) {
				noLiberties(col, row + 1, mark);
			}
		}
	}

}
