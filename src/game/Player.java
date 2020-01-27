package game;

import java.util.Scanner;

import exceptions.ExitProgram;

public class Player {

	/**
	 * The name of this player
	 */
	private String name;

	/**
	 * The mark of this player, either B or W
	 */
	private Mark mark;

	/**
	 * Creates a new player with the provided name and mark
	 */
	public Player(String name, Mark mark) {
		this.mark = mark;
	}

	/**
	 * Scanner for input, TODO replace by inputstream from socket
	 */
	Scanner in = new Scanner(System.in);

	/**
	 * Make a move on the board: Let the player decide on where to place the next
	 * stone and then - check if the intersection is valid (exists and unoccupied)
	 * (if not, let the player decide again) - TODO check if no previous board
	 * situation is recreated (if not, let the player decide again)
	 */
	public boolean makeMove(Board board) throws ExitProgram {
		System.out.println();
		System.out.println(name + ", it is your turn!");
		System.out.println("Do you want to pass? (y/n)");
		boolean pass = in.nextBoolean();
		if (pass) {
			return true;
		}
		System.out.println("What column do you want to place your stone in?");
		int col = in.nextInt();
		System.out.println("What row do you want to place your stone in?");
		int row = in.nextInt();
		int intersec = row * board.getBoardSize() + col;

		boolean valid = board.isUnoccupied(intersec);

		while (!valid || intersec < 0 || intersec > board.getBoardSize() * board.getBoardSize()) {
			System.out.println("That is not a valid intersection. Try again!");
			System.out.println("What column do you want to place your stone in?");
			col = in.nextInt();
			System.out.println("What row do you want to place your stone in?");
			row = in.nextInt();
			intersec = row * board.getBoardSize() + col;
			valid = board.isUnoccupied(intersec);
		}

		board.putStone(col, row, this.mark);
		return false;
	}

}
