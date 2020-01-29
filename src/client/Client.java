package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
//import java.util.Scanner;

import exceptions.ExitProgram;
import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;
import game.Board;
import game.HumanPlayer;
import game.Mark;
import game.Player;
import protocol.ProtocolMessages;

/**
 * Client for the Server that lets clients play the game go.
 */
public class Client {

	/**
	 * The socket and in- and outputStreams.
	 */
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;

	/**
	 * The view (TUI) of this client.
	 */
	private ClientTUI view;

	/**
	 * The name of this client.
	 */
	private String myName;

	/**
	 * The player that this Client is.
	 */
	private Player player;

	/**
	 * The mark (colour) of this player, either B or W.
	 */
	private Mark mark;

	/**
	 * The size of the board that is being played on.
	 */
	private int boardSize;

	/**
	 * The board this game is played in.
	 */
	private Board board;

	/**
	 * Constructs a new Client. Initialises the view. Starts a new ComputerPlayer if
	 * the user chooses to do so, a HumanPlayer otherwise.
	 */
	public Client() {
		view = new ClientTUI();
		this.player = new HumanPlayer(myName, mark);
//		try {
//			if (view.getBoolean("Do you want to start an AI player? (y/n)")) {
//				this.player = new ComputerPlayer(myName, mark);
//			} else {
//				this.player = new HumanPlayer(myName, mark);
//			}
//		} catch (ExitProgram e) {
//			view.showMessage(e + " I will now disconnect.");
//			closeConnection();
//		}
	}

	/**
	 * Starts a new Client by creating a connection, followed by the Handshake as
	 * defined in the protocol. The client waits for the start signal of the Server,
	 * to know the game has started. Then the client continuously reads a message
	 * from the Server (which should include a move of the opponent, apart of course
	 * for all the way at the beginning of the game) and determines a move as
	 * response, which is then send back (and confirmed by another message, which is
	 * read here as well) to the Server.
	 */
	public void start() {
		try {
			createConnection();
			System.out.println("Connection to the server created!");
			this.handleHello();
			this.waitForStart();
			this.board = new Board(this.boardSize, true);
			while (true) {
				this.handleGameplay();
				in.readLine();
				// TODO process the response from the server on my move
			}
		} catch (ExitProgram | ServerUnavailableException | ProtocolException | IOException e) {
			view.showMessage(e + " I will now disconnect.");
			closeConnection();
		}
	}

	/**
	 * reads a message from the Server (which should include a move of the opponent,
	 * apart of course for all the way at the beginning of the game) and determines
	 * a move as response, which is then send back to the Server.
	 */
	private void handleGameplay() throws IOException, ProtocolException, ExitProgram, ServerUnavailableException {
		String line = in.readLine();
		String[] lineSplit = line.split(ProtocolMessages.DELIMITER);
		if (lineSplit[0] == null || !(lineSplit[0].contentEquals(String.valueOf(ProtocolMessages.TURN))
				|| lineSplit[0].contentEquals(String.valueOf(ProtocolMessages.END)))) {
			throw new ProtocolException("Error: server did not adhere to the protocol");
		} else {
			if (lineSplit[0].contentEquals(String.valueOf(ProtocolMessages.TURN))) {
				if (lineSplit[2] == null || lineSplit[2].contentEquals(String.valueOf(ProtocolMessages.PASS))) {
					// do nothing to the board
				} else {
					int intersec = Integer.parseInt(lineSplit[2]);
					board.putStone(board.getCol(intersec), board.getRow(intersec), mark.other());
				}
				boolean move = this.player.makeMove(board);
				// TODO give a user the ability to press Q to quit the game
				if (move) {
					this.sendMessage(String.valueOf(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + move));
				} else {
					this.sendMessage(
							String.valueOf(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + ProtocolMessages.PASS));
				}
			} else {
				if (lineSplit[0].contentEquals(String.valueOf(ProtocolMessages.END))) {
					// TODO show why game has ended
				}
			}
		}
	}

	/**
	 * Creates a connection to the server. Requests a name, the IP of the server and
	 * the port to connect to. TODO Keeps requesting this until a connection is
	 * established or until the user indicates to exit the program.
	 */
	public void createConnection() throws ExitProgram {
		sock = null;
		in = null;
		out = null;
		while (sock == null) {
			// myName = view.getString("Please enter your name");
			myName = "Joris";

			// String host = String.valueOf(view.getIp());
			// String host = view.getString("Please enter an IP address");
			String host = "127.0.0.1";
			// int port = view.getInt("Please enter a port number");
			int port = 8888;

			try {
				InetAddress addr = InetAddress.getByName(host);
				view.showMessage("Attempting to connect to " + addr + " on port " + port + "...");

				sock = new Socket(addr, port);
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			} catch (IOException e) {
				view.showMessage("ERROR: could not create a socket on " + host + " and port " + port + ".");
			}
		}
	}

	/**
	 * Sends a message to the connected server, followed by a new line.
	 */
	public void sendMessage(String msg) throws ServerUnavailableException {
		if (out != null) {
			try {
				out.write(msg);
				out.newLine();
				out.flush();
			} catch (IOException e) {
				view.showMessage(e.getMessage());
				throw new ServerUnavailableException("Could not write to server.");
			}
		} else {
			throw new ServerUnavailableException("Could not write to server.");
		}
	}

	/**
	 * Reads and returns a line from the server.
	 */
	public String readLineFromServer() throws ServerUnavailableException {
		if (in != null) {
			try {
				String msg = in.readLine();
				view.showMessage(("> [Server] Incoming: " + msg));
				if (msg == null) {
					throw new ServerUnavailableException("Could not read from server.");
				}
				return msg;
			} catch (IOException e) {
				throw new ServerUnavailableException("Could not read from server.");
			}
		} else {
			throw new ServerUnavailableException("Could not read from server.");
		}
	}

	/**
	 * Handles the server-client handshake as described in the protocol. Shows the
	 * user via the TUI what the version of the protocol is and the (optional)
	 * welcome message the server provided.
	 */
	public void handleHello() throws ServerUnavailableException, ProtocolException {
		this.sendMessage(String.valueOf(ProtocolMessages.HANDSHAKE + ProtocolMessages.DELIMITER + "1.0"
				+ ProtocolMessages.DELIMITER + this.myName + ProtocolMessages.DELIMITER + ProtocolMessages.BLACK));
		String line = this.readLineFromServer();
		String[] lineSplit = line.split(ProtocolMessages.DELIMITER);
		if (lineSplit[0] == null || !lineSplit[0].contentEquals(String.valueOf(ProtocolMessages.HANDSHAKE))) {
			throw new ProtocolException("Error: server did not give the handshake");
		} else {
			if (!(lineSplit.length > 1) || lineSplit[1] == null) {
				throw new ProtocolException("Error: server did not send the version");
			} else {
				view.showMessage("The version of the protocol is: " + lineSplit[1]);
				if (!(lineSplit.length > 2) || lineSplit[2] == null) {
					view.showMessage("Server did not provide a welcome message. Welcome anyway!");
				} else {
					view.showMessage(lineSplit[2]);
				}
			}
		}
	}

	/**
	 * Waits for the server to start a new Game with this client and another one.
	 * Updates the size of the board based on what the server sends. Shows the user
	 * via the TUI what the size of the board and his colour will be.
	 */
	public void waitForStart() throws ServerUnavailableException, ProtocolException {
		String line = this.readLineFromServer();
		String[] lineSplit = line.split(ProtocolMessages.DELIMITER);
		if (lineSplit[0] == null || !lineSplit[0].contentEquals(String.valueOf(ProtocolMessages.GAME))) {
			throw new ProtocolException("Error: server gave another command than the one for starting a game");
		} else {
			if (!(lineSplit.length > 1) || lineSplit[1] == null) {
				throw new ProtocolException("Error: server did not send the board");
			} else {
				view.showMessage("We will start a game.");
				this.boardSize = (int) Math.sqrt(lineSplit[1].length());
				view.showMessage(
						"The amount of intersections on the board will be : " + boardSize + " x " + boardSize + ".");
				if (!(lineSplit.length > 2) || lineSplit[2] == null) {
					throw new ProtocolException("Error: server did not provide a color");
				} else {
					view.showMessage("There are black and white stones. Your colour will be: " + lineSplit[2] + ".");
					if (lineSplit[2].charAt(0) == ProtocolMessages.BLACK) {
						this.mark = Mark.B;
					} else if (lineSplit[2].charAt(0) == ProtocolMessages.WHITE) {
						this.mark = Mark.W;
					} else {
						throw new ProtocolException("Error: server did not provide a valid color");
					}
				}
			}
		}
	}

	/**
	 * Sends a message to the server indicating that this client will quit. Does not
	 * wait for response, just closes the connection.
	 */
	public void sendExit() throws ServerUnavailableException {
		this.sendMessage(String.valueOf(ProtocolMessages.QUIT));
		this.closeConnection();
	}

	/**
	 * Closes the connection by closing the In- and OutputStreams, as well as the
	 * socket.
	 */
	public void closeConnection() {
		System.out.println("Closing the connection...");
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter method for the board size of the board of the game that this client is
	 * playing on.
	 */
	public int getBoardSize() {
		return this.boardSize;
	}

	/**
	 * Starts a new Client.
	 */
	public static void main(String[] args) {
		(new Client()).start();
	}

}
