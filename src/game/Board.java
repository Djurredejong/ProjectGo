package game;

import protocol.ProtocolMessages;

public class Board {
	
	private int boardSize;
	
	public Board(int boardSize) {
		this.boardSize = boardSize;
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

	public boolean isValidMove(int turn) {
				return false;
	}
	
	
}
