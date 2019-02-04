package santanjm.blokus;

import org.junit.Test;
import static org.junit.Assert.*;
import santanjm.blokus.*;

public class PlayerTest {

	public static final Direction TOP = Direction.TOP;
	public static final Direction BOTTOM = Direction.BOTTOM;
	public static final Direction RIGHT = Direction.RIGHT;
	public static final Direction LEFT = Direction.LEFT;
	
	@Test
	public void testCreateClass1() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);

		assertNotNull(p1.getPiece(1, 0));
		assertEquals(p1.getPiece(1, 0).getClassification(), 1);
	}
	
	@Test
	public void testCreateClass2() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);
		
		assertNotNull(p1.getPiece(2, 0));
		assertNotNull(p1.getPiece(2, 0).search(LEFT));
		assertSame(p1.getPiece(2, 0), p1.getPiece(2, 0).search(LEFT, RIGHT));
		assertEquals(p1.getPiece(2, 0).getClassification(), 2);
	}
	
	@Test
	public void testCreateClass3() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);

		Nomino corner = p1.getPiece(3, 0);
		assertNotNull(corner);
		assertNotNull(corner.search(BOTTOM));
		assertEquals(corner.getClassification(), 3);
		
		Nomino line3 = p1.getPiece(3, 1);
		assertNotNull(line3);
		assertNotNull(line3.search(RIGHT));
		assertEquals(line3.getClassification(), 3);
	}
	
	@Test
	public void testCreateClass4() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);

		Nomino square = p1.getPiece(4, 0);
		assertNotNull(square);
		assertNotNull(square.search(BOTTOM, LEFT));
		assertNotNull(square.search(LEFT, BOTTOM));
		assertSame(square.search(BOTTOM, LEFT), square.search(LEFT, BOTTOM));
		assertEquals(square.getClassification(), 4);
		
		Nomino zshape = p1.getPiece(4, 1);
		assertNotNull(zshape);
		assertNotNull(zshape.search(LEFT, TOP));
		assertEquals(zshape.getClassification(), 4);
		
		Nomino line4 = p1.getPiece(4, 2);
		assertNotNull(line4);
		assertNotNull(line4.search(RIGHT, RIGHT));
		assertEquals(line4.getClassification(), 4);
		
		Nomino tshape = p1.getPiece(4, 3);
		assertNotNull(tshape);
		assertNotNull(tshape.search(TOP));
		assertEquals(tshape.getClassification(), 4);
		
		Nomino lshape = p1.getPiece(4, 4);
		assertNotNull(lshape);
		assertNotNull(lshape.search(RIGHT, TOP));
		assertEquals(lshape.getClassification(), 4);
	}
	
	@Test
	public void testGetRemainingPieces() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);

		// Since the player has not yet placed any pieces, it should 21 remaining pieces
		assertTrue(p1.getRemainingPiecesAsArray().length == 21);
		
		// Place p1's single monomino
		b.placePiece(p1.getPiece(1, 0), 0, 0);
		
		// Player p1 should now have 20 remaining pieces
		assertTrue(p1.getRemainingPiecesAsArray().length == 20);
	}
	
	@Test
	public void testPieceIsPlaced() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);

		// The single nomino has not been placed yet
		assertFalse(p1.pieceIsPlaced(1, 0));
		
		// The single monomino
		Nomino mon = p1.getPiece(1, 0);
		
		// Place the monomino
		b.placePiece(mon, 0, 0);
		
		// The monomino is now placed
		assertTrue(p1.pieceIsPlaced(1, 0));
	}
	
}











