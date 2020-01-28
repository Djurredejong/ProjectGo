package game;

import java.util.Scanner;

import exceptions.ExitProgram;

public class HumanPlayer extends Player {

	/**
	 * The name of this player
	 */
	private String name;

	/**
	 * Scanner for input, TODO replace by input stream from socket
	 */
	Scanner in = new Scanner(System.in);

	/**
	 * Create a new human player.
	 */
	public HumanPlayer(String name, Mark mark) {
		super(name, mark);
		this.name = name;
	}

	/**
	 * Determine a move on the board: Let the player decide on where to place the
	 * next stone and then check if the intersection is valid (exists and
	 * unoccupied). If not, let the player decide again.
	 */
	public int determineMove(Board board) throws ExitProgram {
		System.out.println("Do you want to pass? (if yes, type y)");
		if (in.next().contentEquals("y")) {
			return -1;
		}
		System.out.println("In that case you should put a stone on the board.");
		System.out.println("What column do you want to place your stone in?");
		int col = in.nextInt();
		System.out.println("What row do you want to place your stone in?");
		int row = in.nextInt();
		int intersec = row * board.getBoardSize() + col;

		while (intersec < 0 || intersec > board.getBoardSize() * board.getBoardSize()
				|| !board.isUnoccupied(intersec)) {
			System.out.println("That is not a valid intersection. Try again!");
			System.out.println("What column do you want to place your stone in?");
			col = in.nextInt();
			System.out.println("What row do you want to place your stone in?");
			row = in.nextInt();
			intersec = row * board.getBoardSize() + col;
		}

		// TODO Check wheter the move would recreate a previous board situation (if so,
		// let the player decide again)
		return row * board.getBoardSize() + col;
	}

}
