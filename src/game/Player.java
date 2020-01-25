package game;

import java.util.Scanner;

public class Player {
	
	private Mark mark;
	
	public Player(Mark mark) {
		this.mark = mark;
	}

	
	Scanner in = new Scanner(System.in);

	public void makeMove(Game game) {
		
		System.out.println("What column do you want to place your stone in?");
        int col = in.nextInt();
        
		System.out.println("What row do you want to place your stone in?");
        int row = in.nextInt();
        
        
        
//        boolean valid = board.isField(choice) && board.isEmptyField(choice);
//        while (!valid) {
//            System.out.println("ERROR: field " + choice
//                    + " is no valid choice.");
//            System.out.println(prompt);
//            choice = TextIO.getInt();
//            valid = board.isField(choice) && board.isEmptyField(choice);
//        }
//        return choice;
        
        game.getGui().addStone(col, row, this.mark.color());
        
        
	}

}
