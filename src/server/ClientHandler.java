package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
//import java.net.SocketException;

import exceptions.*;
import protocol.*;
import game.Game;

/**
 * Handles the communication with one client. 
 */
public class ClientHandler implements Runnable {

	/** The socket and In- and OutputStreams */
	private BufferedReader in;
	private BufferedWriter out;
	private Socket sock;

	/** The connected Server */
	private Server srv;

	/** Name of this ClientHandler and getter */
	private String name;
	public String getName() {
		return this.name;
	}

	/** The game this ClientHandler plays on, and getter+setter */
	private Game game;
	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
	}
	
	/** Are there already two players connected to the game of this player */
	boolean twoPlayers = false;
	public void setTwoPlayers(boolean twoPlayers) {
		this.twoPlayers = twoPlayers;
	}

	/** 
	 * Color of the player represented by this ClientHandler
	 * With getter+setter and method to change char to String
	 */
	private char color;
	public char getColor() {
		return color;
	}
	public void setColor(char color) {
		this.color = color;
	}
	public String colorString(char color) {
		if (color == ProtocolMessages.BLACK) {
			return "Black";
		}
		else if (color == ProtocolMessages.WHITE) {
			return "White";
		}
		else {
			return "No color!";
		}
	}

	/**
	 * Constructs a new ClientHandler. Opens the In- and OutputStreams.
	 * 
	 * @param sock The client socket
	 * @param srv  The connected server
	 * @param name The name of this ClientHandler
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
	 * Continuously listens to client input and forwards the input to the
	 * handleCommand(String msg) method.
	 */
	public synchronized void run() {
		String msg;
		try {
			try {
				//first the client needs to send the handshake
				msg = in.readLine();
				System.out.println("> [" + name + "] Incoming: " + msg);
				if (msg.charAt(0) == ProtocolMessages.HANDSHAKE) {
					out.write(srv.getHello());
					out.newLine();
					out.flush();
				}
				else {
					throw new ProtocolException("No handshake received from " + name);
				}
				
				System.out.println(name + " will wait for the game to start");
				//wait for the game to be start-ready, then send Start to clients
				//while (!twoPlayers) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				//}
				System.out.println("Informing " + name + " that the game will start!");
				out.write(ProtocolMessages.GAME + ProtocolMessages.DELIMITER + 
						this.game.getBoard() + ProtocolMessages.DELIMITER + this.color);
				out.newLine();
				out.flush();
				
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
			}
			catch (ProtocolException e) {
				//in case of a ProtocolException, disconnect the client
				out.write(ProtocolMessages.INVALID + ProtocolMessages.DELIMITER 
						+ "You did not adhere to the protocol, goodbye!");
				System.out.println(name + " did not adhere to the protocol, I will disconnect " + name);
				shutdown();
			}
		} catch (IOException e) {
			//This happens purposely
			System.out.println("Goodbye!");
			shutdown();
		} 
	}


	/**
	 * Send to the client that the game starts, according to the protocol
	 * @throws IOException 
	 */
	public void sendStart() throws IOException {
	}

	/**
	 * Handles commands received from the client by calling the according 
	 * methods via the Server.
	 * 
	 * @param msg Message from client
	 * @throws ProtocolException 
	 */
	private void handleCommand(String msg) throws IOException, ProtocolException {

		//if the received message is a move
		if (msg.charAt(0) == ProtocolMessages.MOVE) {
			String[] msgSplit = msg.split(ProtocolMessages.DELIMITER);
			if (msgSplit.length > 1 && msgSplit[1] != null && isInteger(msgSplit[1], 10)) {
				//if (srv.getGames().get(srv.getGames().indexOf(this.game)).getBoard().isValidMove(Integer.parseInt(msgSplit[1]))) {
				if (this.game.getBoard().isValidMove(Integer.parseInt(msgSplit[1]))) {
					this.srv.doMove(this, Integer.parseInt(msgSplit[1]));
					out.write(ProtocolMessages.RESULT + ProtocolMessages.DELIMITER + 
							ProtocolMessages.VALID + ProtocolMessages.DELIMITER + this.game.getBoard());
				}
				else {
					throw new ProtocolException("You did not send me a valid move!");
				}
			}
			else {
				throw new ProtocolException("You did not send me a move!");
			}					
		}

		//apparently, the received message does not correspond to the protocol
		else {

		}		
	}

	/**
	 * Checks whether the provided String is parsable to int
	 * 
	 * @param s to check
	 * @param radix 10 for decimal numbers
	 * @return true if parsable
	 */
	private boolean isInteger(String s, int radix) {
		if (s.isEmpty()) return false;
		for(int i = 0; i < s.length(); i++) {
			if(i == 0 && s.charAt(i) == '-') {
				if(s.length() == 1) return false;
				else continue;
			}
			if (Character.digit(s.charAt(i),radix) < 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Shut down the connection to this client by closing the socket and 
	 * the In- and OutputStreams.
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

}

