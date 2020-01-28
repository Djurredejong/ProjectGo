package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;

import exceptions.ExitProgram;

/**
 * Client TUI for user input and user messages.
 */
public class ClientTUI {

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
	public ClientTUI() {
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

}
