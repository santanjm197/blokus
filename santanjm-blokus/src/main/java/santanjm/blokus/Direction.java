package santanjm.blokus;

public enum Direction {
	TOP, RIGHT, BOTTOM, LEFT;
	
	/**
	 * Gets the value of the direction opposite the calling direction
	 * 
	 * @return the value of the direction opposite the calling direction
	 */
	public int getOppVal() {
		return opposite().ordinal();
	}
	
	/**
	 * Returns the opposite direction of the current direction
	 * 
	 * @return the opposite direction of the calling direction
	 */
	public Direction opposite() {
		switch(this) {
		case TOP   : return BOTTOM;
		case BOTTOM: return TOP;
		case RIGHT : return LEFT;
		case LEFT  : return RIGHT;
		default    : return null;
		}
	}
	
	/**
	 * Takes a degree value of 90, 180 or 270 and returns the direction corresponding
	 * to this direction but rotated clockwise by the specified amount
	 * 
	 * @param degrees a value of 90, 180 or 270 to rotate this direction by
	 * @return the direction that corresponds to this direction rotated by the given degrees
	 * @throws IllegalArgumentException if the given degrees is not 90, 180 or 270
	 */
	public Direction rotate(int degrees) throws IllegalArgumentException {
		if((degrees != 90) && (degrees != 180) && (degrees != 270)) {
			throw new IllegalArgumentException("Rotaion values must be 90, 180 or 270");
		}
		
		switch(degrees) {
		case 90 : return Direction.getDir((this.ordinal() + 1) % 4);
		case 180: return opposite();
		case 270: return Direction.getDir((this.ordinal() + 3) % 4);
		default : return null;
		}
	}
	
	/**
	 * Gets the ordinal value of a direction rotated by a given number of degrees (90, 180 or 270)
	 * 
	 * @param degrees the number of degrees to rotate the direction by (90, 180 or 270)
	 * @return the ordinal value of the rotated direction
	 */
	public int rotateVal(int degrees) {
		int rVal;
		try {
			rVal = rotate(degrees).ordinal();
		} catch(IllegalArgumentException iae) {
			System.out.println(iae.getMessage());
			return ordinal();
		}
		return rVal;
	}
	
	/**
	 * Given an array of directions and a degree of rotation (90, 180 or 270),
	 * this method will return an array defining the same directions but rotated
	 * by specified number of degrees
	 * 
	 * @param dirs    an array of directions
	 * @param degrees the degrees to rotate by (90, 180, 270)
	 * @return array containing the same directions given but rotated by the specified degrees
	 * @throws IllegalArgumentException if the given degrees is not 90, 180 or 270
	 */
	public static Direction[] rotateAll(Direction[] dirs, int degrees) 
				throws IllegalArgumentException {
		if((degrees != 90) && (degrees != 180) && (degrees != 270)) {
			throw new IllegalArgumentException("Rotaion values must be 90, 180 or 270");
		}
		
		Direction[] rotateDirs = new Direction[dirs.length];
		
		for(int i = 0; i < dirs.length; i++) {
			rotateDirs[i] = dirs[i].rotate(degrees);
		}
		
		return rotateDirs;
	}
	
	/**
	 * Method that takes an array of directions and returns an array with the
	 * same directions reflected across the x-axis
	 * 
	 * @param dirs an array of directions
	 * @return an array of directions that is dirs but reflected over the x-axis
	 */
	public static Direction[] reflectx(Direction[] dirs) {
		Direction[] reflectDirs = new Direction[dirs.length];
		
		for(int i = 0; i < dirs.length; i++) {
			if(dirs[i].equals(TOP) || dirs[i].equals(BOTTOM)) {
				reflectDirs[i] = dirs[i].opposite();
			} else {
				reflectDirs[i] = dirs[i];
			}
		}
		
		return reflectDirs;
	}
	
	/**
	 * Method that takes an array of directions and returns an array with the
	 * same directions reflected across the y-axis
	 * 
	 * @param dirs an array of directions
	 * @return an array of directions that is dirs but reflected over the y-axis
	 */
	public static Direction[] reflecty(Direction[] dirs) {
		Direction[] reflectDirs = new Direction[dirs.length];
		
		for(int i = 0; i < dirs.length; i++) {
			if(dirs[i].equals(RIGHT) || dirs[i].equals(LEFT)) {
				reflectDirs[i] = dirs[i].opposite();
			} else {
				reflectDirs[i] = dirs[i];
			}
		}
		
		return reflectDirs;
	}
	
	/**
	 * Given an array of directions that define a series of directions in a path,
	 * this method will return an array of the directions that comprise the same
	 * path reversed
	 * 
	 * @param dirs an array of directions defining a path
	 * @return an array of directions that define the reverse path
	 */
	public static Direction[] reversePath(Direction[] dirs) {
		Direction[] reversed = new Direction[dirs.length];
		
		for(int i = 0; i < dirs.length; i++) {
			reversed[i] = dirs[(dirs.length - 1) - i].opposite();
		}
		
		return reversed;
	}
	
	/**
	 * Gets the direction that has a given value, throwing an IllegalArgumentException
	 * if the value given is not between 0 and 3
	 * 
	 * @param value the value of one of the four directions
	 * @return the direction with the given value
	 */
	public static Direction getDir(int value) {
		if(value < 0 || value > 3) {
			throw new IllegalArgumentException("Direction must be 0-3");
		}
		
		switch(value) {
		case 0 : return TOP;
		case 1 : return RIGHT;
		case 2 : return BOTTOM;
		case 3 : return LEFT;
		default: return null;
		}
	}
}
