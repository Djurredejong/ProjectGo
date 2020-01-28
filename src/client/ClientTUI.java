package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;

import exceptions.ExitProgram;
import exceptions.ServerUnavailableException;

/**
 * Client TUI for user input and user messages.
 */
public class ClientTUI {

	/**
	 * The client for whom this is the view.
	 */
	private Client client;

	/**
	 * The PrintWriter to write messages to.
	 */
	private PrintWriter console;

	/**
	 * The input stream.
	 */
	private BufferedReader in;

	/**
	 * Creates a new view for the provided client. Output of this view will be shown
	 * in the console (standard output). Input for this view will come from the user
	 * keyboard (standard input).
	 */
	public ClientTUI(Client client) {
		this.client = client;
		console = new PrintWriter(System.out, true);
		in = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * Ask for user input continuously and handles that input by calling the
	 * handleUserInput(userInput) method. If an ExitProgram exception is thrown,
	 * stop asking for input, send an exit message to the server according to the
	 * protocol and close the connection.
	 */
	public void start() throws ServerUnavailableException, ExitProgram {
		String userInput;
		try {
			while (true) {
				userInput = in.readLine();
				handleUserInput(userInput);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExitProgram(e + ": Cannot read user input.");
		} catch (ExitProgram exc) {
			client.sendExit();
		}
	}

	/**
	 * The user/client should be able to type one of the following three commands: p
	 * or pass to pass (ignore case), q or quit to quit (ignore case), an integer to
	 * define the intersection on the board for his next move.
	 */
	public void handleUserInput(String input) throws ExitProgram, ServerUnavailableException {
		showMessage("Please pick an intersection for your next stone, pass (p) or quit (q).");

		if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
			client.sendExit();
		}

		else if (input.equalsIgnoreCase("p") || input.equalsIgnoreCase("pass")) {
			client.doPass();
		} else if (isInteger(input)) {
			int intersection = Integer.parseInt(input);
			if (intersection < 0 || intersection > (client.getBoardSize()) - 1) {
				client.doMove(intersection);
			} else {
				showMessage("The integer you typed does not represent an intersection");
			}
		} else {
			showMessage("Please pick an intersection, type p to pass or q to quit");
		}
	}

	/**
	 * Shows a message to the user in the console.
	 */
	public void showMessage(String message) {
		console.println(message);
	}

	/**
	 * Asks the user to provide an IP address and return it. If the user input is
	 * not parsable to an IP address, asks again.
	 */
	public InetAddress getIp() throws ExitProgram {
		try {
			while (true) {
				showMessage("Please enter a valid IP");
				String ip;
				ip = in.readLine();
				String[] parts = ip.split("\\.");
				if (parts.length == 4) {
					for (String s : parts) {
						int i = Integer.parseInt(s);
						if ((i > 0) && (i < 255) && ip.endsWith(".")) {
							return InetAddress.getByName(ip);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExitProgram(e + ": Cannot read user input.");
		}
	}

	/**
	 * Prints the question and asks the user to input a String.
	 */
	public String getString(String question) throws ExitProgram {
		showMessage(question);
		try {
			return in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExitProgram(e + ": Cannot read user input.");
		}
	}

	/**
	 * Prints the question and asks the user to input an integer.
	 */
	public int getInt(String question) throws ExitProgram {
		System.out.println(question);
		try {
			return Integer.parseInt(in.readLine());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExitProgram(e + ": Cannot read user input.");
		}
	}

	/**
	 * Prints the question and asks the user for a yes/no answer.
	 */
	public boolean getBoolean(String question) throws ExitProgram {
		showMessage(question);
		try {
			String answer = in.readLine();
			if (answer.contentEquals("y") || answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("true")) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExitProgram(e + ": Cannot read user input.");
		}
	}

	/**
	 * Checks whether the provided String is parsable to an integer.
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
