package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Server TUI for user input and user messages.
 */
public class ServerTUI {

	/**
	 * The PrintWriter to write messages to
	 */
	private PrintWriter console;

	/**
	 * The input stream.
	 */
	private BufferedReader in;

	/**
	 * Creates a new view for the server. Output of this view will be shown in the
	 * console (standard output). Input for this view will come from the user
	 * keyboard (standard input).
	 */
	public ServerTUI() {
		console = new PrintWriter(System.out, true);
		in = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * Shows a message to the user in the console.
	 */
	public void showMessage(String message) {
		console.println(message);
	}

	/**
	 * Prints the question and asks the user to input a String.
	 */
	public String getString(String question) {
		console.println(question);
		try {
			return in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			console.println("IO Error in ServerTUI: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Prints the question and asks the user to input an integer.
	 */
	public int getInt(String question) {
		System.out.println(question);
		try {
			return Integer.parseInt(in.readLine());
		} catch (IOException e) {
			e.printStackTrace();
			console.println("IO Error in ServerTUI: " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Prints the question and asks the user for a yes/no answer.
	 */
	public boolean getBoolean(String question) {
		System.out.println(question);
		try {
			String answer = (in.readLine());
			if (answer.contentEquals("y") || answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("true")) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			console.println("IO Error in ServerTUI: " + e.getMessage());
			return false;
		}
	}

}
