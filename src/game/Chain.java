package game;

import java.util.ArrayList;
import java.util.List;

/**
 * A chain consists of stones (intersections with the same Mark, B or W) Each
 * stone in the chain has the same number of liberties, namely the amount of
 * neighbouring intersections of the chain that have Mark U
 */
public class Chain {

	private List<Intersec> stones;

	public Chain() {
		stones = new ArrayList<>();
	}

}
