package santanjm.blokus;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import santanjm.blokus.*;

/**
 * Test class for the board
 */
public class BoardTest {
	
	public static final Direction TOP = Direction.TOP;
	public static final Direction BOTTOM = Direction.BOTTOM;
	public static final Direction RIGHT = Direction.RIGHT;
	public static final Direction LEFT = Direction.LEFT;

	@Test
	public void testLegalConstruct1() {
		Board legal1 = new Board();
		assertEquals(legal1.getSpaces().length, 400);
		
		Board legal2 = new Board(2);
		assertEquals(legal2.getSpaces().length, 400);
		assertEquals(legal2.getNumPlayers(), 2);
	}
	
	@Test
	public void testPlaceDomino() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);
		
		// Player p1's domino piece
		Nomino domino = p1.getPiece(2, 0);
		
		// Ensure the spaces at (3, 2) and (2, 2) are unoccupied
		assertFalse(b.getSpace(3, 2).isOccupied());
		assertFalse(b.getSpace(2, 2).isOccupied());
		
		// Place the domino on the board
		b.placePiece(domino, 3, 2);
		
		// Ensure the correct 2 spaces are now occupied
		assertTrue(b.getSpace(3, 2).isOccupied());
		assertTrue(b.getSpace(2, 2).isOccupied());
		
		// Assert that the domino has been flagged as placed
		assertTrue(domino.isPlaced());
	}
	
	@Test
	public void testPlaceSquare() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);
		
		// The square nomino
		Nomino square = p1.getPiece(4, 0);
		
		// Ensure that spaces (0, 0), (1, 0), (0, 1) and (1, 1) are unoccupied
		assertFalse(b.getSpace(0, 0).isOccupied());
		assertFalse(b.getSpace(0, 1).isOccupied());
		assertFalse(b.getSpace(1, 0).isOccupied());
		assertFalse(b.getSpace(1, 1).isOccupied());
		
		// Place the square on the board
		b.placePiece(square, 1, 0);
		
		// Ensure the correct four spaces are now occupied
		assertTrue(b.getSpace(0, 0).isOccupied());
		assertTrue(b.getSpace(0, 1).isOccupied());
		assertTrue(b.getSpace(1, 0).isOccupied());
		assertTrue(b.getSpace(1, 1).isOccupied());
	}
	
	@Test
	public void testWillOverlap() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);
		
		// The single monomino
		Nomino mono = p1.getPiece(1, 0);
		
		// Place it on the board at position (1, 1)
		b.placePiece(mono, 1, 1);
		
		// The domino
		Nomino domino = p1.getPiece(2, 0);
		
		// Check the legality of placing the domino next to the placed monomino
		assertFalse(b.willOverlap(domino, 2, 0));
		
		// Check the illegality of placing the domino on top of the placed monomino
		assertTrue(b.willOverlap(domino, 1, 1));
		assertTrue(b.willOverlap(domino, 2, 1));
		
		// Next, try rotating the domino 90 degrees and placing it next to the monomino
		domino.rotate();
		assertFalse(b.willOverlap(domino, 2, 1));
	}
	
	@Test
	public void testWillTouchOwnedPiece1() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);
		Player p2 = b.getPlayerFromNum(2);
		
		// The single monomino owned by player p1
		Nomino mono = p1.getPiece(1, 0);
		
		// Ensure that attempting to place mono at (1, 1) is legal
		assertFalse(b.willTouchOwnedPiece(mono, 1, 1));
		
		// Ensure that attempting to place mono at (0, 0) is legal (and won't cause null pointer)
		assertFalse(b.willTouchOwnedPiece(mono, 0, 0));
		
		// The domino owned by player p1
		Nomino domino1 = p1.getPiece(2, 0);
		
		// Place the domino at position (2, 1)
		b.placePiece(domino1, 2, 1);
		
		// Check the 6 spaces that touch one of the pieces of the domino, ensuring
		// that player p1 placing their monomino at any of these spaces is illegal
		assertTrue(b.willTouchOwnedPiece(mono, 2, 0));
		assertTrue(b.willTouchOwnedPiece(mono, 3, 1));
		assertTrue(b.willTouchOwnedPiece(mono, 2, 2));
		assertTrue(b.willTouchOwnedPiece(mono, 1, 2));
		assertTrue(b.willTouchOwnedPiece(mono, 0, 1));
		assertTrue(b.willTouchOwnedPiece(mono, 1, 0));
		
		// Check that placing mono outside those 6 spaces is still legal
		assertFalse(b.willTouchOwnedPiece(mono, 0, 0));
		assertFalse(b.willTouchOwnedPiece(mono, 0, 2));
		assertFalse(b.willTouchOwnedPiece(mono, 3, 0));
		assertFalse(b.willTouchOwnedPiece(mono, 3, 2));
		
		// The domino owned by player p2
		Nomino domino2 = p2.getPiece(2, 0);
		
		// Place player p2's domino at (5, 1)
		b.placePiece(domino2, 5, 1);
		
		// Check the 5 spaces that touch player p2's domino and ensure that
		// willTouchOwnedPiece will return false for player p1's monomino there
		assertFalse(b.willTouchOwnedPiece(mono, 5, 0));
		assertFalse(b.willTouchOwnedPiece(mono, 6, 1));
		assertFalse(b.willTouchOwnedPiece(mono, 5, 2));
		assertFalse(b.willTouchOwnedPiece(mono, 4, 2));
		assertFalse(b.willTouchOwnedPiece(mono, 4, 0));
		
		// Finally, check the space between p1's domino and p2's domino to ensure
		// that willTouchOwnedPiece will return true for p1's monomino there
		assertTrue(b.willTouchOwnedPiece(mono, 3, 1));
	}
	
	@Test
	public void testWillTouchOwnedPiece2() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);
		
		// The monomino owned by player p1
		Nomino mono = p1.getPiece(1, 0);
		
		// The u-shaped pentomino owned by player p1
		Nomino ushape = p1.getPiece(5, 9);
		
		// Place the monomino at (1, 1)
		b.placePiece(mono, 1, 1);
	
		// NOTE: The "head" of ushape is the nomino in the center of the 3
		// Ensure that placing ushape so any part of it touches mono will cause
		// willTouchOwnedPiece to return true
		assertTrue(b.willTouchOwnedPiece(ushape, 1, 2));
		assertTrue(b.willTouchOwnedPiece(ushape, 3, 2));
		assertTrue(b.willTouchOwnedPiece(ushape, 2, 3));
		assertTrue(b.willTouchOwnedPiece(ushape, 3, 1));
	}
	
	@Test
	public void testMonominoIsDiagonalToOwnedPiece() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);
		Player p2 = b.getPlayerFromNum(2);
		
		// The single monomino owned by player p1
		Nomino mon = p1.getPiece(1, 0);
		
		// Ensure that IsDiagonalToOwnedPiece will fail at position (10, 10)
		assertFalse(b.isDiagonalToOwnedPiece(mon, 10, 10));
		
		// Domino owned by player p1
		Nomino dom1 = p1.getPiece(2, 0);
		
		// Place the domino so it sits northwest of (10, 10)
		b.placePiece(dom1, 9, 9);
		
		// Now ensure that IsDiagonalToOwnedPiece will pass for mon at (10, 10)
		assertTrue(b.isDiagonalToOwnedPiece(mon, 10, 10));	
		
		// Next, deplace the domino so it may be replaced
		b.deplacePiece(dom1);
		
		// Assert that the piece has been properly deplaced by checking mon's diagonals
		assertFalse(b.isDiagonalToOwnedPiece(mon, 10, 10));
		
		// Place the domino directly above (10, 10) and check mon's diagonals
		b.placePiece(dom1, 10, 9);
		assertTrue(b.isDiagonalToOwnedPiece(mon, 10, 10));
	}
	
	@Test
	public void testIsDiagonalToOwnedPiece() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);
		
		// The z-shape plus pentomino owned by player p1
		Nomino zsp = p1.getPiece(5, 1);
		
		// We will attempt to place zsp at position (10, 10), currently
		// there are no owned pieces diagonal to it
		assertFalse(b.isDiagonalToOwnedPiece(zsp, 10, 10));
		
		// Occupy space (8, 8) by player p1 and check diagonals now
		b.getSpace(8, 8).occupy(1);
		assertTrue(b.isDiagonalToOwnedPiece(zsp, 10, 10));
		b.getSpace(8, 8).unoccupy();
		
		// Occupy space (10, 8) by player p1 and check diagonals now
		b.getSpace(10, 8).occupy(1);
		assertTrue(b.isDiagonalToOwnedPiece(zsp, 10, 10));
		b.getSpace(10, 8).unoccupy();
		
		// Occupy space (12, 9) by player p1 and check diagonals now
		b.getSpace(12, 9).occupy(1);
		assertTrue(b.isDiagonalToOwnedPiece(zsp, 10, 10));
		b.getSpace(12, 9).unoccupy();
		
		// Occupy space (12, 11) by player p1 and check diagonals now
		b.getSpace(12, 11).occupy(1);
		assertTrue(b.isDiagonalToOwnedPiece(zsp, 10, 10));
		b.getSpace(12, 11).unoccupy();
		
		// Occupy space (11, 12) by player p1 and check diagonals now
		b.getSpace(11, 12).occupy(1);
		assertTrue(b.isDiagonalToOwnedPiece(zsp, 10, 10));
		b.getSpace(11, 12).unoccupy();
		
		// Occupy space (9, 12) by player p1 and check diagonals now
		b.getSpace(9, 12).occupy(1);
		assertTrue(b.isDiagonalToOwnedPiece(zsp, 10, 10));
		b.getSpace(9, 12).unoccupy();
		
		// Occupy space (8, 11) by player p1 and check diagonals now
		b.getSpace(8, 11).occupy(1);
		assertTrue(b.isDiagonalToOwnedPiece(zsp, 10, 10));
		b.getSpace(8, 11).unoccupy();
	}
	
	@Test
	public void testFindAdjacent() {
		Board b = new Board(2);
		
		// The space in the top left corner of the grid
		Space corner = b.getSpace(0, 0);
		
		// Check the coordinates of the spaces to the right and below corner
		Space right = b.findAdjacent(corner, RIGHT);
		assertArrayEquals(new int[] {1, 0}, right.getCoords());
		Space below = b.findAdjacent(corner, BOTTOM);
		assertArrayEquals(new int[] {0, 1}, below.getCoords());
		
		// Ensure the spaces above and left of corner are null
		assertNull(b.findAdjacent(corner, TOP));
		assertNull(b.findAdjacent(corner, LEFT));
	}
	
	@Test
	public void testFind() {
		Board b = new Board(2);
		
		// The space in the very center of the board
		Space center = b.getSpace(10, 10);
		
		// Ensure that find correctly gets the spaces around center
		// Northwest (9, 9)
		assertEquals(b.getSpace(9, 9), b.find(center, TOP, LEFT));
		
		// North (10, 9)
		assertEquals(b.getSpace(10, 9), b.find(center, TOP));
		
		// Northeast (11, 9)
		assertEquals(b.getSpace(11, 9), b.find(center, TOP, RIGHT));
		
		// East (11, 10)
		assertEquals(b.getSpace(11, 10), b.find(center, RIGHT));
		
		// Southeast (11, 11)
		assertEquals(b.getSpace(11, 11), b.find(center, BOTTOM, RIGHT));
		
		// South (10, 11)
		assertEquals(b.getSpace(10, 11), b.find(center, BOTTOM));
		
		// Southwest (9, 11)
		assertEquals(b.getSpace(9, 11), b.find(center, BOTTOM, LEFT));
		
		// West (9, 10)
		assertEquals(b.getSpace(9, 10), b.find(center, LEFT));
	}
	
	@Test
	public void testFindPossibleMoveSpaces() {
		Board b = new Board(2);
		Player p1 = b.getPlayerFromNum(1);
		Player p2 = b.getPlayerFromNum(2);
		
		// Have player p1 place their single monomino piece in the top left corner
		b.placePiece(p1.getPiece(1, 0), 0, 0);
		
		// Assert that the only possible move space for p1 is the one southeast of [0,0]
		Set<Space> expected = new HashSet<Space>(Arrays.asList(b.getSpace(1, 1)));
		Set<Space> actual = b.findPossibleMoveSpaces(p1);
		assertEquals(expected, actual);
		
		// Have player p2 place their z-shape long piece in the bottom right corner
		b.placePiece(p2.getPiece(5, 2), 19, 17);
		
		// Ensure that the placed piece is occupying the expected five spaces
		assertTrue(b.getSpace(19, 17).isOccupied());
		assertTrue(b.getSpace(19, 18).isOccupied());
		assertTrue(b.getSpace(19, 19).isOccupied());
		assertTrue(b.getSpace(18, 17).isOccupied());
		assertTrue(b.getSpace(18, 16).isOccupied());
		
		// Assert that there are 3 possible move spaces of p2 around their placed piece
		expected = new HashSet<Space>(Arrays.asList(b.getSpace(19, 15), 
													b.getSpace(17, 15),
													b.getSpace(17, 18)));
		
		// The possible move spaces for player p2, there should only be three of them
		actual = b.findPossibleMoveSpaces(p2);		
		assertEquals(expected, actual);
		
		// Next, we will place player p1's z-shape plus pentomino at position [2, 2]
		// that is so the nomino northwest of the z-shape plus pentomino's "head" sits
		// in the space at position [1, 1] southeast of player p1's place single monomino
		b.placePiece(p1.getPiece(5, 1), 2, 2);
		
		// There are now 6 possible move spaces for player p1 on the board
		expected = new HashSet<Space>(Arrays.asList(b.getSpace(2, 0),
													b.getSpace(4, 1),
													b.getSpace(4, 3),
													b.getSpace(3, 4),
													b.getSpace(1, 4),
													b.getSpace(0, 3)));
		actual = b.findPossibleMoveSpaces(p1);
		assertEquals(expected, actual);
	}
}








