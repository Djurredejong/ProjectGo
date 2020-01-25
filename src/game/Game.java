package game;

import com.nedap.go.gui.GoGUIIntegrator;

//import server.ClientHandler;

public class Game {
	
	//The GUI of this b and getter
	private GoGUIIntegrator g;
	public GoGUIIntegrator getGui() {
		return g;
	}

	/** The 2 players of the game (represented by ClientHandlers)b*/
	private Player[] players;

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
	public Game(Player player1, Player player2, int boardSize) {
		this.boardSize = boardSize;
		this.board = new Board(boardSize);
		players = new Player[2];
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
		GoGUIIntegrator g = new GoGUIIntegrator(true, true, 10);
		g.startGUI();
		g.setBoardSize(3);
		//g.setBoardSize(this.boardSize);
		while (!board.gameOver()) {    		
			players[current].makeMove(this);
			current++;
			current = current % 2;
		}
	}

}
