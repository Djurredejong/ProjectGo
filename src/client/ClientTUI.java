package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;

import exceptions.*;
import protocol.*;

public class ClientTUI {

	private Client client;

	private PrintWriter console;

	private BufferedReader in;

	public ClientTUI(Client client) {
		this.client = client;
		in = new BufferedReader(new InputStreamReader(System.in));
		console = new PrintWriter(System.out, true);
	}

	public void start() throws ServerUnavailableException {
		String userInput;
		try {
			while (true) {
				userInput = in.readLine();
				handleUserInput(userInput);
			}
		} catch (IOException e) {
			e.printStackTrace();
			showMessage("IO ERROR IN START METHOD OF HOTELCLIENTTUI");
		}
		catch (ExitProgram exc) {
			client.sendExit();
		}
	}

	/**
	 * The user/client should be able to type one of the following three commands:
	 * p or pass to pass (ignore case)
	 * q or quit to quit (ignore case)
	 * an integer to define the intersection on the board for his next move
	 * 
	 * @param input Input of the user
	 */
	public void handleUserInput(String input) throws ExitProgram, ServerUnavailableException {
		showMessage("Please pick an intersection for your next stone or pass (p)");
		if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
			client.sendExit();
		}
		else if (input.equalsIgnoreCase("p") || input.equalsIgnoreCase("pass")) {
			client.doPass();
		}
		else if (isInteger(input, 10)) {
			int intersection = Integer.parseInt(input);		
			if (intersection < 0 || intersection > (Integer.parseInt(client.getBoardSize()) - 1)) {
				client.doMove(intersection);
			}
			else {
				showMessage("The integer you typed does not represent an intersection");
			}
		}
		else {
			showMessage("Please pick an intersection, type p to pass or q to quit");
		}
	}

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

	public void showMessage(String message) {
		console.println(message);
	}

	public InetAddress getIp() {
		try {
			while (true) {
				showMessage("Please enter a valid IP");
				String ip;
				ip = in.readLine();
				String[] parts = ip.split( "\\." );
				if ( parts.length == 4 ) {
					for ( String s : parts ) {
						int i = Integer.parseInt( s );
						if ( (i > 0) && (i < 255) && ip.endsWith(".") ) {
							return InetAddress.getByName(ip);
						}			
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO ERROR IN GETIP METHOD OF HOTELCLIENTTUI");
			return null;
		}
	}

	public String getString(String question) {
		showMessage(question);
		try {
			return in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO ERROR IN GETSTRING METHOD OF HOTELCLIENTTUI");
			return null;
		}
	}

	public int getInt(String question) {
		System.out.println(question);
		try {
			return Integer.parseInt(in.readLine());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO ERROR IN GETINT METHOD OF HOTELCLIENTTUI");
			return 0;
		}
	}

	public boolean getBoolean(String question) {
		showMessage(question);
		try {
			String answer = in.readLine();
			if (answer.contentEquals("y") || answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("true")) {
				return true;
			}
			else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO ERROR IN GETBOOLEAN METHOD OF HOTELCLIENTTUI");
			return false;
		}
	}

	public void printHelpMenu() {
		showMessage(" Help menu: the following are valid commands:");
		showMessage(" i name .......... check in guest with name");
		showMessage(" o name .......... check out guest with name");
		showMessage(" r name .......... request room of guest");
		showMessage(" a name password . activate safe, password required for PricedSafe");
		showMessage(" b name nights ... print bill for guest (name) and number of nights");
		showMessage(" h ............... help (this menu)");
		showMessage(" p ............... print state");
		showMessage(" x ............... exit");
	}
}
