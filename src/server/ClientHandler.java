package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
//import java.net.SocketException;

import exceptions.ProtocolException;
import game.Game;
import protocol.ProtocolMessages;

/**
 * Handles the communication with one client.
 */
public class ClientHandler implements Runnable {

	/**
	 * The server, socket and in- and outputStreams.
	 */
	private Server srv;
	private BufferedReader in;
	private BufferedWriter out;
	private Socket sock;

	/**
	 * Name of this ClientHandler and getter.
	 */
	private String name;

	/**
	 * The player that this ClientHandler represents.
	 */
	// private Player player;

	/**
	 * The game this ClientHandler plays on.
	 */
	private Game game;

	/**
	 * Set to true as soon as two players are connected to the game that this
	 * ClientHandler plays on.
	 */
	volatile boolean twoPlayers = false;

	/**
	 * Colour of the player represented by this ClientHandler.
	 */
	private char color;

	/**
	 * Constructs a new ClientHandler. Opens the In- and OutputStreams.
	 */
	public ClientHandler(Socket sock, Server srv, String name) {
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			this.sock = sock;
			this.srv = srv;
			this.name = name;
		} catch (IOException e) {
			System.out.println("IOException in ClientHandler constructor!");
			shutdown();
		}
	}

	/**
	 * First handle the Handshake as defined in the protocol. Then wait for the sign
	 * of the Server that there are two players assigned to a game and the game will
	 * start. After that, continuously listen to client input and forward that input
	 * to the handleCommand(String msg) method.
	 */
	public void run() {
		try {
			try {
				doHandshake();
				while (!twoPlayers) {
					// wait
				}
				sendStart();
				String msg;
				msg = in.readLine();
				while (msg != null) {
					System.out.println("> [" + name + "] Incoming: " + msg);
					handleCommand(msg);
					out.newLine();
					out.flush();
					msg = in.readLine();
				}
				System.out.println("Shutting down");
				shutdown();
			} catch (ProtocolException e) {
				// in case of a ProtocolException, disconnect the client
				out.write(ProtocolMessages.INVALID + ProtocolMessages.DELIMITER
						+ "You did not adhere to the protocol, goodbye!");
				System.out.println(name + " did not adhere to the protocol, disconnect " + name);
				shutdown();
			}
		} catch (IOException e) {
			// This happens purposely
			System.out.println("Goodbye!");
			shutdown();
		}
	}

	/**
	 * Handles commands received from the client by calling the according methods
	 * via the Server.
	 */
	private void handleCommand(String msg) throws IOException, ProtocolException {

		if (msg.charAt(0) == ProtocolMessages.MOVE) {
			String[] msgSplit = msg.split(ProtocolMessages.DELIMITER);
			if (msgSplit.length > 1 && msgSplit[1] != null && isInteger(msgSplit[1])) {
				// if
				// (srv.getGames().get(srv.getGames().indexOf(this.game)).getBoard().isValidMove(Integer.parseInt(msgSplit[1])))
				// {
				if (this.game.getBoard().isValidMove(Integer.parseInt(msgSplit[1]))) {
					this.srv.doMove(this, Integer.parseInt(msgSplit[1]));
					out.write(ProtocolMessages.RESULT + ProtocolMessages.DELIMITER + ProtocolMessages.VALID
							+ ProtocolMessages.DELIMITER + this.game.getBoard());
				} else {
					throw new ProtocolException("You did not send me a valid move!");
				}
			} else {
				throw new ProtocolException("You did not send me a move!");
			}
		}

		// apparently, the received message does not correspond to the protocol
		else {
			throw new ProtocolException("You did not send me a valid command!");
		}
	}

	/**
	 * Send to the client that the game starts, according to the protocol
	 */
	public void sendStart() throws IOException {
		out.write(ProtocolMessages.GAME + ProtocolMessages.DELIMITER + this.game.getBoard() + ProtocolMessages.DELIMITER
				+ this.color);
		out.newLine();
		out.flush();
	}

	/**
	 * Responds to the client's handshake, according to the protocol
	 */
	private void doHandshake() throws IOException, ProtocolException {
		String msg = in.readLine();
		System.out.println("> [" + name + "] Incoming: " + msg);
		if (msg.charAt(0) == ProtocolMessages.HANDSHAKE) {
			out.write(ProtocolMessages.HANDSHAKE + ProtocolMessages.DELIMITER + "1.0" + ProtocolMessages.DELIMITER
					+ "Welcome to this server that will let you play go.");
			out.newLine();
			out.flush();
		} else {
			throw new ProtocolException("No handshake received from " + name);
		}
	}

	/**
	 * Shut down the connection to this client by closing the socket and the In- and
	 * OutputStreams.
	 */
	private void shutdown() {
		System.out.println("> [" + name + "] Shutting down.");
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		srv.removeClient(this);
	}

	/**
	 * Getter method for the name of this ClientHandler.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Getter method for the game this ClientHandler plays on.
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Setter method for the game this ClientHandler plays on.
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * Setter method for the twoPlayers boolean.
	 */
	public void setTwoPlayers(boolean twoPlayers) {
		this.twoPlayers = twoPlayers;
	}

	/**
	 * Getter method for the colour of this ClientHandler.
	 */
	public char getColor() {
		return color;
	}

	/**
	 * Setter method for the colour of this ClientHandler.
	 */
	public void setColor(char color) {
		this.color = color;
	}

	/**
	 * Return the colour of this ClientHandler as a String.
	 */
	public String colorString(char color) {
		if (color == ProtocolMessages.BLACK) {
			return "Black";
		} else if (color == ProtocolMessages.WHITE) {
			return "White";
		} else {
			return "No color!";
		}
	}

	/**
	 * Checks whether the provided String is parsable to a (decimal) integer.
	 */
	private boolean isInteger(String s) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), 10) < 0) {
				return false;
			}
		}
		return true;
	}

}
