package client;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
//import java.util.Scanner;

import exceptions.*;
import protocol.*;

public class Client {

	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private ClientTUI view;
	private String myName;

	private int boardSize;
	public int getBoardSize() {
		return boardSize;
	}

	/**
	 * Constructs a new Client. Initialises the view.
	 */
	public Client() {
		view = new ClientTUI(this);
	}

	/**
	 * Starts a new HotelClient by creating a connection, followed by the 
	 * HELLO handshake as defined in the protocol. After a successful 
	 * connection and handshake, the view is started. The view asks for 
	 * user input and handles all further calls to methods of this class. 
	 * 
	 * When errors occur, or when the user terminates a server connection, the
	 * user is asked whether a new connection should be made.
	 */
	public void start() {
		try {
			createConnection();
			System.out.println("Connection to the server created!");
			this.handleHello();
			this.waitForStart();
			view.start();
		} catch (ExitProgram | ServerUnavailableException | ProtocolException e) {
			view.showMessage(e + " in Client.start()");
			e.printStackTrace();
		} 
	}


	/**
	 * Creates a connection to the server. Requests the IP and port to 
	 * connect to at the view (TUI).
	 * 
	 * The method continues to ask for an IP and port and attempts to connect 
	 * until a connection is established or until the user indicates to exit 
	 * the program.
	 * 
	 * @throws ExitProgram if a connection is not established and the user 
	 * 				       indicates to want to exit the program.
	 * @ensures serverSock contains a valid socket connection to a server
	 */
	public void createConnection() throws ExitProgram {
		clearConnection();
		while (sock == null) {
			//myName = view.getString("Please enter your name");
			myName = "speler";
			//String host = String.valueOf(view.getIp());
			String host = "127.0.0.1";
			//int port = view.getInt("Please enter a port number");
			int port = 8888;

			// try to open a Socket to the server
			try {
				InetAddress addr = InetAddress.getByName(host);
				view.showMessage("Attempting to connect to " + addr + " :" 
						+ port + "...");
				sock = new Socket(addr, port);
				in = new BufferedReader(new InputStreamReader(
						sock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(
						sock.getOutputStream()));
			} catch (IOException e) {
				view.showMessage("ERROR: could not create a socket on " 
						+ host + " and port " + port + ".");
			}
			//
			//				//Do you want to try again? (ask user, to be implemented)
			//				System.out.println("Do you want to try again? y/n");	
			//
			//				Boolean cont = view.getBoolean("Do you want to try again? y/n");
			//
			//				if (cont == false) {
			//					throw new ExitProgram("User indicated to exit.");
			//				}

		}
	}


	/**
	 * Resets the serverSocket and In- and OutputStreams to null.
	 * 
	 * Always make sure to close current connections via shutdown()  ==> this is a method in the HotelClientHandler class!
	 * before calling this method!
	 */
	public void clearConnection() {
		sock = null;
		in = null;
		out = null;
	}

	/**
	 * Sends a message to the connected server, followed by a new line. 
	 * The stream is then flushed.
	 * 
	 * @param msg the message to write to the OutputStream.
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public synchronized void sendMessage(String msg) 
			throws ServerUnavailableException {
		if (out != null) {
			try {
				out.write(msg);
				out.newLine();
				out.flush();
			} catch (IOException e) {
				view.showMessage(e.getMessage());
				throw new ServerUnavailableException("Could not write "
						+ "to server.");
			}
		} else {
			throw new ServerUnavailableException("Could not write "
					+ "to server.");
		}
	}

	/**
	 * Reads and returns one line from the server.
	 * 
	 * @return the line sent by the server.
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public String readLineFromServer() 
			throws ServerUnavailableException {
		if (in != null) {
			try {
				// Read and return answer from Server
				String answer = in.readLine();
				if (answer == null) {
					throw new ServerUnavailableException("Could not read "
							+ "from server.");
				}
				return answer;
			} catch (IOException e) {
				throw new ServerUnavailableException("Could not read "
						+ "from server.");
			}
		} else {
			throw new ServerUnavailableException("Could not read "
					+ "from server.");
		}
	}

	/**
	 * Closes the connection by closing the In- and OutputStreams, as 
	 * well as the socket.
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
	 * Handles the server-client handshake as described in the protocol
	 */
	public void handleHello() 
			throws ServerUnavailableException, ProtocolException {
		this.sendMessage(String.valueOf(ProtocolMessages.HANDSHAKE + ProtocolMessages.DELIMITER + 
				//requestedVersion + ProtocolMessages.DELIMITER + 
				this.myName));
		String line = this.readLineFromServer();
		String[] lineSplit = line.split(ProtocolMessages.DELIMITER);

		if (lineSplit[0] == null || !lineSplit[0].contentEquals(String.valueOf(ProtocolMessages.HANDSHAKE))) {
			throw new ProtocolException("Error: server did not give the handshake");
		} 
		else {
			if (!(lineSplit.length > 1) || lineSplit[1] == null) {
				throw new ProtocolException("Error: server did not send the version");
			}
			else {
				view.showMessage("The version of the protocol is: " + lineSplit[1]);
				if (!(lineSplit.length > 2) || lineSplit[2] != null) {
					view.showMessage("Server did not provide a welcome message. Welcome anyway!");
				} 
				else {
					view.showMessage(lineSplit[2]);
				}
			}
		}
	}

	/**
	 * Waits for the server to start a new Game with this client and another one
	 */
	public void waitForStart() throws ServerUnavailableException, ProtocolException {
		view.showMessage("Waiting for the start of the game...");
		
		System.out.println("going to read line from server and print this line directly under here:");
		String line = this.readLineFromServer();
		System.out.println("line read " + line);
		
		String[] lineSplit = line.split(ProtocolMessages.DELIMITER);

		if (lineSplit[0] == null || !lineSplit[0].contentEquals(String.valueOf(ProtocolMessages.GAME))) {
			throw new ProtocolException("Error: server gave another command than the one for starting a game");
		} 
		else {
			if (!(lineSplit.length > 1) || lineSplit[1] == null) {
				throw new ProtocolException("Error: server did not send the board");
			}
			else {
				view.showMessage("The state of the board is: " + lineSplit[1]);
				this.boardSize = lineSplit[1].length();
				view.showMessage("The size of the board is: " + boardSize);
				if (!(lineSplit.length > 2) || lineSplit[2] == null) {
					throw new ProtocolException("Error: server did not provide a color");
				} 
				else {
					view.showMessage("Your color is: " + lineSplit[2]);
				}
			}
		}
	}


	/**
	 * Checks if the move the user wants to do is valid.
	 * If so, sends this move to the server.
	 * Then waits for and reads the response from the server.
	 * 
	 * @param intersection The selected intersection
	 */
	public void doMove(int intersection) throws ServerUnavailableException {
		//TODO check if move is valid
		this.sendMessage(String.valueOf(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + intersection));
		String line = this.readLineFromServer();
		//TODO update board accordingly
		view.showMessage("> " + line);
	}

	/**
	 * Lets the server know this client wants to pass
	 * Then waits for and reads the response from the server.
	 */
	public void doPass() throws ServerUnavailableException {
		this.sendMessage(String.valueOf(ProtocolMessages.PASS));
		String line = this.readLineFromServer();
		//TODO update board accordingly
		view.showMessage("> " + line);
	}

	/** Sends a message to the server indicating that this client will quit:
	 * 
	 * Does not wait for response, just closes the connection.
	 */
	public void sendExit() throws ServerUnavailableException {
		this.sendMessage(String.valueOf(ProtocolMessages.QUIT));
		this.closeConnection();
	}

	/**
	 * Starts a new Client.
	 */
	public static void main(String[] args) {
		(new Client()).start();
	}

}

