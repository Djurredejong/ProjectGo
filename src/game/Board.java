package game;

import java.util.HashSet;
import java.util.Set;

import com.nedap.go.gui.GoGUIIntegrator;

import exceptions.ExitProgram;
import protocol.ProtocolMessages;

public class Board {

	/**
	 * The size of the board.
	 */
	private int boardSize;

	/**
	 * The intersections of this board. Can either be black/white stones or
	 * unoccupied (set to public for testing).
	 */
	public Intersec[] intersecs;

	/**
	 * The GUI of this board.
	 */
	private GoGUIIntegrator g;

	/**
	 * Indicates whether a first stone has been placed on the board.
	 */
	private boolean firstStone;

	/**
	 * Keeps track of all previous board situations (used for Ko-rule).
	 */
	private Set<String> boardSituations;

	/**
	 * Creates an empty board, gives each intersection its initial number of
	 * liberties and its list of neighbours, starts the GUI. If gui is set to false,
	 * the GUI will not be started (useful for testing). Finally initialises the
	 * board situations as an empty set.
	 */
	public Board(int boardSize, boolean gui) {
		this.firstStone = false;
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
		this.boardSituations = new HashSet<>();
	}

	/**
	 * Getter method for the size of this board.
	 */
	public int getBoardSize() {
		return boardSize;
	}

	/**
	 * Given a column and a row, gives the corresponding intersection.
	 */
	public int coorToInt(int col, int row) {
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
	 */
	public void addStone(int col, int row, Mark mark) {
		int i = coorToInt(col, row);
		g.addStone(col, row, mark.bool());
		intersecs[i].setMark(mark);
		for (Intersec neighbour : intersecs[i].getNeighbours()) {
			if (neighbour.getMark() != Mark.U) {
				intersecs[i].removeLiberty(neighbour);
				neighbour.removeLiberty(intersecs[i]);
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
		intersecs[i].setMark(Mark.U);
		for (Intersec neighbour : intersecs[i].getNeighbours()) {
			neighbour.addLiberty(intersecs[i]);
		}
	}

	/**
	 * Removes a chain from the board.
	 */
	public void removeChain(Chain chain) {
		for (Intersec stone : chain.getStones()) {
			removeStone(stone.getCol(), stone.getRow());
		}
	}

	/**
	 * Put a stone on the board at the provided column and row. Create a chain with
	 * this stone. Add to this chain any neighbouring chains of the same colour.
	 * Remove any stones of the opponent player that have no liberties anymore.
	 * Remove any stones of the player who put the stone that have no liberties
	 * anymore. Finally updates the board situations with this new board situation
	 * (for enforcing the Ko-rule).
	 * 
	 * @throws ExitProgram if some player (client) chooses an intersection for his
	 *                     next stone that already has a stone on there
	 */
	public void putStone(int col, int row, Mark mark) throws ExitProgram {
		this.firstStone = false;
		int i = coorToInt(col, row);
		if (!isUnoccupied(i)) {
			throw new ExitProgram("Cannot place a stone at an intersection where there already is one!");
		}
		addStone(col, row, mark);
		Chain chain = new Chain(intersecs[i], mark);
		for (Intersec neighbour : intersecs[i].getNeighbours()) {
			if (neighbour.getMark() == mark && !neighbour.getChain().equals(chain)) {
				chain.joinChain(neighbour.getChain());
			}
		}
		for (Intersec neighbour : intersecs[i].getNeighbours()) {
			if (neighbour.getMark() == mark.other() && neighbour.getChain().chainLib() == 0) {
				removeChain(neighbour.getChain());
			}
		}
		if (intersecs[i].getChain().chainLib() == 0) {
			removeChain(intersecs[i].getChain());
		}
		addBoardSituation(this.toString());
	}

	/**
	 * After the game has ended (either board full or two consecutive passes in a
	 * row), the score will be counted by calling this method.
	 */
	public void countScore() {

	}

	/**
	 * Checks whether the board is full (and hence the game is over).
	 */
	public boolean gameOver() {
		int i = 0;
		if (!firstStone) {
			return false;
		}
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
	 * Stores all the board situations to check whether no previous board situation
	 * is recreated (Ko rule). This method should not be called when a player
	 * passes!!!
	 * 
	 * @throws ExitProgram when the Ko rule has been violated
	 */
	public void addBoardSituation(String boardSituation) throws ExitProgram {
		if (boardSituations.contains(boardSituation)) {
			throw new ExitProgram("The Ko rule has been violated!");
		} else {
			this.boardSituations.add(boardSituation);
		}
	}

	// boolean for easier testing
	public boolean violation = false;

	// ------------------- Methods for communicating -----------------------------//

	/**
	 * Checks whether a String represents a possible board situation.
	 */
	public boolean checkValidBoard(String board) {
		if (board.length() != boardSize * boardSize) {
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
	 * Turns a Board into a String. Can be used to communicate the contents of a
	 * board situation. Also used for determining whether the Ko rule is breached.
	 */
	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < intersecs.length; i++) {
			s = s + intersecs[i];
		}
		return s;
	}

}
