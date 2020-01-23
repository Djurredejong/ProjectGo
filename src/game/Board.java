package game;

import protocol.ProtocolMessages;

public class Board {
	
	private int boardSize;
	
	private String board;
	
	/**
	 * Initialses an empty board representation: a String
	 * of length (boardSize * boardSize - 1) with each char
	 * being ProtocolMessages.UNOCCUPIED
	 * 
	 */
	public Board(int boardSize) {
		this.boardSize = boardSize;
		board = "";
		for (int i = 0; i < (boardSize * boardSize); i++) {
				board += ProtocolMessages.UNOCCUPIED; {
			}
		}
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
			if (board.charAt(i) != ProtocolMessages.BLACK ||
					board.charAt(i) != ProtocolMessages.WHITE ||
					board.charAt(i) != ProtocolMessages.UNOCCUPIED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether a move is valid
	 * Calls the MoveChecker class to do so
	 */
	public boolean isValidMove(int move) {
		return false;
	}
	
	/**
	 * returns the String representation of the board
	 */
	public String toString() {
		return board;
	}

	
	public boolean gameOver() {	
		return false;
	}
}
