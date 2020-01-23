package game;

import server.ClientHandler;

public class Game {

    /** The 2 players of the game (represented by ClientHandlers)b*/
    private ClientHandler[] players;
	
    /** The size of the board */
	private int boardSize;
	public int getBoardSize() {
		return boardSize;
	}
	
	/** The current player (always 0 or 1) and getter+setter */
	private int current;
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	
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
        setCurrent(0);
	}

	public void move(int move) {
		System.out.println("this is my move");
	}
	
	/** 
	 * As long as the game has not ended,
	 * players make a move one after the other
	 */
	public void startPlay() {
    	while (!board.gameOver()) {    		
    		//players[current].makeMove(board);
    		current++;
    		current = current % 2;
    	}
	}
	
}
