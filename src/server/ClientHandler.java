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
	public void run() {
		String msg;
		try {
			try {
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
				System.out.println(name + " did not adhere to the protocol, I will isconnect " + name);
				shutdown();
			}
		} catch (IOException e) {
			//This happens purposely...?
			System.out.println("Goodbye!");
			shutdown();
		} 
	}


	/**
	 * Handles commands received from the client by calling the according 
	 * methods via the Server.
	 * 
	 * @param msg Message from client
	 * @throws ProtocolException 
	 */
	private void handleCommand(String msg) throws IOException, ProtocolException {

		//as soon as the handshake has been received,
		//the client should not send it again
		boolean handshakeReceived = false;
		//if the received message is the handshake
		if (msg.charAt(0) == ProtocolMessages.HANDSHAKE) {
			if (handshakeReceived == true) {
				throw new ProtocolException("You've already shook my hand!");
			}
			else {
				out.write(srv.getHello());
				handshakeReceived = true;
			}
		}
		
		//if the received message is a turn
		else if (msg.charAt(0) == ProtocolMessages.TURN) {
			String[] msgSplit = msg.split(ProtocolMessages.DELIMITER);
			if (msgSplit.length > 1 && msgSplit[1] != null) {
				if (srv.getGames().get(srv.getGames().indexOf(this.game)).getBoard().isValidMove(Integer.parseInt(msgSplit[1]))) {
					out.write(srv.doMove(msgSplit[1]));
				}
				else {
					throw new ProtocolException("You did not send me a valid move!");
				}
			}
			else {
				throw new ProtocolException("You did not send me a possible turn!");
			}					
		}

		//apparently, the received message does not correspond to the protocol
		else {

		}		
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

