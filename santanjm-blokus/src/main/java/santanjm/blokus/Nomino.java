package santanjm.blokus;

import java.util.*;
import santanjm.blokus.*;

public class Nomino {
	// The nominos attached to the four sides of this nomino
	// 0 - top
	// 1 - right
	// 2 - bottom
	// 3 - left
	private Nomino[] sides = new Nomino[4];
	
	// The player that owns this nomino
	private Player owner;
	
	// The board on which this nomino is/will be placed
	private Board board;
	
	// The column and row on the board on which this nomino has been placed
	private int col, row;
	
	// Flag that tells whether or not this nomino has been placed on the board
	private boolean placed = false;
	
	// The number of nominos there will be total in the polynomino that
	// this nomino is a piece of (this will always be between 1 and 5)
	private int classification = 1;
	
	/**
	 * Construct a new Nomino owned by the given player
	 * 
	 * @param owningPlayer the player that owns this nomino
	 * @param gameBoard    the board on which the game will be played
	 */
	public Nomino(Player owningPlayer, Board gameBoard) {
		owner = owningPlayer;
		board = gameBoard;
	}
	
	/**
	 * Gets the player object that owns this nomino
	 * 
	 * @return the player that owns this nomino
	 */
	public Player getOwner() {
		return owner;
	}
	
	/**
	 * Gets the number of the player that owns this nomino
	 * 
	 * @return the player number of the owner of the calling nomino instance
	 */
	public int getOwnerNum() {
		return owner.getPlayerNum();
	}
	
	/**
	 * Gets the coordinates of the nomino on the board as an array: [column, row]
	 * 
	 * @return the coordinates of this nomino on the board as an array
	 */
	public int[] getCoords() {
		return new int[] {col, row};
	}
	
	/**
	 * Sets the coordinates of the nomino
	 * 
	 * @param col the column of the nomino
	 * @param row the row of the nomino
	 */
	public boolean setCoords(int col, int row) {
		if((col < 0 || board.getSize() <= col) || (row < 0 || board.getSize() <= row))
			return false;
		
		this.col = col;
		this.row = row;
		
		if(classification > 1) return updateCoords();
		
		return true;
	}
	
	/**
	 * Updates the coordinates of every nomino attached to the calling nomino relative
	 * to the coordinates of the calling nomino
	 */
	private boolean updateCoords() {
		// The current nomino
		Nomino curr = this;
		
		// Next, we create a stack ArrayDeque using this nomino's walk
		Deque<Direction> steps = new ArrayDeque<Direction>(Arrays.asList(walk()));
		
		// A set of the nominos we have visited during the update
		Set<Nomino> visited = new HashSet<Nomino>(Arrays.asList(this));
		
		// Flag that is set to true if any of the nominos end up outside the grid
		boolean isOut = false;
		
		while(! steps.isEmpty()) {
			Direction step = steps.pop();
			
			if(! visited.contains(curr.sides[step.ordinal()])) {
				switch(step) {
				case TOP:
					curr.sides[step.ordinal()].col = curr.col;
					curr.sides[step.ordinal()].row = curr.row-1;
					break;
				case RIGHT:
					curr.sides[step.ordinal()].col = curr.col+1;
					curr.sides[step.ordinal()].row = curr.row;
					break;
				case BOTTOM:
					curr.sides[step.ordinal()].col = curr.col;
					curr.sides[step.ordinal()].row = curr.row+1;
					break;
				case LEFT:
					curr.sides[step.ordinal()].col = curr.col-1;
					curr.sides[step.ordinal()].row = curr.row;
					break;
				}
			}
			curr = curr.sides[step.ordinal()];
			visited.add(curr);
			if((curr.col < 0 || board.getSize() <= curr.col) || 
					(curr.row < 0 || board.getSize() <= curr.row))
				isOut = true;
		}
		
		// If any of the nominos ended up outside the grid, we return false to indicate it
		if(isOut) return false;
		
		return true;
	}
	
	/**
	 * Method that shifts this nomino onto the grid, assuming it was not entirely
	 * within the grid to start with
	 */
	public void shiftIntoGrid() {
		// Every nomino attached to this nomino
		Nomino[] attached = searchAllUnique(walk());
		
		// We keep track of the smallest and largest column and row of each attached nomino
		int minCol = 0, minRow = 0;
		int maxCol = board.getSize()-1, maxRow = board.getSize()-1;
		
		// Iterate through the attached nominos and update the min and max values when needed
		for(Nomino n : attached) {
			int[] coords = n.getCoords();
			if(coords[0] < minCol) minCol = coords[0];
			if(coords[0] > maxCol) maxCol = coords[0];
			if(coords[1] < minRow) minRow = coords[1];
			if(coords[1] > maxRow) maxRow = coords[1];
		}
		
		// Now move the piece onto the grid
		if(minCol < 0)                move(Math.abs(minCol), Direction.RIGHT);
		if(maxCol >= board.getSize()) move(maxCol - (board.getSize()-1), Direction.LEFT);
		if(minRow < 0)                move(Math.abs(minRow), Direction.BOTTOM);
		if(maxRow >= board.getSize()) move(maxRow - (board.getSize()-1), Direction.TOP);
	}
	
	/**
	 * Moves this nomino exactly one space in a given direction
	 * 
	 * @param dir the direction in which to move this nomino
	 */
	public void move(Direction dir) {
		// The current coordinates of this nomino
		int oldCol = col;
		int oldRow = row;
		
		switch(dir) {
		case TOP:
			if(setCoords(col, row-1) == false) setCoords(oldCol, oldRow);
			break;
		case RIGHT:
			if(setCoords(col+1, row) == false) setCoords(oldCol, oldRow);
			break;
		case BOTTOM:
			if(setCoords(col, row+1) == false) setCoords(oldCol, oldRow);
			break;
		case LEFT:
			if(setCoords(col-1, row) == false) setCoords(oldCol, oldRow);
			break;
		}
	}
	
	/**
	 * Moves the nomino a specified number of times in a given direction, note that
	 * this method does not care if the coordinates of any of the nominos attached to
	 * it end up outside the grid.  This method is used in shifting pieces back into the
	 * grid when they end up outside due to rotation or reflection
	 * 
	 * @param steps the number of times to move the nomino
	 * @param dir   the direction in which to move the nomino
	 */
	private void move(int steps, Direction dir) {
		for(int i = 0; i < steps; i++) {
			switch(dir) {
			case TOP:
				setCoords(col, row-1);
				break;
			case RIGHT:
				setCoords(col+1, row);
				break;
			case BOTTOM:
				setCoords(col, row+1);
				break;
			case LEFT:
				setCoords(col-1, row);
				break;
			}
		}
	}
	
	/**
	 * Sets the placed field for this Nomino to true, indicating it has been
	 * placed on the board
	 */
	public void place() {
		// If the nomino is a single monomino, place it and return
		if(classification == 1) {
			placed = true;
			return;
		}
		
		// All nominos somehow attached to this nomino
		Nomino[] attached = searchAllUnique(walk());
		
		// For each attached nomino, set the placed field to true
		for(Nomino att : attached) att.placed = true;
	}
	
	/**
	 * (TESTING PURPOSES ONLY)
	 * Sets the placed field for this nomino to false, indicating it has been
	 * taken off of the board
	 */
	public void deplace() {
		// If the nomino is a single monomino, deplace it and return
		if(classification == 1) {
			placed = false;
			return;
		}
		
		// All nominos somehow attached to this nomino
		Nomino[] attached = searchAllUnique(walk());
		
		// For each attached nomino, set the placed field to false
		for(Nomino att : attached) att.placed = false;
	}
	
	/**
	 * Gets the value of this Nomino's placed field, telling whether or not
	 * this particular Nomino has been placed on the board yet or not
	 * 
	 * @return the value of the placed field
	 */
	public boolean isPlaced() {
		return this.placed;
	}
	
	/**
	 * Attaches a new nomino to the designated side of this nomino
	 * 
	 * @param dir the direction to attach to
	 */
	public void attach(Direction dir) {
		// If the side in the indicated direction is null, then attach a new monomino
		if(sides[dir.ordinal()] == null) {
			sides[dir.ordinal()] = new Nomino(owner, board);
			
			// We also must create a reference on this new monomino to this monomino
			// on the opposite side
			sides[dir.ordinal()].sides[dir.getOppVal()] = this;
			
			// The classification of this nomino (AND all those attached to it)
			// has thus just gone up by 1
			classification++;
			updateClassification();
		}
	}
	
	/**
	 * Attaches a specified number of nominos in a specified direction from
	 * this instance.  Note that it will search for the nomino furthest in the
	 * given direction and then attach to that nomino
	 * 
	 * @param dir   the direction in which to attach the new nominos
	 * @param times the number of nominos to attach
	 */
	public void attach(Direction dir, int times) {
		// First search for the nomino furthest in the given direction
		Nomino curr = search(dir);
		
		// It is possible that there was no nomino in that direction,
		// in which case we attach directly to this nomino
		if(curr == null) {
			curr = this;
		}
		for(int i = 0; i < times; i++) {
			curr.attach(dir);
			curr = curr.getSide(dir);
		}
	}
	
	/**
	 * Method which gives the ability to attach a new nomino to any nomino along
	 * the reference chains that start at this nomino 
	 * 
	 * @param side the side of the nomino to attach the new nomino to
	 * @param dirs the directions to search from this nomino to find the nomino to attach to
	 */
	public void attach(Direction side, Direction... dirs) {
		// First we must attempt to find the nomino that we will attach to
		Nomino attachTo = search(dirs);
		
		// Assuming a non-null nomino was found in our search, then if the side
		// of that nomino we want to attach a new nomino to is null, then we go ahead
		if(attachTo != null && attachTo.getSide(side) == null) {
			attachTo.attach(side);
			updateClassification();
		}
	}

	/**
	 * Attaches an existing nomino instance to this nomino instance in the
	 * given direction.  This attachment requires both the caller to have
	 * the side in the given direction null already and the nomino that
	 * will be attached to be in null in the opposite direction
	 * 
	 * @param dir the direction (from the caller) to attach the nomino to
	 * @param n   a nomino instance
	 */
	public void attach(Direction dir, Nomino n) {
		if(sides[dir.ordinal()] == null && n.sides[dir.getOppVal()] == null) {
			sides[dir.ordinal()] = n;
			sides[dir.ordinal()].sides[dir.getOppVal()] = this;
			
			classification = classification + n.classification;
			updateClassification();
		}
	}
	
	/**
	 * Attaches an existing nomino instance to this nomino instance in the
	 * given direction in the exact same manner as attach, the only difference
	 * is that this attachment does not sum the cassifications like the other one.
	 * This is used when constructing nominos like the square one which requires
	 * a single nomino to be attached southwest of the "head" nomino
	 * 
	 * @param dir the direction relative to the calling nomino to attach to
	 * @param n   a nomino instance
	 */
	public void attachWithoutChangingClassification(Direction dir, Nomino n) {
		if(sides[dir.ordinal()] == null && n.sides[dir.getOppVal()] == null) {
			sides[dir.ordinal()] = n;
			sides[dir.ordinal()].sides[dir.getOppVal()] = this;
			
			updateClassification();
		}
	}
	
	/**
	 * Detaches the nomino from the given side of this nomino if there is one there
	 * 
	 * @param dir the side of this nomino to detach from
	 * @throws NullPointerException if the side given is already null there is nothing to detach
	 */
	public void detach(Direction dir) throws NullPointerException {
		if(sides[dir.ordinal()] != null) {
			sides[dir.ordinal()] = null;
		} else {
			throw new NullPointerException("The side given is already null");
		}
	}
	
	/**
	 * Gets the nomino (or null reference) attached to a specified side of this nomino
	 * 
	 * @param dir the side to return
	 * @return the nomino at sides[dir.ordinal()], which may be null
	 */
	public Nomino getSide(Direction dir) {
		return sides[dir.ordinal()];
	}
	
	/**
	 * Gets the number of sides of the calling nomino instance that are currently null
	 * 
	 * @return the number of null sides this nomino currently has
	 */
	public int numNullSides() {
		int total = 0;
		
		for(Direction dir : Direction.values()) {
			if(sides[dir.ordinal()] == null) {
				total++;
			}
		}
		return total;
	}
	
	/**
	 * Method that returns an array of the directions corresponding to the
	 * sides of the calling instance that are currently null
	 * 
	 * @return an array of the directions from the calling nomino that are null
	 */
	public Direction[] nullSides() {
		// If none of the sides are null, return null right away
		if(numNullSides() == 0) return null;
		
		// Set of the sides of the nomino that are null
		Set<Direction> nullDirs = new HashSet<Direction>();
		for(Direction dir : Direction.values()) {
			if(sides[dir.ordinal()] == null)  nullDirs.add(dir);
		}
		
		// Turn the set into an array and sort it
		Direction[] nullDirsArray = nullDirs.toArray(new Direction[nullDirs.size()]);
		Arrays.sort(nullDirsArray);
		
		return nullDirsArray;
	}
	
	/**
	 * Method that returns an array of the directions corresponding to the 
	 * sides of the calling instance that are currently not null
	 * 
	 * @return an array of the directions from the calling nomino that are not null
	 */
	public Direction[] notNullSides() {
		// If all 4 sides are null then there is no direction to return
		if(numNullSides() == 4) return null;
		
		// The set of directions around this nomino that are not currently null
		Set<Direction> notNullDirs = new HashSet<Direction>();
		for(Direction dir : Direction.values()) {
			if(sides[dir.ordinal()] != null) notNullDirs.add(dir);
		}
		
		// Now, we turn that set into an array and sort it
		Direction[] notNullDirsArray = notNullDirs.toArray(new Direction[notNullDirs.size()]);
		Arrays.sort(notNullDirsArray);
		
		return notNullDirsArray;
	}
	
	/**
	 * Getter for the classification of a nomino
	 * 
	 * @return the classification of this nomino
	 */
	public int getClassification() {
		return classification;
	}
	
	/**
	 * Updates the classification of the nomino and all of the nominos that are
	 * attached to it
	 */
	private void updateClassification() {
		for(int i = 0; i < 4; i++) {
			if(sides[i] != null && sides[i].classification != classification) {
				sides[i].classification = classification;
				sides[i].updateClassification();
			}
		}
	}
	
	/**
	 * Gets the index of this Nomino in its owning Player's pieces map
	 * 
	 * @return the index corresponding to this Nomino among all pieces of its classification
	 */
	public int getIndex() {
		// The map of pieces owned by the owner of this nomino
		Map<Integer, Nomino[]> ownerPieces = owner.getPieces();
		
		// The value of the map at the same classification as this nomino
		Nomino[] sameClass = ownerPieces.get(classification);
		
		// The index in the array which corresponds to this nomino
		int index = -1;
		
		for(int i = 0; i < sameClass.length; i++) {
			if(sameClass[i].equals(this)) {
				index = i;
				break;
			}
		}	
		return index;
	}

	/**
	 * Method for searching from this nomino into a given direction continuously
	 * until a null is hit and returning the final nomino reached before the null.
	 * 
	 * @param dir the direction in which to search from this nomino
	 * @return either the last nomino in the given direction or null if there are none
	 */
	public Nomino search(Direction dir) {
		// If there are no nominos in the given direction then return null
		if(travelToEnd(dir) == 0) {
			return null;
		}
		
		// Create an array holding the number of times to travel in the given direction
		// to reach the last not null nomino
		Direction[] toEnd = new Direction[travelToEnd(dir)];
		Arrays.fill(toEnd, dir);
		
		return search(toEnd);
	}	
	
	/**
	 * Method for iteravely searching through the nominos adjacent to this nomino
	 * from a list of directions and returning what is found
	 * 
	 * @param dirs a list of directions, each between 0 and 3, to search by
	 * @return either the nomino found at the end of the search or null
	 */
	public Nomino search(Direction... dirs) {
		// The first nomino to look at will be the one in the direction of dirs[0] 
		Nomino curr = sides[dirs[0].ordinal()];
		
		// If that side is null then we may as well return since the rest of the
		// search will most certainly be fruitless
		if(curr == null) {
			return null;
		}
		
		// Otherwise we want to iterate through the remaining directions until we either
		// hit a null or reach the end of dirs, whichever comes first
		for(int i = 1; i < dirs.length; i++) {
			curr = curr.getSide(dirs[i]);
			if(curr == null) {
				return null;
			}
		}
		return curr;
	}
	
	/**
	 * Method that searches in the given direction and returns an array of the
	 * nominos that are found in that direction (not including the starting point)
	 * in the order in which they are reached
	 * 
	 * @param dir the direction in which to search
	 * @return an array of the nominos found in the given direction or null if there are none
	 */
	public Nomino[] searchAll(Direction dir) {
		// If there are not any nominos in the given direction then do not bother searching
		if(travelToEnd(dir) == 0) {
			return null;
		}
		
		Nomino[] result = new Nomino[travelToEnd(dir)];
		Nomino curr = this;
		
		for(int i = 0; i < travelToEnd(dir); i++) {
			result[i] = curr.sides[dir.ordinal()];
			curr = result[i];
		}
		
		return result;
	}
	
	/**
	 * Method that searches in a series of directions and returns an array of each
	 * of the nominos it reaches along the way (not including the starting point)
	 * in the order in which they were reached.  If a null is encountered anywhere
	 * during the search, the method will return null
	 * 
	 * @param dirs a series of directions in which to search
	 * @return an array containing every nomino found during the search or null
	 */
	public Nomino[] searchAll(Direction... dirs) {
		// If the first direction given is bad, then there is no reason to continue
		if(sides[dirs[0].ordinal()] == null) {
			return null;
		}
		
		Nomino[] result = new Nomino[dirs.length];
		Nomino curr = this;
		
		for(int i = 0; i < dirs.length; i++) {
			// If we hit a null at any point, then the search was bad and so we stop
			if(curr.sides[dirs[i].ordinal()] == null) {
				return null;
			}
			result[i] = curr.sides[dirs[i].ordinal()];
			curr = result[i];
		}
		
		return result;
	}
	
	/**
	 * Method that searches in a series of directions and returns an array of the
	 * unique nominos encountered along the way (excluding the starting point)
	 * 
	 * @param dirs a series of directions to search in from this nomino
	 * @return an array of the unique nominos found in the search or null
	 */
	public Nomino[] searchAllUnique(Direction... dirs) {
		// If the nomino is a single monomino, simply return an array with itself
		if(classification == 1) return new Nomino[] {this};
		
		// If the first direction given is bad, then there is no reason to continue
		if(sides[dirs[0].ordinal()] == null) {
			return null;
		}
		
		List<Nomino> result = new ArrayList<Nomino>();
		Nomino curr = this;
		
		for(int i = 0; i < dirs.length; i++) {
			// If we hit a null at any point, then the search was bad and so we stop
			if(curr.sides[dirs[i].ordinal()] == null) {
				return null;
			}
			
			// We only add the next nomino to the result if it is not already in the list
			if(! result.contains(curr.sides[dirs[i].ordinal()])) {
				result.add(curr.sides[dirs[i].ordinal()]);
			}
			
			// Regardless of whether or not we added the next nomino to the list,
			// we need to change the current nomino to the next one in the search
			curr = curr.sides[dirs[i].ordinal()];
		}
		
		return result.toArray(new Nomino[result.size()]);
	}
	
	/**
	 * Method that checks whether or not a nomino is attached somehow to this nomino
	 * 
	 * @param n a nomino instance
	 * @return true if n is attach to this nomino and false otherwise
	 */
	public boolean contains(Nomino n) {
		// Get a set of all of the nominos somehow connected to this nomino
		Set<Nomino> attached = new HashSet<Nomino>(Arrays.asList(searchAllUnique(walk())));
		if(attached.contains(n)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Method that finds a direct route from this nomino to another nomino that it
	 * is attached to in some way.  If the given nomino is not attached to this nomino
	 * then the method will return null
	 * 
	 * @param n a nomino instance
	 * @return an array of the steps to take to get from this nomino directly to n, or null
	 */
	public Direction[] find(Nomino n) {
		// First we check to make sure that n is even attached to this nomino
		if(! contains(n)) {
			return null;
		}
		
		// The (not necessarily direct) path from this nomino to n
		Direction[] path = findPath(walk(), n);
		
		// The direct path from this nomino to n
		return Direction.reversePath(n.findPath(Direction.reversePath(path), this));
	}
	
	/**
	 * Method for finding a (not necessarily direct) path from this nomino to another
	 * by following a given array of steps.  If the route given leads to n, then the
	 * method will return the array of steps given but truncated so that it stops at n
	 * 
	 * @param dirs an array of directions corresponding to a path to follow
	 * @param n    a nomino instance
	 * @return array of the directions to take in the path from this nomino to n, or null
	 */
	public Direction[] findPath(Direction[] dirs, Nomino n) {
		// First we check to make sure that n is even attached to this nomino
		if(! contains(n)) {
			return null;
		}
		
		// The nomino we are currently at
		Nomino curr = this;
		
		// We follow the directions given one step at a time
		for(int i = 0; i < dirs.length; i++) {
			// If the directions led us to a null then there is no reason to keep looking
			if(curr.sides[dirs[i].ordinal()] == null) {
				return null;
				
			// If the current step lands on the nomino we are looking for, then we can
			// stop.  Truncate the path and return it
			} else if(curr.sides[dirs[i].ordinal()].equals(n)) {
				return Arrays.copyOf(dirs, i+1);
			}
			
			// If we have neither reached a null nor found our target, then take
			// the next step and continue searching
			curr = curr.sides[dirs[i].ordinal()];
		}
		
		return null;
	}
	
	/**
	 * Method that returns the number of nominos there are in a straight line
	 * from this nomino in a given direction
	 * 
	 * @param dir the direction from this nomino to search in
	 * @return an integer representing the number of nominos in the given direction
	 */
	public int travelToEnd(Direction dir) {
		// The number of nominos from this nomino there are in the given direction
		int result = 0;
		
		// Start by trying to look in the given direction from this nomino
		Nomino curr = sides[dir.ordinal()];
		
		// If there is not even a single nomino in the given direction from
		// this nomino, then we know there are 0 nominos in the given direction
		if(curr == null) {
			return result;
		}
		
		// There was at least 1 nomino in the given direction, so keep going
		// in that direction until we hit a null, adding 1 to the result each time
		while(curr != null) {
			result++;
			curr = curr.sides[dir.ordinal()];
		}
		
		return result;
	}
	
	/**
	 * Method that returns an array of the steps required to travel from this nomino
	 * to every other nomino in the shape and return back to this nomino
	 * 
	 * @return an array of the directions required to walk around the graph and return to start
	 */
	public Direction[] walk() {
		// If the nomino has no attached nominos then searching is pointless
		if(numNullSides() == 4) return null;
		
		// The list of steps to take to complete the walk
		List<Direction> steps = new ArrayList<Direction>();
		
		// The nominos we have already visited in the walk
		Set<Nomino> visited = new HashSet<Nomino>(Arrays.asList(this));
		
		// The nomino we are currently at
		Nomino curr = sides[notNullSides()[0].ordinal()];
		visited.add(curr);
		steps.add(notNullSides()[0]);
		
		loop:
			while(curr != this || visited.size() != classification) {
				// The directions of those sides around the current nomino that are not null
				Direction[] notNullDirs = curr.notNullSides();
				Nomino[] notNull = new Nomino[notNullDirs.length];
				for(int i = 0; i < notNull.length; i++) {
					notNull[i] = curr.sides[notNullDirs[i].ordinal()];
				}
				
				// If the current nomino has only one not null side, start going back
				if(curr.numNullSides() == 3) {
					curr = notNull[0];
					visited.add(curr);
					steps.add(notNullDirs[0]);
					continue loop;
				}
				
				// First loop through all of the not null sides of the current nomino,
				// if we find one that we have not visited yet, then we go there next
				for(int i = 0; i < notNull.length; i++) {
					if(! visited.contains(notNull[i])) {
						curr = notNull[i];
						visited.add(curr);
						steps.add(notNullDirs[i]);
						continue loop;
					}
				}
				
				// If we get here then it means all of the not null sides of the current
				// nomino have been visited already, in which case we go to the first one
				// that is not in the opposite direction of the last step we took
				Direction lastStep = steps.get(steps.size() - 1);
				for(int i = 0; i < 4-curr.numNullSides(); i++) {
					if(notNullDirs[i].opposite() != lastStep) {
						curr = notNull[i];
						steps.add(notNullDirs[i]);
						continue loop;
					}
				}
			}
		return steps.toArray(new Direction[steps.size()]);
	}
	
	/**
	 * Method that takes a piece as is and rotates it clockwise by 90 degrees
	 */
	public void rotate() {
		// If the nomino has a classification of 1, then simply return
		if(classification == 1) return;
		
		// If the nomino is square (meaning it is a tetromino whose walk is 4 steps),
		// then we also simnply return
		if(classification == 4 && walk().length == 4) return;
		
		// If the nomino is the square-plus pentomino (meaning it is the pentomino
		// that is shaped like a square with one additional nomino attached somewhere)
		// then we need to handle it in a special way
		if(classification == 5 && (walk().length == 6 || walk().length == 10)) {
			applySquarePlus(2);
			if(setCoords(col, row) == false) shiftIntoGrid();
			return;
		}
		
		// The walk of this nomino rotated by 90 degrees
		Direction[] rotatedWalk = Direction.rotateAll(walk(), 90);
		
		// Now apply the rotation
		apply(rotatedWalk);
		
		// The coordinates of the "head" nomino does not change in a rotation
		// If the piece ends up outside the grid after rotation, shift the piece into the grid
		if(setCoords(col, row) == false) shiftIntoGrid();
	}
	
	/**
	 * Method that takes this nomino and reflects it over the x or y axes
	 * 
	 * @param axis flag which tells the method which axis to reflect over: 0-x and 1-y
	 * @throws IllegalArgumentException if the given axis argument is not 0 or 1
	 */
	public void reflect(int axis) throws IllegalArgumentException {
		// If the nomino has a classification of 1, then simply return
		if(classification == 1) return;
		
		// If the nomino is square (meaning it is a tetromino whose walk is 4 steps),
		// then we also simnply return
		if(classification == 4 && walk().length == 4) return;
		
		// If the nomino is the square-plus pentomino (meaning it is the pentomino
		// that is shaped like a square with one additional nomino attached somewhere)
		// then we need to handle it in a special way
		if(classification == 5 && (walk().length == 6 || walk().length == 10)) {
			switch(axis) {
			case 0:
				applySquarePlus(0);
				if(setCoords(col, row) == false) shiftIntoGrid();
				return;
			case 1:
				applySquarePlus(1);
				if(setCoords(col, row) == false) shiftIntoGrid();
				return;
			default:
				throw new IllegalArgumentException("Given " + axis + " expected 0 or 1");
			}
		}
		
		Direction[] reflectedWalk;
		switch(axis) {
		case 0: 
			reflectedWalk = Direction.reflectx(walk());
			break;
		case 1:
			reflectedWalk = Direction.reflecty(walk());
			break;
		default:
			throw new IllegalArgumentException("Given " + axis + " expected 0 or 1");
		}
		
		// Now apply the reflection
		apply(reflectedWalk);
		
		// When reflecting we do not change the coordinates of the "head" nomino
		// If the reflection causes the piece to go outside the grid, we must shift
		// it back into the grid
		if(setCoords(col, row) == false) shiftIntoGrid();
	}
	
	/**
	 * Method that deconstructs the calling nomino and then reconstructs it using the
	 * given array of directions, applying either rotation or reflection
	 * 
	 * @param dirs an array of directions to use in rotating/reflecting this nomino
	 */
	private void apply(Direction[] dirs) {
		// Detach all nominos from this nomino
		for(Direction dir : notNullSides()) {
			detach(dir);
		}
		
		// Now that all nominos have been detached, the classification of this
		// nomino has gone down to one
		this.classification = 1;
		
		// Next, we create a stack ArrayDeque using the rotated walk
		Deque<Direction> steps = new ArrayDeque<Direction>(Arrays.asList(dirs));
		
		// The nomino we are currently attaching to, beginning at this nomino
		Nomino curr = this;
		
		while(! steps.isEmpty()) {
			// Pop the next direction off the stack
			Direction step = steps.pop();
			
			// If there is not currently a nomino attached in the direction of step,
			// then attach a new nomino there and then step in that direction
			if(curr.sides[step.ordinal()] == null) {
				curr.attach(step);
			}
			curr = curr.sides[step.ordinal()];
		}
	}
	
	/**
	 * Applies a reflection or a 90 degree rotation of the square plus pentomino,
	 * depending on the integer flag that is given:
	 * 0 - Reflection over x-axis
	 * 1 - Reflection over y-axis
	 * 2 - 90 degree clockwise rotation
	 * NOTE that this method assumes the "head" nomino (the calling instance) of the
	 * square plus nomino is one of the four nominos in the squre shape, not the one that
	 * hangs off one of the sides of the shape
	 * 
	 * @param action flag for what symmetry to apply to the nomino
	 * @throws IllegalArgumentException if the given action is not 0, 1 or 2
	 */
	private void applySquarePlus(int action) throws IllegalArgumentException {
		// The original walk of the square plus nomino
		Direction[] unchangedWalk = walk();
		
		// The current nomino in our search
		Nomino curr = this;

		// The last step taken in the walk
		Direction prevStep = null;
		for(Direction step : unchangedWalk) {
			
			// If the next step is in the opposite direction of the
			// previous step, then we have found the extra square
			if(prevStep != null && prevStep.equals(step.opposite())) {
				curr = curr.sides[step.ordinal()];
				break;
			}
			
			// Otherwise, we take a step and continue on
			curr = curr.sides[step.ordinal()];
			prevStep = step;
		}
		
		// First thing we do is detach the extra nomino from the square
		curr.detach(prevStep);
		curr.classification = 4;
		curr.updateClassification();
		
		// Now apply the symmetry
		switch(action) {
		case 0:
			reflectxSquarePlus(curr, prevStep);
			break;
		case 1:
			reflectySquarePlus(curr, prevStep);
			break;
		case 2:
			rotateSquarePlus(curr, prevStep);
			break;
		default:
			throw new IllegalArgumentException("Given " + action + " expected 0, 1 or 2");
		}
	}
	
	/**
	 * Reflect the square plus nomino over the x-axis
	 * 
	 * @param n    the nomino to which the extra square was attached before the reflection
	 * @param side the side of n that the extra square was attached to before the reflection
	 */
	private void reflectxSquarePlus(Nomino n, Direction side) {
		// Depending on the side of n the extra square was attached to prior to being
		// detached to perform the reflection, it must be reattatched somewhere else to
		// apply the reflection
		switch(side) {
		case TOP:
			// Attach to the bottom of the nomino below n
			n.attach(Direction.BOTTOM, Direction.BOTTOM);
			break;
		case BOTTOM:
			// Attach the the top of the nomino above n
			n.attach(Direction.TOP, Direction.TOP);
			break;
		case RIGHT:
			// If n is the top right nomino in the square, then attach the new nomino
			// right of the nomino below n
			if(n.sides[Direction.TOP.ordinal()] == null) {
				n.attach(Direction.RIGHT, Direction.BOTTOM);
				
			// Otherwise, n must be the bottom right nomino in the square, so we attach
			// the new nomino right of the nomino above n
			} else {
				n.attach(Direction.RIGHT, Direction.TOP);
			}
			break;
		case LEFT:
			// If n is the top left nomino in the square, then attach the new nomino
			// left of the nomino below n
			if(n.sides[Direction.TOP.ordinal()] == null) {
				n.attach(Direction.LEFT, Direction.BOTTOM);
				
			// Otherwise, n must be the bottom left nomino in the square, so we attach
			// the new nomino left of the nomino above n
			} else {
				n.attach(Direction.LEFT, Direction.TOP);
			}
			break;
		}
	}
	
	/**
	 * Reflect the square plus nomino over the y-axis
	 * 
	 * @param n    the nomino to which the extra square was attached before the reflection
	 * @param side the side of n that the extra square was attached to before the reflection
	 */
	private void reflectySquarePlus(Nomino n, Direction side) {
		// Depending on the side of n the extra square was attached to prior to being
		// detached to perform the reflection, it must be reattatched somewhere else to
		// apply the reflection
		switch(side) {
		case TOP:
			// If n is the top right nomino in the square, then we attach the new nomino
			// to the top of the nomino left of n
			if(n.sides[Direction.RIGHT.ordinal()] == null) {
				n.attach(Direction.TOP, Direction.LEFT);
			
			// Otherwise, n must be the top left nomino in the square, so we attach
			// the new nomino to the top of the nomino right of n
			} else {
				n.attach(Direction.TOP, Direction.RIGHT);
			}
			break;
		case BOTTOM:
			// If n is the bottom right nomino in the square, then we attach the new nomino
			// to the bottom the nomino left of n
			if(n.sides[Direction.RIGHT.ordinal()] == null) {
				n.attach(Direction.BOTTOM, Direction.LEFT);
			
			// Otherwise, n must be the bottom left nomino in the square, so we attach
			// the new nomino to the bottom of the nomino right of n
			} else {
				n.attach(Direction.BOTTOM, Direction.RIGHT);
			}
			break;
		case RIGHT:
			// Attach to the left of the nomino left of n
			n.attach(Direction.LEFT, Direction.LEFT);
			break;
		case LEFT:
			// Attach to the right of the nomino right of n
			n.attach(Direction.RIGHT, Direction.RIGHT);
			break;
		}
	}
	
	/**
	 * Rotate the square-plus nomino 90 degrees clockwise
	 * 
	 * @param n    the nomino to which the extra square was attached before the rotation
	 * @param side the side of n that the extra square was attached to before the rotation
	 */
	private void rotateSquarePlus(Nomino n, Direction side) {
		switch(side) {
		case TOP:
			// If n is the top right nomino of the square, attach the new nomino
			// right of the nomino below n
			if(n.sides[Direction.RIGHT.ordinal()] == null) {
				n.attach(Direction.RIGHT, Direction.BOTTOM);
				
			// Otherwise, n must be the top left nomino of the square, so attach the
			// new nomino right of the nomino right of n
			} else {
				n.attach(Direction.RIGHT, Direction.RIGHT);
			}
			break;
		case RIGHT:
			// If n is the top right nomino of the square, attach the new nomino to
			// the bottom of the nomino below n
			if(n.sides[Direction.TOP.ordinal()] == null) {
				n.attach(Direction.BOTTOM, Direction.BOTTOM);
				
			// Otherwise, n must be the bottom right nomino of the square, so attach the
			// new nomino below the nomino left of n
			} else {
				n.attach(Direction.BOTTOM, Direction.LEFT);
			}
			break;
		case BOTTOM:
			// If n is the bottom right nomino of the square, attach the new nomino to
			// the left of the nomino left of n
			if(n.sides[Direction.RIGHT.ordinal()] == null) {
				n.attach(Direction.LEFT, Direction.LEFT);
				
			// Otherwise, n must be the bottom left nomino of the square, so attach the
			// new nomino left of the nomino above n
			} else {
				n.attach(Direction.LEFT, Direction.TOP);
			}
			break;
		case LEFT:
			// If n is the top left nomino of the square, attach the new nomino to
			// the top of the nomino right of n
			if(n.sides[Direction.TOP.ordinal()] == null) {
				n.attach(Direction.TOP, Direction.RIGHT);
				
			// Otherwise, n must be the bottom left nomino of the square, so attach the
			// new nomino to the top of the nomino above n
			} else {
				n.attach(Direction.TOP, Direction.TOP);
			}
			break;
		}
	}
		
	/**
	 * Method for creating a new nomino that duplicates the shape of this nomino,
	 * that is, it will have nominos attached to the same sides as the calling nomino.
	 * Note that this duplicating does not go any deeper than that, that is, any
	 * nominos not attached directly to the calling instance will not be duplicated 
	 * 
	 * @return a nomino with the same shape as the calling instance
	 */
	public Nomino duplicate() {
		Nomino result = new Nomino(owner, board);
				
		for(Direction dir : Direction.values()) {
			if(sides[dir.ordinal()] != null) {
				result.attach(dir);
			}
		}
		return result;
	}
	
	/**
	 * Method that creates a complete copy of a nomino
	 * 
	 * @return the head of the copy of the nomino
	 */
	public Nomino copy() {
		// If the calling nomino is just a single monomino, create a copy and return
		if(classification == 1) return new Nomino(owner, board);
		
		// Array of all the individual nominos that are attached to this nomino somehow
		Nomino[] all = searchAllUnique(walk());
		
		// The 'head' nomino, that is, the duplicate of the calling instance
		Nomino duphead = null;
		
		// Array of newly duplicated versions of every nomino
		Nomino[] dups = new Nomino[all.length];
		for(int i = 0; i < all.length; i++) {
			dups[i] = all[i].duplicate();
			if(all[i].equals(this)) {
				duphead = dups[i];
			}
		}
		
		// Next, we iterate through every nomino attached to this nomino and,
		// for each of its not null sides, we find the corresponding duplicate and
		// attach it to the corresponding side of the duplicate of all[i]
		for(int i = 0; i < all.length; i++) {
			for(Direction dir : all[i].notNullSides()) {
				for(int j = 0; j < all.length; j++) {
					if(all[i].sides[dir.ordinal()].equals(all[j])) {
						dups[i].sides[dir.ordinal()] = dups[j];
						dups[j].sides[dir.getOppVal()] = dups[i];
					}
				}
			}
		}
		duphead.classification = this.classification;
		duphead.updateClassification();
		return duphead;
	}
	
	public String toString() {
		String result = "h";
		
		// First go left and then right
		if(searchAll(Direction.LEFT) != null) {
			for(Nomino n : searchAll(Direction.LEFT)) {
				result = "+" + result;
			}
		}
		if(searchAll(Direction.RIGHT) != null) {
			for(Nomino n : searchAll(Direction.RIGHT)) {
				result = result + "+";
			}
		}
		
		// Figure out how many spaces in the "head" nomino now is on the screen
		String cushion = "";
		for(int i = 0; i < result.indexOf("h"); i++) {
			cushion = cushion + " ";
		}
		
		// Now go up and down from the "head"
		if(searchAll(Direction.TOP) != null) {
			for(Nomino n : searchAll(Direction.TOP)) {
				result = cushion + "+\n" + result;
			}
		}
		if(searchAll(Direction.BOTTOM) != null) {
			for(Nomino n : searchAll(Direction.BOTTOM)) {
				result = result + "\n" + cushion + "+";
			}
		}
		
		return result;
	}
	
}













