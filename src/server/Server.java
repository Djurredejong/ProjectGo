package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import exceptions.ExitProgram;
import game.Game;
import protocol.ProtocolMessages;

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
	 * List of Games that are played on this server.
	 */
	private List<Game> games;

	/**
	 * Constructs a new Server. Initialises the clients list, the games list, the
	 * view and the next_client_no.
	 */
	public Server() {
		clients = new ArrayList<>();
		games = new ArrayList<>();
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

					// TODO look in list of clients if there are two connected clients not yet in a
					// game
					if (secondPlayer) {
						// two players have connected, start a new Game!
						Game game = new Game(clients.get(nextClientNo - 2), clients.get(nextClientNo - 1), boardSize);
						games.add(game);

						// give the ClientHandlers a color, first player is always black
						clients.get(nextClientNo - 2).setColor(ProtocolMessages.BLACK);
						clients.get(nextClientNo - 1).setColor(ProtocolMessages.WHITE);

						// let the ClientHandlers know the Game they are playing on!
						clients.get(nextClientNo - 2).setGame(games.get(games.size() - 1));
						clients.get(nextClientNo - 1).setGame(games.get(games.size() - 1));

						// wake up the ClientHandlers that will play in the new game
						// by setting their boolean twoPlayer values to true
						view.showMessage(clients.get(nextClientNo - 2).getName() + " and "
								+ clients.get(nextClientNo - 1).getName() + " will play against each other!");

						clients.get(nextClientNo - 2).setTwoPlayers(true);
						clients.get(nextClientNo - 1).setTwoPlayers(true);

						while (true) {
							// while the game is not over yet
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						// clients.get(nextClientNo - 2).notify();
						// clients.get(nextClientNo - 1).notify();
						// start the game play
						// game.startPlay();
					}
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
				ssock = new ServerSocket(port, 0, InetAddress.getByName("127.0.0.1"));
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
	 * Removes a game from the games list. Gets called in the shutdown() method of
	 * Game.
	 */
	public void removeGame(Game game) {
		this.games.remove(game);
	}

	/**
	 * Called by a ClientHandler when the player it represents wants to do this
	 * move; does the provided move on the board.
	 */
	public synchronized String doMove(ClientHandler handler, int move) {
		// if (handler.getColor() == ProtocolMessages.BLACK) {
		handler.getGame().move(move);
		handler.getGame().setCurrent(handler.getGame().getCurrent() % 2);
		return "move done";
		// }

	}

	/**
	 * Called by a ClientHandler when the player it represents passes.
	 */
	public synchronized String doPass(String cmd) {
		return "doPass Method";
	}

	/**
	 * Getter method for the board size of the games being played on this server.
	 */
	public int getBoardSize() {
		return boardSize;
	}

	/**
	 * Getter method for the list of games.
	 */
	public List<Game> getGames() {
		return games;
	}

	/**
	 * Start a new Server.
	 */
	public static void main(String[] args) {
		Server server = new Server();
		System.out.println("This Server will let you play Go. Starting...");
		new Thread(server).start();
	}

}
