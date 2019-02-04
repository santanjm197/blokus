package santanjm.blokus;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;

import santanjm.blokus.*;

public class NominoTest {

	public static final Direction TOP = Direction.TOP;
	public static final Direction BOTTOM = Direction.BOTTOM;
	public static final Direction RIGHT = Direction.RIGHT;
	public static final Direction LEFT = Direction.LEFT;
	
	@Test
	public void testSetCoords() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		// Constructs n as a z-shaped pentomino
		n.attach(BOTTOM);
		n.attach(RIGHT, BOTTOM);
		n.attach(LEFT);
		n.attach(LEFT, LEFT);
		
		// Set the coordinates of n to column 5 row 5
		n.setCoords(5, 5);
		
		// Check the coordinates of each nomino attached to n
		assertArrayEquals(new int[] {5, 6}, n.getSide(BOTTOM).getCoords());
		assertArrayEquals(new int[] {6, 6}, n.search(BOTTOM, RIGHT).getCoords());
		assertArrayEquals(new int[] {4, 5}, n.getSide(LEFT).getCoords());
		assertArrayEquals(new int[] {3, 5}, n.search(LEFT, LEFT).getCoords());
	}
	
	@Test
	public void testMoveMonomino() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		
		// The single monomino belonging to player p
		Nomino mon = p.getPiece(1, 0);
		
		// Set mon's coordinates to the top left corner of the grid
		mon.setCoords(0, 0);
		
		// Attempt to move mon RIGHT
		mon.move(RIGHT);
		assertArrayEquals(new int[] {1, 0}, mon.getCoords());
		
		// Attempt to move mon LEFT
		mon.move(LEFT);
		assertArrayEquals(new int[] {0, 0}, mon.getCoords());
		
		// Attempt to move mon TOP (this should fail)
		mon.move(TOP);
		assertArrayEquals(new int[] {0, 0}, mon.getCoords());
		
		// Attempt to move mon BOTTOM
		mon.move(BOTTOM);
		assertArrayEquals(new int[] {0, 1}, mon.getCoords());
	}
	
	@Test
	public void testMoveDomino() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		
		// The domino belonging to player p
		Nomino dom = p.getPiece(2, 0);
		
		// Set dom's coordinates so it lays horizontally against the top left corner
		dom.setCoords(1, 0);
		
		// Attempt to move dom RIGHT
		dom.move(RIGHT);
		assertArrayEquals(new int[] {2, 0}, dom.getCoords());
		assertArrayEquals(new int[] {1, 0}, dom.getSide(LEFT).getCoords());
	}
	
	@Test
	public void testAttach1() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(TOP);
		assertNotNull(n.getSide(TOP));
		assertNotNull(n.getSide(TOP).getSide(BOTTOM));
		assertSame(n, n.getSide(TOP).getSide(BOTTOM));
		
		assertEquals(n.getClassification(), 2);
		assertEquals(n.getSide(TOP).getClassification(), 2);
	}
	
	@Test
	public void testAttach2() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(RIGHT, 2);
		assertNotNull(n.getSide(RIGHT));
		assertNotNull(n.search(RIGHT));
		assertEquals(n.getClassification(), 3);
	}
	
	@Test
	public void testAttach3() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);

		n.attach(TOP);
		n.attach(RIGHT, TOP);
		n.attach(LEFT);
		n.attach(BOTTOM, LEFT);
		n.attach(LEFT, LEFT, BOTTOM);
		
		assertNotNull(n.search(LEFT, BOTTOM, LEFT));
		assertEquals(n.getClassification(), 6);
		assertEquals(n.search(LEFT, BOTTOM, LEFT).getClassification(), 6);
	}
	
	@Test
	public void testAttach4() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n1 = new Nomino(p, b);
		Nomino n2 = new Nomino(p, b);

		n1.attach(RIGHT);
		
		n2.attach(LEFT);
		n2.attach(BOTTOM);
		
		n1.attach(LEFT, n2);
		assertNotNull(n1.search(LEFT, LEFT));
		assertNotNull(n1.search(LEFT, BOTTOM));
		assertSame(n1, n1.search(LEFT, LEFT, RIGHT, RIGHT));
		
		assertNotNull(n2.search(RIGHT, RIGHT));
		assertSame(n2, n2.search(RIGHT, RIGHT, LEFT, LEFT));
		
		assertEquals(n1.getClassification(), 5);
		assertEquals(n2.getClassification(), 5);
	}
	
	@Test
	public void testAttach5() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(LEFT);
		n.attach(BOTTOM);
		n.attach(LEFT, BOTTOM);
		n.search(LEFT).attachWithoutChangingClassification(BOTTOM, n.search(BOTTOM, LEFT));
		assertEquals(n.getClassification(), 4);
	}
	
	@Test
	public void testSearch1() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);

		n.attach(TOP);
		n.attach(TOP, TOP);
		n.attach(LEFT);
		
		assertNotNull(n.search(TOP));
		assertEquals(n.search(TOP), n.getSide(TOP).getSide(TOP));
		
		assertNotNull(n.search(LEFT));
		assertEquals(n.search(LEFT), n.getSide(LEFT));
		
		assertNull(n.search(RIGHT));
	}
	
	@Test
	public void testSearch2() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(TOP);
		n.getSide(TOP).attach(RIGHT);
		n.attach(LEFT);
		n.getSide(LEFT).attach(BOTTOM);
		
		assertNotNull(n.search(LEFT, BOTTOM, TOP, RIGHT, TOP, RIGHT));
		assertNull(n.search(TOP, RIGHT, RIGHT));
	}
	
	@Test
	public void testSearchAll1() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(RIGHT);
		n.attach(RIGHT, RIGHT);
		
		Nomino[] searched = n.searchAll(RIGHT);
		assertNotNull(searched);
		assertArrayEquals(searched, new Nomino[] {n.getSide(RIGHT), n.search(RIGHT, RIGHT)});
		
		searched = n.searchAll(TOP);
		assertNull(searched);
	}
	
	@Test
	public void testSearchAll2() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(RIGHT);
		n.attach(TOP, RIGHT);
		n.attach(TOP, RIGHT, TOP);
		n.attach(LEFT, RIGHT, TOP, TOP);
		
		Nomino[] searched = n.searchAll(RIGHT, TOP, TOP, LEFT);
		assertNotNull(searched);
		assertArrayEquals(searched, new Nomino[]{n.getSide(RIGHT),n.search(RIGHT, TOP),
												 n.search(RIGHT, TOP, TOP),
												 n.search(RIGHT, TOP, TOP, LEFT)});
		
		searched = n.searchAll(LEFT, TOP);
		assertNull(searched);
		
		searched = n.searchAll(RIGHT, TOP, TOP, TOP);
		assertNull(searched);
	}
	
	@Test
	public void testSearchAllUnique() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(LEFT);
		n.attach(TOP, LEFT);
		n.attach(BOTTOM);
		n.attach(BOTTOM, BOTTOM);
		
		assertArrayEquals(n.searchAllUnique(BOTTOM, BOTTOM),
						  new Nomino[] {n.getSide(BOTTOM), n.search(BOTTOM)});
		
		assertArrayEquals(n.searchAllUnique(LEFT, RIGHT, LEFT, RIGHT),
						  new Nomino[] {n.getSide(LEFT), n});
		
		Nomino[] allofn = n.searchAllUnique(n.walk());
		assertEquals(allofn.length, 5);
	}
	
	@Test
	public void testDuplicate1() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(TOP);
		n.attach(RIGHT);
		n.attach(RIGHT, RIGHT);
		
		Nomino duplicate = n.duplicate();
		assertNotNull(duplicate.getSide(TOP));
		assertNotNull(duplicate.getSide(RIGHT));
		assertNull(duplicate.search(RIGHT, RIGHT));
	}
	
	@Test
	public void testCopy() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(RIGHT);
		n.attach(TOP, RIGHT);
		n.attach(LEFT);
		n.attach(BOTTOM, LEFT);
		Nomino copy = n.copy();
		Direction[] expWalk1 = new Direction[] {RIGHT, TOP, BOTTOM, LEFT, LEFT, BOTTOM, TOP, RIGHT};
		assertArrayEquals(expWalk1, copy.walk());
		
		Nomino m = new Nomino(p, b);
		m.attach(RIGHT);
		m.attach(RIGHT, RIGHT);
		m.attach(RIGHT, RIGHT, RIGHT);
		m.attach(LEFT);
		copy = m.copy();
		Direction[] expWalk2 = new Direction[] {RIGHT, RIGHT, RIGHT, LEFT, LEFT, LEFT, LEFT, RIGHT};
		assertArrayEquals(expWalk2, copy.walk());
		
		Nomino square = p.getPiece(4, 0);
		copy = square.copy();
		Direction[] expWalk3 = new Direction[] {BOTTOM, LEFT, TOP, RIGHT};
		assertArrayEquals(expWalk3, copy.walk());
	}
	
	@Test
	public void testDetach() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(TOP);
		n.attach(RIGHT, TOP);
		
		Nomino top = n.search(TOP);
		n.detach(TOP);
		assertNotNull(top);
	}
	
	@Test
	public void testFind() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(BOTTOM);
		n.attach(BOTTOM, BOTTOM);
		n.attach(LEFT);
		n.attach(TOP, LEFT);
		
		Nomino topLeft = n.search(LEFT, TOP);
		
		Direction[] path = n.find(topLeft);
		
		assertArrayEquals(new Direction[] {LEFT, TOP}, path);
	}
	
	@Test
	public void testFindPath() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(BOTTOM);
		n.attach(BOTTOM, BOTTOM);
		n.attach(LEFT);
		n.attach(TOP, LEFT);
		
		Nomino topLeft = n.search(LEFT, TOP);
		
		Direction[] path = n.findPath(n.walk(), topLeft);
		
		assertArrayEquals(new Direction[] {BOTTOM, BOTTOM, TOP, TOP, LEFT, TOP}, path);
	}
	
	@Test
	public void testTravelToEnd() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		n.attach(TOP);
		n.attach(TOP, TOP);
		n.attach(TOP, TOP, TOP);
		n.attach(BOTTOM);
		
		assertEquals(n.travelToEnd(TOP), 3);
		assertEquals(n.travelToEnd(RIGHT), 0);
		assertEquals(n.travelToEnd(BOTTOM), 1);
		assertEquals(n.travelToEnd(LEFT), 0);
		
		assertEquals(n.getSide(TOP).travelToEnd(TOP), 2);
	}
	
	@Test
	public void testWalk() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		
		Direction[] walk = p.getPiece(1, 0).walk();
		
		walk = p.getPiece(2, 0).walk();
		
		walk = p.getPiece(3, 0).walk();
		walk = p.getPiece(3, 1).walk();
		
		walk = p.getPiece(4, 0).walk();
		walk = p.getPiece(4, 1).walk();
		walk = p.getPiece(4, 2).walk();
		walk = p.getPiece(4, 3).walk();
		walk = p.getPiece(4, 4).walk();
		
		walk = p.getPiece(5, 0).walk();
		walk = p.getPiece(5, 1).walk();
		walk = p.getPiece(5, 2).walk();
		walk = p.getPiece(5, 3).walk();
		walk = p.getPiece(5, 4).walk();
		walk = p.getPiece(5, 5).walk();
		walk = p.getPiece(5, 6).walk();
		walk = p.getPiece(5, 7).walk();
		walk = p.getPiece(5, 8).walk();
		walk = p.getPiece(5, 9).walk();
		walk = p.getPiece(5, 10).walk();
		walk = p.getPiece(5, 11).walk();
	}
	
	@Test
	public void testSquarePlusWalk() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		
		// The square-shaped tetromino
		Nomino square = p.getPiece(4, 0);
		
		// Attach an extra square left of the bottom left of the square
		square.attach(LEFT, BOTTOM, LEFT);
		
		// Check the walk of this shape
		assertArrayEquals(
				new Direction[] {BOTTOM, LEFT, TOP, RIGHT, BOTTOM, LEFT, LEFT, RIGHT, TOP, RIGHT},
				square.walk());
	}
	
	@Test
	public void testRotate() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		// Build a z-shaped pentomino
		n.attach(RIGHT);
		n.attach(RIGHT, RIGHT);
		n.attach(BOTTOM);
		n.attach(LEFT, BOTTOM);
		
		// First test that this nomino's walk is what we expect
		assertArrayEquals(new Direction[] {RIGHT, RIGHT, LEFT, LEFT, BOTTOM, LEFT, RIGHT, TOP},
				n.walk());
		
		// Rotate n by 90 degrees and check that the new walk is correct
		n.rotate();
		assertArrayEquals(new Direction[] {BOTTOM, BOTTOM, TOP, TOP, LEFT, TOP, BOTTOM, RIGHT},
				n.walk());
		
		// Rotate n by another 90 degrees (180 degrees total) and check the new walk
		n.rotate();
		assertArrayEquals(new Direction[] {TOP, RIGHT, LEFT, BOTTOM, LEFT, LEFT, RIGHT, RIGHT},
				n.walk());
		
		// Rotate n by another 90 degrees (270 degrees total) and check the new walk
		n.rotate();
		assertArrayEquals(new Direction[] {TOP, TOP, BOTTOM, BOTTOM, RIGHT, BOTTOM, TOP, LEFT},
				n.walk());
		
		// Finally, rotate n by another 90 degrees (360 degrees total) and check the walk
		n.rotate();
		assertArrayEquals(new Direction[] {RIGHT, RIGHT, LEFT, LEFT, BOTTOM, LEFT, RIGHT, TOP},
				n.walk());
	}
	
	@Test
	public void testRotateCross() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		
		// The cross-shaped pentomino
		Nomino cross = p.getPiece(5, 7);
		
		// First test that this nomino's walk is what we expect
		assertArrayEquals(new Direction[] {TOP, BOTTOM, RIGHT, LEFT, BOTTOM, TOP, LEFT, RIGHT},
				cross.walk());
		
		// Rotate cross by 90 degrees and make sure that its walk is unchanged
		cross.rotate();
		assertArrayEquals(new Direction[] {TOP, BOTTOM, RIGHT, LEFT, BOTTOM, TOP, LEFT, RIGHT},
				cross.walk());
	}
	
	@Test
	public void testRotateSquare() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		
		// The square-shaped tetromino
		Nomino square = p.getPiece(4, 0);
		
		// First test that this nomino's walk is what we expect
		assertArrayEquals(new Direction[] {BOTTOM, LEFT, TOP, RIGHT}, square.walk());
		
		// Attempt to rotate the square and then determine that it is unchanged
		square.rotate();
		assertArrayEquals(new Direction[] {BOTTOM, LEFT, TOP, RIGHT}, square.walk());
	}
	
	@Test
	public void testReflect() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		
		// The z-shape-plus pentomino made from the z-shape tetromino with an extra
		// nomino attached to the bottom of the "head"
		Nomino zShapePlus = p.getPiece(5, 1);
		
		// First test that this nomino's walk is what we expect
		assertArrayEquals(new Direction[] {RIGHT, LEFT, BOTTOM, TOP, LEFT, TOP, BOTTOM, RIGHT},
				zShapePlus.walk());
		
		// Reflect zShapePlus over the x-axis and check the walk
		zShapePlus.reflect(0);
		assertArrayEquals(new Direction[] {TOP, BOTTOM, RIGHT, LEFT, LEFT, BOTTOM, TOP, RIGHT},
				zShapePlus.walk());
		
		// Reflect zShapePlus back over the x-axis and check the walk
		zShapePlus.reflect(0);
		assertArrayEquals(new Direction[] {RIGHT, LEFT, BOTTOM, TOP, LEFT, TOP, BOTTOM, RIGHT},
				zShapePlus.walk());
		
		// Now reflect zShapePlus over the y-axis and check the walk
		zShapePlus.reflect(1);
		assertArrayEquals(new Direction[] {RIGHT, TOP, BOTTOM, LEFT, BOTTOM, TOP, LEFT, RIGHT},
				zShapePlus.walk());
	}
	
	@Test
	public void testReflectSquarePlusRightLeft() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		
		// The square-plus nomino (extra square attached right of top right corner)
		Nomino squarePlus = p.getPiece(5, 0).copy();
		
		// Check the initial walk
		assertArrayEquals(new Direction[] {RIGHT, LEFT, BOTTOM, LEFT, TOP, RIGHT},
				squarePlus.walk());
		
		// Reflect over the x-axis and check the new walk
		squarePlus.reflect(0);
		assertArrayEquals(new Direction[] {BOTTOM, RIGHT, LEFT, LEFT, TOP, RIGHT},
				squarePlus.walk());
		
		// Reflect over the y-axis and check the new walk
		squarePlus.reflect(1);
		assertArrayEquals(
				new Direction[] {BOTTOM, LEFT, TOP, RIGHT, BOTTOM, LEFT, LEFT, RIGHT, TOP, RIGHT},
				squarePlus.walk());
		
		// Reflect over x-axis again and check the walk
		squarePlus.reflect(0);
		assertArrayEquals(new Direction[] {BOTTOM, LEFT, TOP, LEFT, RIGHT, RIGHT},
				squarePlus.walk());
		
		// Reflect over y-axis once more to return to the original configuration
		squarePlus.reflect(1);
		assertArrayEquals(new Direction[] {RIGHT, LEFT, BOTTOM, LEFT, TOP, RIGHT},
				squarePlus.walk());
	}
	
	@Test
	public void testReflectSquarePlusTopBottom() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		
		// We will create the squarePlus by copying the square and then attaching
		// a new nomino to the top of the top right corner
		Nomino squarePlus = p.getPiece(4, 0).copy();
		squarePlus.attach(TOP);
		
		// Check the unchanged walk
		assertArrayEquals(new Direction[] {TOP, BOTTOM, BOTTOM, LEFT, TOP, RIGHT},
				squarePlus.walk());
		
		// Reflect over the x-axis
		squarePlus.reflect(0);
		assertArrayEquals(new Direction[] {BOTTOM, BOTTOM, TOP, LEFT, TOP, RIGHT},
				squarePlus.walk());
		
		// Reflect over the y-axis
		squarePlus.reflect(1);
		assertArrayEquals(
				new Direction[] {BOTTOM, LEFT, TOP, RIGHT, BOTTOM, LEFT, BOTTOM, TOP, TOP, RIGHT},
				squarePlus.walk());
		
		// Reflect over the x-axis again
		squarePlus.reflect(0);
		assertArrayEquals(new Direction[] {BOTTOM, LEFT, TOP, TOP, BOTTOM, RIGHT},
				squarePlus.walk());
	}
	
	@Test
	public void testReflectSquarePlus1() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		
		// The square-plus nomino (extra square attached right of top right corner)
		Nomino squarePlus = p.getPiece(5, 0).copy();
		
		// Check the initial walk
		assertArrayEquals(new Direction[] {RIGHT, LEFT, BOTTOM, LEFT, TOP, RIGHT},
				squarePlus.walk());
		
		// Rotate 90 degrees clockwise and check the walk
		squarePlus.rotate();
		assertArrayEquals(new Direction[] {BOTTOM, BOTTOM, TOP, LEFT, TOP, RIGHT},
				squarePlus.walk());
		
		// Rotate another 90 degrees (180 degrees total) clockwise
		squarePlus.rotate();
		assertArrayEquals(
				new Direction[] {BOTTOM, LEFT, TOP, RIGHT, BOTTOM, LEFT, LEFT, RIGHT, TOP, RIGHT},
				squarePlus.walk());
		
		// Rotate another 90 degrees (270 degrees total) clockwise
		squarePlus.rotate();
		assertArrayEquals(new Direction[] {BOTTOM, LEFT, TOP, TOP, BOTTOM, RIGHT},
				squarePlus.walk());
		
		// Finally, rotate another 90 degrees (360 degrees total) clockwise
		squarePlus.rotate();
		assertArrayEquals(new Direction[] {RIGHT, LEFT, BOTTOM, LEFT, TOP, RIGHT},
				squarePlus.walk());
	}
	
	@Test
	public void testNullSides() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		assertEquals(n.numNullSides(), 4);
		
		n.attach(RIGHT);
		assertEquals(n.numNullSides(), 3);
		
		n.attach(TOP);
		assertEquals(n.numNullSides(), 2);
		
		n.attach(LEFT);
		assertEquals(n.numNullSides(), 1);
		
		n.attach(BOTTOM);
		assertEquals(n.numNullSides(), 0);
	}
	
	@Test
	public void testNotNullSides() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		assertNull(n.notNullSides());
		
		n.attach(TOP);
		assertEquals(n.notNullSides().length, 1);
		assertEquals(n.notNullSides()[0], TOP);
		
		n.attach(RIGHT);
		assertEquals(n.notNullSides().length, 2);
		assertEquals(n.notNullSides()[1], RIGHT);
		
		n.attach(BOTTOM);
		assertEquals(n.notNullSides().length, 3);
		assertEquals(n.notNullSides()[2], BOTTOM);
		
		n.attach(LEFT);
		assertEquals(n.notNullSides().length, 4);
		assertEquals(n.notNullSides()[3], LEFT);
	}
	
	@Test
	public void testToString() {
		Board b = new Board(2);
		Player p = b.getPlayerFromNum(1);
		Nomino n = new Nomino(p, b);
		
		System.out.println(n + "\n");
		
		n.attach(LEFT);
		n.attach(RIGHT, 3);
		System.out.println(n + "\n");
		
		n.attach(TOP, 2);
		System.out.println(n + "\n");
		
		n.attach(BOTTOM);
		System.out.println(n + "\n");
	}
}





















