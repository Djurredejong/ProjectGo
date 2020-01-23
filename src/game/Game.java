package game;

import server.ClientHandler;

public class Game {

    /** The 2 players of the game (represented by ClientHandlers)b*/
    private ClientHandler[] players;
	
    /** The size of the board */
	private int boardSize;
	
	/** The current player (always 0 or 1)*/
	private int current;
	
	/** The Board this game is played on and getter method*/
	private Board board;
	public Board getBoard() {
		return board;
	}
	
    /**
     * Creates a new Game object, initialises players, board size and current
     * Parameters all received from Server, which initialises the games
     * 
     * @param player1 The first player
     * @param player2 The second player
     * @param boardSize The size of the board
     */
	public Game(ClientHandler player1, ClientHandler player2, int boardSize) {
		this.boardSize = boardSize;
		this.board = new Board(boardSize);
		players = new ClientHandler[2];
        players[0] = player1;
        players[1] = player2;
        current = 0;
	}


}
