package game;

import java.util.Scanner;

/**
 * Class that let's you play a game of Go on this computer. No need to worry
 * about a client-server network.
 */
public class GoGame {

	/**
	 * Scanner to get user input.
	 */
	static Scanner in = new Scanner(System.in);

	/**
	 * The name of the first human player.
	 */
	private static String name;

	/**
	 * The name of the optional second human player.
	 */
	private static String name2;

	/**
	 * The size of the board that the game will be played on.
	 */
	private static int boardSize;

	/**
	 * initialises the two players and starts a new game.
	 */
	public static void main(String args[]) {
		System.out.println("Let's play a round of Go!");
		System.out.println("What is your name?");
		name = in.nextLine();
		Player p0 = new HumanPlayer(name, Mark.B);
		System.out.println("Do you want the other player to be an AI player? (if yes, type y and hit Enter)");
		Player p1;
		if (in.nextLine().contentEquals("y")) {
			p1 = new ComputerPlayer("AIplayer", Mark.W);
		} else {
			System.out.println("What is the name of the second player?");
			name = in.nextLine();
			p1 = new HumanPlayer(name2, Mark.W);
		}
		System.out.println(
				"What should be the size of the board (size of the board = number of intersections in one dimension");
		boardSize = in.nextInt();
		Game game = new Game(p0, p1, boardSize, true);
		game.startPlay();

	}
}
