package game;

import java.util.ArrayList;
import java.util.List;

public class Intersec {

	/**
	 * Represents the state of the intersection (Unoccupied or stone and in case of
	 * the latter, which colour)
	 */
	private Mark mark;

	/**
	 * Number of liberties of an intersection
	 */
	private int liberties;

	/**
	 * List of neighbouring intersections, initialised when creating the board
	 * Intersection can have 2, 3 or 4 neighbours
	 */
	private List<Intersec> neighbours;

	/**
	 * Creates a new intersection This intersection is unoccupied and initially has
	 * 4 liberties
	 */
	public Intersec() {
		this.mark = Mark.U;
		this.liberties = 4;
		neighbours = new ArrayList<>();
	}

	/**
	 * getter method for the mark of this intersection
	 */
	public Mark getMark() {
		return mark;
	}

	/**
	 * setter method for the mark of this intersection
	 */
	public void setMark(Mark mark) {
		this.mark = mark;
	}

	/**
	 * getter method for the number of liberties of this intersection
	 */
	public int getLiberties() {
		return this.liberties;
	}

	/**
	 * Reduces the number of liberties of this intersection by 1
	 */
	public void reduceLib() {
		this.liberties--;
	}

	/**
	 * Increases the number of liberties of this intersection by 1
	 */
	public void increaseLib() {
		this.liberties++;
	}

	/**
	 * getter method for the neighbours of this intersection
	 */
	public List<Intersec> getNeighbours() {
		return neighbours;
	}

	/**
	 * adds a neighbour tot the neighbours list of this intersection
	 */
	public void addNeighbour(Intersec neighbour) {
		this.neighbours.add(neighbour);
	}

}
