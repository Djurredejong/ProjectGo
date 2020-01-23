package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import exceptions.*;
import protocol.*;
import game.Game;

public class Server implements Runnable {

	/** The ServerSocket of this Server */
	private ServerSocket ssock;

	/** List of ClientHandlers, one for each connected client */
	private List<ClientHandler> clients;

	/** Next client number, increasing for every new connection */
	private int nextClientNo;

	/** The view of this HotelServer */
	public ServerTUI view;

	/** The size of the board and getter method for it */
	private int boardSize;
	public int getBoardSize() {
		return boardSize;
	}

	/** List of Games and getter method for it*/
	private List<Game> games;
	public List<Game> getGames() {
		return games;
	}

	/**
	 * Constructs a new Server. Initialises the clients list, 
	 * the games list, the view and the next_client_no.
	 */
	public Server() {
		clients = new ArrayList<>();
		games = new ArrayList<>();
		view = new ServerTUI();
		nextClientNo = 1;
	}

	/**
	 * Opens a new socket by calling setup() and starts a new
	 * ClientHandler for every connecting client.
	 * 
	 * If setup() throws a ExitProgram exception, stop the program. 
	 * In case of any other errors, ask the user whether the setup should be 
	 * ran again to open a new socket.
	 */
	public void run() {
		boolean openNewSocket = true;
		while (openNewSocket) {
			try {
				setup();
				boolean secondPlayer = true;
				while (true) {
					secondPlayer = !secondPlayer;
					Socket sock = ssock.accept();
					String clientName = "Player " + String.format("%02d", nextClientNo++);
					view.showMessage("New player [" + clientName + "] connected!");
					ClientHandler handler = new ClientHandler(sock, this, clientName);
					new Thread(handler).start();
					clients.add(handler);

					//TODO look in list of clients if there are two connected clients not yet in a game
					if (secondPlayer) {
						//two players have connected, start a new Game!
						Game game = new Game(clients.get(nextClientNo - 3), clients.get(nextClientNo - 2), boardSize);
						games.add(game);
						//give the ClientHandlers a color, first player is always black
						clients.get(nextClientNo - 3).setColor(ProtocolMessages.BLACK);
						clients.get(nextClientNo - 2).setColor(ProtocolMessages.WHITE);
						//let the ClientHandlers know the Game they are playing on!
						clients.get(nextClientNo - 3).setGame(games.get(games.size()-1));
						clients.get(nextClientNo - 2).setGame(games.get(games.size()-1));
						//wake up the ClientHandlers that will play in the new game
						//by setting their boolean twoPlayer values to true
						clients.get(nextClientNo - 3).setTwoPlayers(true);
						clients.get(nextClientNo - 2).setTwoPlayers(true);
						//send to both Clients that the game starts, according to the protocol
						clients.get(nextClientNo - 3).sendStart();
						clients.get(nextClientNo - 2).sendStart();
						//start the game play
						game.startPlay();
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
	 * The user is asked to input a port, after which a socket is attempted 
	 * to be opened. If the attempt succeeds, the method ends, If the 
	 * attempt fails, the user decides to try again, after which an 
	 * ExitProgram exception is thrown or a new port is entered.
	 */
	public void setup() throws ExitProgram {			
		//determine the board size for the games to be played on this Server
		
		//boardSize = view.getInt("What should be the board size for the games played on this Server?");
		boardSize = 5;
		view.showMessage("The board size is: " + boardSize 
				+ " x " + boardSize + ", let's see who wants to play!");

//		view.showMessage("The board size is: " + boardSize 
//				+ " x " + boardSize + ", let's see who wants to play!");
		
		ssock = null;
		while (ssock == null) {
			//int port = view.getInt("Please enter the server port.");
			int port = 8888;

			// try to open a new ServerSocket [at local host]
			try {
				view.showMessage("Attempting to open a socket at 127.0.0.1 " + "on port " + port + "...");
				ssock = new ServerSocket(port, 0, InetAddress.getByName("127.0.0.1"));
				view.showMessage("Server started at port " + port);
			} catch (IOException e) {
				view.showMessage("ERROR: could not create a socket on 127.0.0.1 and port " + port + ".");
				if (!view.getBoolean("Do you want to try again?")) {
					throw new ExitProgram("User indicated to exit the program.");
				}
			}
		}
	}

	/**
	 * Removes a clientHandler from the client list.
	 * Gets called in the shutdown() method of ClientHandler
	 */
	public void removeClient(ClientHandler client) {
		this.clients.remove(client);
	}

	/**
	 * Removes a game from the games list.
	 * Gets called in the shutdown() method of Game
	 */
	public void removeGame(Game game) {
		this.games.remove(game);
	}


	/**
	 * Provides the protocol-defined handshake the Server should give
	 * 
	 * @return server handshake
	 */
	public String getHello() {
		return ProtocolMessages.HANDSHAKE + ProtocolMessages.DELIMITER + "version" + ProtocolMessages.DELIMITER;
	}
	
	/**
	 * Called by a ClientHandler, does the provided move on the board 
	 * 
	 * @param move The move of the player,
	 * 		it is already checked in the ClientHandler
	 * 		that this is a valid move
	 */
	public synchronized String doMove(ClientHandler handler, int move) {
		//if (handler.getColor() == ProtocolMessages.BLACK) {
			handler.getGame().move(move);
			handler.getGame().setCurrent(handler.getGame().getCurrent() % 2);
			return "move done";
 		//}
	
	
	}
	
	public synchronized String doPass(String cmd) {
		return "doPss Method";
	}


	/** 
	 * Start a new Server 
	 */
	public static void main(String[] args) {
		Server server = new Server();
		System.out.println("This Server will let you play Go. Starting...");
		new Thread(server).start();
	}

}

