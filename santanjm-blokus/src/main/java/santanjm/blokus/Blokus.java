package santanjm.blokus;

import santanjm.blokus.*;

public class Blokus {
	
	public static void main(String[] args) {
		try {
			Board b = new Board(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			b.gameLoop();
		} catch(Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
