package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ServerTUI {

	private PrintWriter console;
	private BufferedReader in;

	public ServerTUI() {
		console = new PrintWriter(System.out, true);
		in = new BufferedReader(new InputStreamReader(System.in));
	}

	public void showMessage(String message) {
		console.println(message);
	}

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

	public boolean getBoolean(String question) {
		System.out.println(question);
		try {
			String answer = (in.readLine());
			if (answer.contentEquals("y") || answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("true")) {
				return true;
			}
			else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			console.println("IO Error in ServerTUI: " + e.getMessage());
			return false;
		}
	}

}
