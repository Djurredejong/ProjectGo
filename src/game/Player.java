package game;

import java.util.Scanner;

public class Player {

	/**
	 * The name of this player and getter method
	 */
	private String name;

	public String getName() {
		return name;
	}

	/**
	 * The mark of this player, either B or W, and getter method
	 */
	private Mark mark;

	public Mark getMark() {
		return mark;
	}

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
	 * stone and then - check if the intersection is unoccupied (if not, let the
	 * player decide again) - TODO check if no previous board situation is recreated
	 * (if not, let the player decide again)
	 */
	public void makeMove(Board board) {
		System.out.println("What column do you want to place your stone in?");
		int col = in.nextInt();
		System.out.println("What row do you want to place your stone in?");
		int row = in.nextInt();
		int intersec = row * board.getBoardSize() + col;

		boolean valid = board.isUnoccupied(intersec);

		while (!valid) {
			System.out.println("That is not a valid intersection. Try again!");
			System.out.println("What column do you want to place your stone in?");
			col = in.nextInt();
			System.out.println("What row do you want to place your stone in?");
			row = in.nextInt();
			intersec = row * board.getBoardSize() + col;
			valid = board.isUnoccupied(intersec);
		}

		board.putStone(col, row, this.mark);
	}
}
