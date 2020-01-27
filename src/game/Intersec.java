package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents an intersection on the board and stores info about the contents of
 * this intersection.
 */
public class Intersec {

	/**
	 * Represents the contents of the intersection (Unoccupied or stone and in case
	 * of the latter, which colour).
	 */
	private Mark mark;

	/**
	 * List of neighbouring intersections, initialised when creating the board
	 * Intersection can have 2, 3 or 4 neighbours.
	 */
	private List<Intersec> neighbours;

	/**
	 * List of liberties of this intersection (not actually a thing, but necessary
	 * for determining liberties of a chain). Initially equal to the list of
	 * neighbouring intersections.
	 */
	private Set<Intersec> liberties;

	/**
	 * The chain this intersection (in case its Mark is B or W, and hence it's a
	 * stone) belongs to.
	 */
	private Chain chain;

	/**
	 * The column of this intersection.
	 */
	private int col;

	/**
	 * The row of this intersection.
	 */
	private int row;

	/**
	 * Creates a new intersection. Initialises the column and row. This intersection
	 * is unoccupied, does hence not belong to any chain. The neighbour list and
	 * liberties set get initialised by the method calling this (the constructor of
	 * Board).
	 */
	public Intersec(int col, int row) {
		this.col = col;
		this.row = row;
		this.mark = Mark.U;
		this.chain = null;
		neighbours = new ArrayList<>();
		liberties = new HashSet<>();
	}

	/**
	 * Getter method for the mark of this intersection.
	 */
	public Mark getMark() {
		return mark;
	}

	/**
	 * Setter method for the mark of this intersection.
	 */
	public void setMark(Mark mark) {
		this.mark = mark;
	}

	/**
	 * Getter method for the chain this intersection belongs to.
	 */
	public Chain getChain() {
		return chain;
	}

	/**
	 * Setter method for the chain this intersection belongs to.
	 */
	public void setChain(Chain chain) {
		this.chain = chain;
	}

	/**
	 * Getter method for the neighbours of this intersection.
	 */
	public List<Intersec> getNeighbours() {
		return neighbours;
	}

	/**
	 * Adds a neighbour to the neighbours list of this intersection.
	 */
	public void addNeighbour(Intersec neighbour) {
		this.neighbours.add(neighbour);
	}

	/**
	 * Getter method for the liberties of this intersection.
	 */
	public Set<Intersec> getLiberties() {
		return liberties;
	}

	/**
	 * Adds a liberty to the liberties list of this intersection.
	 */
	public void addLiberty(Intersec liberty) {
		this.liberties.add(liberty);
	}

	/**
	 * Removes a liberty from the liberties list of this intersection.
	 */
	public void removeLiberty(Intersec liberty) {
		this.liberties.remove(liberty);
	}

	/**
	 * Setter method for the row of this intersection.
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Getter method for the row of this intersection.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Useful for debugging.
	 */
	@Override
	public String toString() {
		return (mark + ": " + col + ", " + row);
	}

}
