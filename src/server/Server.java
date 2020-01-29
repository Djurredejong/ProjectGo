package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import exceptions.ExitProgram;
import game.Board;
import game.Mark;

public class Server implements Runnable {

	/**
	 * The ServerSocket of this Server.
	 */
	private ServerSocket ssock;

	/**
	 * List of ClientHandlers, one for each connected client.
	 */
	private List<ClientHandler> clients;

	/**
	 * Next client number, increasing for every new connection.
	 */
	private int nextClientNo;

	/**
	 * The view (TUI) of this HotelServer.
	 */
	public ServerTUI view;

	/**
	 * The size of the board (number of horizontal/vertical intersections)of the
	 * games being played on this server.
	 */
	private int boardSize;

	/**
	 * The board of the game that is being played on this server. TODO change to
	 * List of Boards, such that multiple games can be played on this server.
	 */
	private Board board;

	/**
	 * Constructs a new Server. Initialises the clients list, the games list, the
	 * view and the next_client_no.
	 */
	public Server() {
		clients = new ArrayList<>();
		view = new ServerTUI();
		nextClientNo = 0;
	}

	/**
	 * Opens a new socket by calling setup() and starts a new ClientHandler for
	 * every connecting client.
	 * 
	 * If setup() throws a ExitProgram exception, stop the program. In case of any
	 * other errors, ask the user whether the setup should be ran again to open a
	 * new socket.
	 */
	public void run() {
		boolean openNewSocket = true;
		while (openNewSocket) {
			try {
				setup();
				boolean secondPlayer = true;
				while (true) {
					secondPlayer = !secondPlayer;
					nextClientNo++;
					Socket sock = ssock.accept();
					String clientName = "Player " + String.format("%02d", nextClientNo);
					view.showMessage("New player [" + clientName + "] connected!");
					ClientHandler handler = new ClientHandler(sock, this, clientName);
					new Thread(handler).start();
					clients.add(handler);

					// TODO check clients list for two connected clients that are not yet in a game
					if (secondPlayer) {
						view.showMessage(clients.get(nextClientNo - 2).getName() + " and "
								+ clients.get(nextClientNo - 1).getName() + " will play against each other!");
						// two players have connected, start a new Game!
						this.board = new Board(this.boardSize, false);
						// give the ClientHandlers a colour, first player is always black
						clients.get(nextClientNo - 2).setMark(Mark.B);
						clients.get(nextClientNo - 1).setMark(Mark.W);
						// the first move should be black's, let the ClientHandler's know this
						// only one needs to be told this, since it's a static variable
						clients.get(nextClientNo - 2).setWhiteTurn(false);
						clients.get(nextClientNo - 2).setLastMove(-1);
						// wake up the ClientHandlers that will play in the new game
						// by setting the volatile boolean twoPlayer value to true
						clients.get(nextClientNo - 2).setTwoPlayers(true);
						clients.get(nextClientNo - 1).setTwoPlayers(true);
					}
					// game.startPlay();
				}

			} catch (ExitProgram ep) {
				// If setup() throws an ExitProgram exception, stop the program.
				openNewSocket = false;
			} catch (IOException e) {
				System.out.println("IO error in server: " + e.getMessage());
				if (!view.getBoolean("Do you want to open a new socket? (y/n)")) {
					openNewSocket = false;
				}
			}
		}
		view.showMessage("See you later!");

	}

	/**
	 * Opens a new ServerSocket at local host [on a user-defined port].
	 * 
	 * The user is asked to input a board size and then a port, after which a socket
	 * is attempted to be opened. If the attempt succeeds, the method ends, If the
	 * attempt fails, the user decides whether to try again, after which an
	 * ExitProgram exception is thrown or a new port is entered.
	 */
	public void setup() throws ExitProgram {
		// boardSize = view.getInt("What should be the board size for the games played
		// on this Server?");
		boardSize = 4;
		view.showMessage("The board size is: " + boardSize + " x " + boardSize + ", let's see who wants to play!");

		ssock = null;
		while (ssock == null) {
			// int port = view.getInt("Please enter the server port.");
			int port = 8888;

			try {
				view.showMessage("Attempting to open a socket at 127.0.0.1 " + "on port " + port + "...");
				ssock = new ServerSocket(port);// , 0, InetAddress.getByName("127.0.0.1"));
				view.showMessage("Server started at port " + port + ".");
			} catch (IOException e) {
				view.showMessage("ERROR: could not create a socket on 127.0.0.1 and port " + port + ".");
				if (!view.getBoolean("Do you want to try again?")) {
					throw new ExitProgram("Ok, in that case the program will be exited.");
				}
			}
		}
	}

	/**
	 * Removes a clientHandler from the client list. Gets called in the shutdown()
	 * method of ClientHandler
	 */
	public void removeClient(ClientHandler client) {
		this.clients.remove(client);
	}

	/**
	 * Called by a ClientHandler when the player it represents wants to do this
	 * move; does the provided move on the board.
	 * 
	 * @throws ExitProgram
	 */
	public synchronized void doMove(Mark mark, int move) throws ExitProgram {
		board.putStone(move % this.boardSize, move / this.boardSize, mark);
	}

	/**
	 * Called by a ClientHandler when the player it represents passes.
	 */
	public synchronized String doPass(String cmd) {
		return "doPass Method";
	}

	/**
	 * Getter method for the board of the game being played on this server.
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Getter method for the board size of the games being played on this server.
	 */
	public int getBoardSize() {
		return boardSize;
	}

	/**
	 * Start a new Server.
	 */
	public static void main(String[] args) {
		Server server = new Server();
		System.out.println("This Server will let you people Go. Starting...");
		new Thread(server).start();
	}

}
