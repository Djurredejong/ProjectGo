package game;

import java.util.ArrayList;
import java.util.List;

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
	 * Number of liberties of an intersection. Own convention: an unoccupied
	 * intersection will always have its number of liberties set equal to its number
	 * of neighbouring intersections.
	 */
	private int liberties;

	/**
	 * List of neighbouring intersections, initialised when creating the board
	 * Intersection can have 2, 3 or 4 neighbours.
	 */
	private List<Intersec> neighbours;

	/**
	 * The chain this intersection (in case its Mark is B or W, and hence it's a
	 * stone) belongs to.
	 */
	private Chain chain;

	/**
	 * Creates a new intersection This intersection is unoccupied, does hence not
	 * belong to any chain and initially has 4 liberties.
	 */
	public Intersec() {
		this.mark = Mark.U;
		this.liberties = 4;
		this.chain = null;
		neighbours = new ArrayList<>();
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
	 * Getter method for the number of liberties of this intersection.
	 */
	public int getLiberties() {
		return this.liberties;
	}

	/**
	 * Setter method for the number of liberties of this intersection.
	 */
	public void setLiberties(int liberties) {
		this.liberties = liberties;
	}

	/**
	 * Reduces the number of liberties of this intersection by 1.
	 */
	public void reduceLib() {
		this.liberties--;
	}

	/**
	 * Increases the number of liberties of this intersection by 1.
	 */
	public void increaseLib() {
		this.liberties++;
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

}
