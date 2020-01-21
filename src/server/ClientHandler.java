package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import protocol.*;
import exceptions.*;

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

	/** Name of this ClientHandler */
	private String name;


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
		assignToGame();
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
		} catch (IOException e) {
			System.out.println("Goodbye!");
			shutdown();
		}
	}

	/**
	 * Assigns the player represented by this ClientHandler to a game.
	 * Creates a new Game if there is no free game available.
	 */
	private void assignToGame() {
		if (!checkFreeGame()) {
			srv.getGames().add(new Game());
		}
		
	}

	/**
	 * Checks if there is a game with only one player that can hence be joined.
	 */
	private boolean checkFreeGame() {
		for (Game game : srv.getGames()) {
			if (game.isFree()) {
				return true;
			}
		}
	}

	/**
	 * Handles commands received from the client by calling the according 
	 * methods via the Server.
	 * 
	 * @param msg Message from client
	 */
	private void handleCommand(String msg) throws IOException {
		//do sth with msg
	}
	
	
	/**
	 * Getter for inGame.
	 * Called by the method Game
	 * 
	 * @return inGame (true if player represented by this Handler is assigned to a game)
	 */
	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
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

