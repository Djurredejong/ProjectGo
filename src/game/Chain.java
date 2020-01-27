package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A chain consists of stones (intersections with the same Mark, B or W) Each
 * stone in the chain has the same number of liberties, namely the amount of
 * neighbouring intersections of the chain that have Mark U.
 */
public class Chain {

	/**
	 * A list of the stones in this chain.
	 */
	private List<Intersec> stones;

	/**
	 * A set of the liberties of this chain.
	 */
	private Set<Intersec> liberties;

	/**
	 * Creates a new chain, with a stone of certain colour (therefore, mark should
	 * either be B or W) that has just been placed on the board and initialises the
	 * stones List of this chain with it. Also updates the chain of the provided
	 * stone (intersection) to be this chain.
	 */
	public Chain(Intersec stone, Mark mark) {
		stones = new ArrayList<>();
		liberties = new HashSet<>();
		stones.add(stone);
		stone.setChain(this);
	}

	/**
	 * Return the number of liberties of the chain, which is equal to those of all
	 * the stones in the chain combined, not counting any liberties twice. Hence the
	 * use of a Set.
	 */
	public int chainLib() {
		for (Intersec stone : stones) {
			for (Intersec liberty : stone.getLiberties()) {
				liberties.add(liberty);
			}
		}
		return liberties.size();
	}

	/**
	 * Adds the stones of some other chain to the stone(s) of this chain.
	 */
	public void joinChain(Chain otherChain) {
		for (Intersec stone : otherChain.getStones()) {
			this.stones.add(stone);
			stone.setChain(this);
		}
	}

	/**
	 * Getter method for the list of stones (intersections) of this chain
	 */
	public List<Intersec> getStones() {
		return this.stones;
	}

}
