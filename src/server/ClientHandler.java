package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
//import java.net.SocketException;

import exceptions.ExitProgram;
import exceptions.ProtocolException;
import game.Mark;
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
	 * Mark (colour) of the Client's player represented by this ClientHandler.
	 */
	private Mark mark;

	/**
	 * Set to true as soon as two players are connected to the game that this
	 * ClientHandler plays on.
	 */
	volatile boolean twoPlayers = false;

	/**
	 * When true, the player with colour white should play; when false, the player
	 * with colour black should play. TODO other solution for determining whose turn
	 * it is, so that more games can be played simultaneously.
	 */
	volatile static private boolean whiteTurn;

	/**
	 * The last move of the opponent player. In the beginning and after a pass, it's
	 * value should be -1.
	 */
	volatile static private int lastMove;

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
	 * start. After that, continuously send the client that it is their turn and the
	 * last move of the opponent player, listen to their response, and handle it.
	 */
	public void run() {
		try {
			try {
				doHandshake();
				while (!twoPlayers) {
					// wait for second player
				}
				sendStart();
				String msg;
				if (this.mark == Mark.W) {
					while (!whiteTurn) {
						// black should make the first move
						// if white, wait till it's white's turn
					}
				}

				while (true) {
					this.sendTurn();
					msg = in.readLine();
					srv.view.showMessage(("> [" + name + "] Incoming: " + msg));
					handleCommand(msg);
					ClientHandler.whiteTurn = !ClientHandler.whiteTurn;
					if (this.mark == Mark.W) {
						while (!whiteTurn) {
							// wait till it's white's turn again
						}
					} else if (this.mark == Mark.B) {
						while (whiteTurn) {
							// wait till it's black's turn again
						}
					}
				}
			}

			catch (ProtocolException | NumberFormatException | ExitProgram e) {
				// in case of a ProtocolException, disconnect the client
				System.out.println(name + " did not adhere to the protocol, disconnect " + name);
				shutdown();
			}
		} catch (IOException e) {
			// this happens purposely
			System.out.println("Shutting down. Goodbye!");
			shutdown();
		}
	}

	/**
	 * Handles commands received from the client by calling the doMove method via
	 * the Server or, in the case of a pass, doing nothing and then writing back
	 * according to the protocol.
	 */
	private void handleCommand(String msg) throws IOException, ProtocolException, NumberFormatException, ExitProgram {

		if (msg.charAt(0) == ProtocolMessages.MOVE) {
			String[] msgSplit = msg.split(ProtocolMessages.DELIMITER);
			if (msgSplit.length > 1 && msgSplit[1] != null && isInteger(msgSplit[1])) {
				this.srv.doMove(this.mark, Integer.parseInt(msgSplit[1]));
				out.write(String.valueOf(ProtocolMessages.RESULT + ProtocolMessages.DELIMITER + ProtocolMessages.VALID
						+ ProtocolMessages.DELIMITER + srv.getBoard()));
			} else if (msgSplit.length > 1 && msgSplit[1] != null
					&& msgSplit[1].contentEquals(String.valueOf(ProtocolMessages.PASS))) {
				out.write(String.valueOf(ProtocolMessages.RESULT + ProtocolMessages.DELIMITER + ProtocolMessages.VALID
						+ ProtocolMessages.DELIMITER + srv.getBoard()));
			} else {
				throw new ProtocolException("You did not send me a move!");
			}
			out.newLine();
			out.flush();
		}

		// apparently, the received message does not correspond to the protocol
		else {
			throw new ProtocolException("You did not send me a valid command!");
		}
	}

	/**
	 * Sends to a Client that it is his turn. If the opponent's last move was a
	 * Pass, send this to the Client, if his last move was putting a stone on the
	 * board, send the intersection, according to the protocol.
	 */
	public void sendTurn() throws IOException {
		if (ClientHandler.lastMove == -1) {
			srv.view.showMessage(
					"Sending to " + this.name + ": " + String.valueOf(ProtocolMessages.TURN + ProtocolMessages.DELIMITER
							+ srv.getBoard().toString() + ProtocolMessages.DELIMITER + ProtocolMessages.PASS));
			out.write(String.valueOf(ProtocolMessages.TURN + ProtocolMessages.DELIMITER + srv.getBoard().toString()
					+ ProtocolMessages.DELIMITER + ProtocolMessages.PASS));
		} else {
			srv.view.showMessage(
					"Sending to " + this.name + ": " + String.valueOf(ProtocolMessages.TURN + ProtocolMessages.DELIMITER
							+ srv.getBoard().toString() + ProtocolMessages.DELIMITER + ClientHandler.lastMove));
			out.write(String.valueOf(ProtocolMessages.TURN + ProtocolMessages.DELIMITER + srv.getBoard().toString()
					+ ProtocolMessages.DELIMITER + ClientHandler.lastMove));
		}
		out.newLine();
		out.flush();
		srv.view.showMessage(this.name + " should now make a move!");
	}

	/**
	 * Send to the client that the game starts, according to the protocol
	 */
	public void sendStart() throws IOException {
		srv.view.showMessage("Sending to " + this.name + ": " + String.valueOf(ProtocolMessages.GAME
				+ ProtocolMessages.DELIMITER + srv.getBoard().toString() + ProtocolMessages.DELIMITER + this.mark));
		out.write(String.valueOf(ProtocolMessages.GAME + ProtocolMessages.DELIMITER + srv.getBoard().toString()
				+ ProtocolMessages.DELIMITER + this.mark));
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
			srv.view.showMessage("Sending to " + this.name + ": "
					+ String.valueOf(ProtocolMessages.HANDSHAKE + ProtocolMessages.DELIMITER + "1.0"
							+ ProtocolMessages.DELIMITER + "Welcome to this server that will let you play Go!"));
			out.write((String.valueOf(ProtocolMessages.HANDSHAKE + ProtocolMessages.DELIMITER + "1.0"
					+ ProtocolMessages.DELIMITER + "Welcome to this server that will let you play Go!")));
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
	 * Setter method for the twoPlayers boolean.
	 */
	public void setTwoPlayers(boolean twoPlayers) {
		this.twoPlayers = twoPlayers;
	}

	/**
	 * Setter method for the whiteTurn boolean, should initially be set to false.
	 */
	public void setWhiteTurn(boolean whiteTurn) {
		ClientHandler.whiteTurn = whiteTurn;
	}

	/**
	 * Setter method for the colour associated with this ClientHandler.
	 */
	public void setMark(Mark mark) {
		this.mark = mark;
	}

	/**
	 * Sets the last move of the opponent player. The provided parameter should be a
	 * number representing an intersection or -1 in case the opponent passed.
	 */
	public void setLastMove(int lastMove) {
		ClientHandler.lastMove = lastMove;
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
