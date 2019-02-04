package santanjm.blokus;

import org.junit.Test;
import static org.junit.Assert.*;
import santanjm.blokus.*;

public class DirectionTest {
	public static final Direction TOP = Direction.TOP;
	public static final Direction BOTTOM = Direction.BOTTOM;
	public static final Direction RIGHT = Direction.RIGHT;
	public static final Direction LEFT = Direction.LEFT;
	
	@Test
	public void testDirectionConstruct() {
		assertEquals(TOP.ordinal(), 0);
		assertEquals(RIGHT.ordinal(), 1);
		assertEquals(BOTTOM.ordinal(), 2);
		assertEquals(LEFT.ordinal(), 3);
	}
	
	@Test
	public void testGetDir() {
		assertEquals(Direction.getDir(0), TOP);
		
		assertEquals(Direction.getDir(1), RIGHT);
		
		assertEquals(Direction.getDir(2), BOTTOM);
		
		assertEquals(Direction.getDir(3), LEFT);
	}
	
	@Test
	public void testOppVal() {
		assertEquals(TOP.getOppVal(), BOTTOM.ordinal());
		assertEquals(BOTTOM.getOppVal(), TOP.ordinal());
		assertEquals(RIGHT.getOppVal(), LEFT.ordinal());
		assertEquals(LEFT.getOppVal(), RIGHT.ordinal());
	}
	
	@Test
	public void testOpposite() {
		Direction dir = TOP;
		Direction opp = dir.opposite();
		assertEquals(BOTTOM, opp);
		
		opp = opp.opposite();
		assertEquals(TOP, opp);
		
		dir = RIGHT;
		opp = dir.opposite();
		assertEquals(LEFT, opp);
		
		opp = opp.opposite();
		assertEquals(RIGHT, opp);
	}
	
	@Test
	public void testRotate() {
		Direction dir = TOP;
		Direction rotated = dir.rotate(90);
		assertEquals(RIGHT, rotated);
		
		rotated = dir.rotate(180);
		assertEquals(BOTTOM, rotated);
		
		rotated = dir.rotate(270);
		assertEquals(LEFT, rotated);
	}
	
	@Test
	public void testRotateAll() {
		Direction[] dirs = new Direction[] {TOP, BOTTOM, RIGHT, LEFT, LEFT, RIGHT};
		
		Direction[] rotate90 = new Direction[] {RIGHT, LEFT, BOTTOM, TOP, TOP, BOTTOM};
		assertArrayEquals(rotate90, Direction.rotateAll(dirs, 90));
		
		Direction[] rotate180 = new Direction[] {BOTTOM, TOP, LEFT, RIGHT, RIGHT, LEFT};
		assertArrayEquals(rotate180, Direction.rotateAll(dirs, 180));
		
		Direction[] rotate270 = new Direction[] {LEFT, RIGHT, TOP, BOTTOM, BOTTOM, TOP};
		assertArrayEquals(rotate270, Direction.rotateAll(dirs, 270));
	}
	
	@Test
	public void testReflectX() {
		Direction[] dirs = new Direction[] {RIGHT, LEFT, BOTTOM, TOP, LEFT, TOP, BOTTOM, RIGHT};
		assertArrayEquals(new Direction[] {RIGHT, LEFT, TOP, BOTTOM, LEFT, BOTTOM, TOP, RIGHT},
				Direction.reflectx(dirs));
	}
	
	@Test
	public void testReflectY() {
		Direction[] dirs = new Direction[] {RIGHT, LEFT, BOTTOM, TOP, LEFT, TOP, BOTTOM, RIGHT};
		assertArrayEquals(new Direction[] {LEFT, RIGHT, BOTTOM, TOP, RIGHT, TOP, BOTTOM, LEFT},
				Direction.reflecty(dirs));
	}
	
	@Test
	public void testReversePath() {
		Direction[] path = new Direction[] {TOP, RIGHT, RIGHT, BOTTOM, BOTTOM, LEFT};
		
		Direction[] reverse = Direction.reversePath(path);
		Direction[] expected = new Direction[] {RIGHT, TOP, TOP, LEFT, LEFT, BOTTOM};
		
		assertArrayEquals(expected, reverse);
	}
}










