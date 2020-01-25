package game;

public class GoGame {

	/**
	 * initialises the two players and starts a new game
	 */
	public static void main(String args[]) {
		Player p0 = new Player(Mark.B);
		Player p1 = new Player(Mark.W);

		Game game = new Game(p0, p1, 3);
		game.startPlay();
	}	
}
