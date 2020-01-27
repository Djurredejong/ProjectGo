package game;

import com.nedap.go.gui.GoGUIIntegrator;

import exceptions.ExitProgram;
import protocol.ProtocolMessages;

public class Board {

	/**
	 * The size of the board
	 */
	private int boardSize;

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
				intersecs[i] = new Intersec(col, row);
				i++;
			}
		}
		i = 0;
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				if (col != 0) {
					intersecs[i].addNeighbour(intersecs[coorToInt(col - 1, row)]);
					intersecs[i].addLiberty(intersecs[coorToInt(col - 1, row)]);
				}
				if (col != boardSize - 1) {
					intersecs[i].addNeighbour(intersecs[coorToInt(col + 1, row)]);
					intersecs[i].addLiberty(intersecs[coorToInt(col + 1, row)]);
				}
				if (row != 0) {
					intersecs[i].addNeighbour(intersecs[coorToInt(col, row - 1)]);
					intersecs[i].addLiberty(intersecs[coorToInt(col, row - 1)]);
				}
				if (row != boardSize - 1) {
					intersecs[i].addNeighbour(intersecs[coorToInt(col, row + 1)]);
					intersecs[i].addLiberty(intersecs[coorToInt(col, row + 1)]);
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
	 * Getter method for the size of this board.
	 */
	public int getBoardSize() {
		return boardSize;
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
	 * Return the column of an intersection when its index in the intersections
	 * array is given.
	 */
	public int getCol(int i) {
		return i % this.boardSize;
	}

	/**
	 * Return the row of an intersection when its index in the intersections array
	 * is given.
	 */
	public int getRow(int i) {
		return i / this.boardSize;
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

	/**
	 * Checks whether the board is full (and hence the game is over)
	 */
	public boolean gameOver() {
		int i = 0;
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				if (intersecs[i].getMark() != Mark.U) {
					return false;
				}
				i++;
			}
		}
		return true;
	}

	/**
	 * Returns true if the intersection i is empty.
	 * 
	 * @throws ExitProgram if the given intersection does not exist on the board.
	 */
	public boolean isUnoccupied(int i) throws ExitProgram {
		if (i < 0 || i > this.intersecs.length) {
			throw new ExitProgram("You have provided an intersection that is not on the board!");
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
//		for (int j = 0; j < intersecs[i].getNeighbours().size(); j++) {
//			if (intersecs[i].getNeighbours().get(j).getMark() != Mark.U) {
//				intersecs[i].removeLiberty(intersecs[i].getNeighbours().get(j));
//				intersecs[i].getNeighbours().get(j).removeLiberty(intersecs[i]);
//			} else {
//				intersecs[i].getNeighbours().get(j).removeLiberty(intersecs[i]);
//			}
//		}
		for (Intersec neighbour : intersecs[i].getNeighbours()) {
			if (neighbour.getMark() != Mark.U) {
				intersecs[i].removeLiberty(neighbour);
				System.out.println();
				System.out.println("REMOVING LIBERTY: " + intersecs[i] + " from " + neighbour);
				System.out.println("liberties before: " + neighbour.getLiberties());
				neighbour.removeLiberty(intersecs[i]);
				System.out.println("liberties after: " + neighbour.getLiberties());
				System.out.println();
			} else {
				neighbour.removeLiberty(intersecs[i]);
			}
		}
	}

	/**
	 * Removes a stone from the GUI. Updates the intersection on which the stone is
	 * placed by changing the mark to the stone's colour. Then updates liberties of
	 * the neighbouring stones.
	 */
	public void removeStone(int col, int row) {
		int i = coorToInt(col, row);
		g.removeStone(col, row);
		Mark otherMark = intersecs[i].getMark().other();
		intersecs[i].setMark(Mark.U);
		System.out.println("removed stone " + i);
		// for (int j = 0; j < intersecs[i].getNeighbours().size(); j++) {
		// if (intersecs[i].getNeighbours().get(j).getMark() == otherMark) {
		// intersecs[i].getNeighbours().get(j).addLiberty(intersecs[i]);
		// }
		// }
		System.out.println("other mark is " + otherMark);
		for (Intersec neighbour : intersecs[i].getNeighbours()) {
			System.out.println("mark of neighbour is " + neighbour.getMark());
			// if (neighbour.getMark() == otherMark) {
			neighbour.addLiberty(intersecs[i]);
			// }
		}
	}

	/**
	 * Removes a chain from the board.
	 */
	public void removeChain(Chain chain) {
		// for (int j = 0; j < chain.getStones().size(); j++) {
		// removeStone(chain.getStones().get(j).getCol(),
		// chain.getStones().get(j).getRow());
		// }
		for (Intersec stone : chain.getStones()) {
			removeStone(stone.getCol(), stone.getRow());
		}
	}

	/**
	 * Put a stone on the board at the provided column and row. Create a chain with
	 * this stone. Add to this chain any neighbouring chains of the same colour.
	 * Remove any stones of the opponent player that have no liberties anymore.
	 * 
	 * @throws ExitProgram if some player (client) chooses an intersection for his
	 *                     next stone that already has a stone on there
	 */
	public void putStone(int col, int row, Mark mark) throws ExitProgram {
		int i = coorToInt(col, row);
		if (!isUnoccupied(i)) {
			throw new ExitProgram("Cannot place a stone at an intersection where there already is one!");
		}
		addStone(col, row, mark);
		Chain chain = new Chain(intersecs[i], mark);

		System.out.println("placed stone " + i + " on the board, color " + intersecs[i].getMark());
		for (int k = 0; k < 9; k++) {
			System.out.println("#liberties of stone " + k + " is " + intersecs[k].getLiberties().size());
		}

//		for (int j = 0; j < intersecs[i].getNeighbours().size(); j++) {
//			if (intersecs[i].getNeighbours().get(j).getMark() == mark
//					&& !intersecs[i].getNeighbours().get(j).getChain().equals(chain)) {
//				chain.joinChain(intersecs[i].getNeighbours().get(j).getChain());
//			} else if (intersecs[i].getNeighbours().get(j).getMark() == mark.other()) {
//				System.out.println(
//						"looking at neighbours of stone " + i + " whose neighbouring stone is in a chain with length "
//								+ intersecs[i].getNeighbours().get(j).getChain().getStones().size()
//								+ " and with #liberties " + intersecs[i].getNeighbours().get(j).getChain().chainLib());
//				if (intersecs[i].getNeighbours().get(j).getChain().chainLib() == 0) {
//					System.out.println("removing chain!");
//					removeChain(intersecs[i].getNeighbours().get(j).getChain());
//				}
//			}
//		}
//		System.out.println("placed stone " + i + " on the board, color " + intersecs[i].getMark());
//		for (int k = 0; k < 9; k++) {
//			System.out.println("#liberties of stone " + k + " is " + intersecs[k].getLiberties().size());
//		}

		for (Intersec neighbour : intersecs[i].getNeighbours()) {
			if (neighbour.getMark() == mark && !neighbour.getChain().equals(chain)) {
				System.out.println(neighbour.getChain() + " joined with " + chain);
				chain.joinChain(neighbour.getChain());
			} else if (neighbour.getMark() == mark.other()) {
				System.out.println(
						"looking at neighbours of stone " + i + " whose neighbouring stone is in a chain with length "
								+ neighbour.getChain().getStones().size() + " and with #liberties "
								+ neighbour.getChain().chainLib());
			}
			if (neighbour.getChain() != null && neighbour.getChain().chainLib() == 0) {
				removeChain(neighbour.getChain());
			}
		}
	}

	public void countScore() {
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
