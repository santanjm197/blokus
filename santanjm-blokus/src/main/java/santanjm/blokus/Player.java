package santanjm.blokus;

import java.util.*;
import santanjm.blokus.*;

public class Player {
	// The board the player will be playing on
	private Board board;
	
	// So the 4 directions can be called more succinctly
	public static final Direction TOP = Direction.TOP;
	public static final Direction BOTTOM = Direction.BOTTOM;
	public static final Direction RIGHT = Direction.RIGHT;
	public static final Direction LEFT = Direction.LEFT;
	
	// The number of polynominos the player has left
	public int remainingPieces = 21;
	
	// Map of the classifications of the polynominos to an array of them
	private Map<Integer, Nomino[]> pieces = new HashMap<Integer, Nomino[]>();
	
	// This player's number (used in determining turn order)
	private int playerNum;
	
	// A set containing the indices (within the board's spaces field) of all spaces
	// currently occupied by this player
	private Set<Integer> occupying = new HashSet<Integer>();
	
	// Flag indicating whether or not the player has passed their remaining turns
	private boolean hasPassed = false;
	
	/**
	 * Constructs a new Player with a given board and player number
	 * 
	 * @param gameBoard the board on which this player will play the game
	 * @param pNum      this player's number (used in determining turn order)
	 */
	public Player(Board gameBoard, int pNum) {
		board = gameBoard;
		playerNum = pNum;
		
		// Initialize this player's 21 nomino pieces
		initPieces();
	}
	
	/**
	 * Creates all 21 polynominos that this player starts the game with
	 */
	public void initPieces() {
		// First create entries in the pieces map for each of the 5 classifications
		// There is 1 polynomino of size 1 and 1 of size 2
		pieces.put(1,  new Nomino[1]);
		pieces.put(2, new Nomino[1]);
		
		// There are 2 polynominos of size 3
		pieces.put(3, new Nomino[2]);
		
		// There are 5 polynominos of size 4
		pieces.put(4, new Nomino[5]);
		
		// There are 12 polynominos of size 5
		pieces.put(5, new Nomino[12]);
		
		// Now we create all of the pieces in order of classification
		createClass1();
		createClass2();
		createClass3();
		createClass4();
		createClass5();
	}
	
	/**
	 * Creates the class 1 pieces
	 */
	public void createClass1() {
		// The only class 1 nomino is that of the single monomino
		pieces.get(1)[0] = new Nomino(this, board);
	}
	
	/**
	 * Creates the class 2 pieces
	 */
	public void createClass2() {
		// First we make a copy of the class 1 polynomino: the single monomino
		Nomino domino = new Nomino(this, board);
		
		// We create the class 2 polynomino by simply attaching a new monomino to
		// the left side of the "head" of the copied monomino
		domino.attach(LEFT);
		
		// Finally we place the domino into our pieces map
		pieces.get(2)[0] = domino;
	}
	
	/**
	 * Creates the class 3 pieces
	 */
	public void createClass3() {
		// There are 2 class 3 nominos, both gotten from attaching a single new
		// nomino to a domino: the corner by attaching to the bottom and the line3
		// by attaching to the right
		Nomino corner = getPiece(2, 0).duplicate();
		Nomino line3 = getPiece(2, 0).duplicate();
		
		// First we make the corner piece
		corner.attach(BOTTOM);
		pieces.get(3)[0] = corner;
		
		// Then we make the line3 piece
		line3.attach(RIGHT);
		pieces.get(3)[1] = line3;
	}
	
	/**
	 * Creates the class 4 pieces
	 */
	public void createClass4() {
		// There are 5 class 4 Nominos, two of which we form from the corner
		// trinomino and three of which we form from the line3 trinomino
		// We will start with the 2 from the corner trinomino
		Nomino square = getPiece(3, 0).duplicate();
		Nomino zshape = getPiece(3, 0).duplicate();
		
		// To make the square, we attach a single nomino southwest of the "head"
		// of the corner trinomino
		square.attach(LEFT, BOTTOM);
		square.search(LEFT).attachWithoutChangingClassification(BOTTOM, square.search(BOTTOM, LEFT));
		pieces.get(4)[0] = square;
		
		// To make the zshape, we attach a single nomino northwest of the "head"
		// of the corner trinomino
		zshape.attach(TOP, LEFT);
		pieces.get(4)[1] = zshape;
		
		// The other 3 class 4 nominos we derive from the line trinomino
		Nomino line4 = getPiece(3, 1).duplicate();
		Nomino tshape = getPiece(3, 1).duplicate();
		Nomino lshape = getPiece(3, 1).duplicate();
		
		// To make the line4, we attach a single nomino to the right of the 
		// right of the "head" of the line3 nomino
		line4.attach(RIGHT, RIGHT);
		pieces.get(4)[2] = line4;
		
		// To the make the tshape, we attach a single nomino to the top of the "head"
		// of the line3 nomino
		tshape.attach(TOP);
		pieces.get(4)[3] = tshape;
		
		// To make the lshape, we attach a single nomino to the top of the right
		// of the "head" of the line3 nomino
		lshape.attach(TOP, RIGHT);
		pieces.get(4)[4] = lshape;
	}
	
	/**
	 * Creates the class 5 pieces
	 */
	public void createClass5() {
		// There are 12 class 5 nominos
		// The squarePlus nomino is made very similarly to the square tetranomino,
		// except we attach one additional nomino right of the "head"
		Nomino squarePlus = getPiece(4, 0).copy();
		squarePlus.attach(RIGHT);
		pieces.get(5)[0] = squarePlus;
		
		// The zshapePlus nomino is made very similarly to the zshape tetranomino,
		// except we attach one additional nomino right of the "head"
		Nomino zshapePlus = getPiece(4, 1).copy();
		zshapePlus.attach(RIGHT);
		pieces.get(5)[1] = zshapePlus;
		
		// The zshapeLong nomino is made very similarly to the zshape tetranomino,
		// except we attach one additional nomino below the nomino below the "head"
		Nomino zshapeLong = getPiece(4, 1).copy();
		zshapeLong.attach(BOTTOM, BOTTOM);
		pieces.get(5)[2] = zshapeLong;
		
		// The wshape nomino is made very similarly to the zshape tetranomino,
		// except we attach one additional nomino to the right of the nomino below the "head"
		Nomino wshape = getPiece(4, 1).copy();;
		wshape.attach(RIGHT, BOTTOM);
		pieces.get(5)[3] = wshape;
		
		// The line5 nomino is made very similarly to the line4 tetranomino,
		// except we attach one additional nomino to the far right
		Nomino line5 = getPiece(4, 2).copy();
		line5.attach(RIGHT, RIGHT, RIGHT);
		pieces.get(5)[4] = line5;
		
		// The lshapeLong nomino is made very similarly to the line4 tetranomino,
		// except we attach one additional nomino to the top of the nomino left of the "head"
		Nomino lshapeLong = getPiece(4, 2).copy();
		lshapeLong.attach(TOP, LEFT);
		pieces.get(5)[5] = lshapeLong;
		
		// The tshapePlus nomino is made from the tshape tetranomino by attaching
		// one additonal nomino to the right of the right of the "head"
		Nomino tshapePlus = getPiece(4, 3).copy();
		tshapePlus.attach(RIGHT, RIGHT);
		pieces.get(5)[6] = tshapePlus;
		
		// The cross nomino is made from the tshape tetranomino by attaching
		// one additional nomino below the "head"
		Nomino cross = getPiece(4, 3).copy();
		cross.attach(BOTTOM);
		pieces.get(5)[7] = cross;
		
		// The tshapeLong nomino is made from the tshape tetranomino by attaching
		// one additional nomino to the top of the top of the "head"
		Nomino tshapeLong = getPiece(4, 3).copy();
		tshapeLong.attach(TOP, TOP);
		pieces.get(5)[8] = tshapeLong;
		
		// The bowl nomino is made very similarly to the lshape trinomino,
		// except we attach one additional nomino to the top of the right of the "head"
		Nomino bowl = getPiece(4, 4).copy();
		bowl.attach(TOP, LEFT);
		pieces.get(5)[9] = bowl;
		
		// The lshapePlus nomino is made very similarly to the lshape tetranomino,
		// except we attach one additional nomino to the top of the nomino northeast of "head"
		Nomino lshapePlus = getPiece(4, 4).copy();
		lshapePlus.attach(TOP, RIGHT, TOP);
		pieces.get(5)[10] = lshapePlus;
		
		// Finally, the windmill nomino is made very similarly to the lshape tetranomino,
		// except we attach one additional nomino below the nomino to the left of the "head"
		Nomino windmill = getPiece(4, 4).copy();
		windmill.attach(BOTTOM, LEFT);
		pieces.get(5)[11] = windmill;
	}
	
	/**
	 * Gets a piece owned by this player with specified classification and index
	 * 
	 * @param classification the number of monominos that make up the piece
	 * @param index          the position of that piece in pieces.get(classification)
	 * @return the polynomino of the requested type
	 * @throws IllegalArgumentException if the given classificaiton is not between 1 and 5
	 */
	public Nomino getPiece(int classification, int index) throws IllegalArgumentException {
		// The classification must be between 1 and 5
		if(classification < 1 || classification > 5) {
			throw new IllegalArgumentException("Classifications must be in range 1 to 5");
		}
		return pieces.get(classification)[index];
	}
	
	/**
	 * Method that returns the piece owned by the player having the lowest classification,
	 * and lowest index within that classification, of all pieces that the player has 
	 * not yet placed on the board
	 * 
	 * @return the next Nomino still available to the player
	 */
	public Nomino getNextNotPlacedPiece() {
		// The smallest classification of piece for which there is at least one piece left
		int smallestClass = 1;
		
		// Find the smallest classification of piece with at least one piece remaining
		for(int i = 1; i <= 5; i++) {
			if(allPlacedByClass(i) == false) {
				smallestClass = i;
				break;
			}
		}
		// All pieces with the smallest classification
		Nomino[] smallest = pieces.get(smallestClass);
		
		// The index of the first piece with that classification left
		int first = 0;
			
		// Find the first piece left with the smallest classification of pieces left
		for(int i = 0; i < smallest.length; i++) {
			if(smallest[i].isPlaced() == false) {
				first = i;
				break;
			}
		}
		
		return pieces.get(smallestClass)[first];
	}
	
	/**
	 * Method which returns the index of the first not yet placed piece owned by
	 * this Player with a given classification
	 * 
	 * @param classification the classification of piece to look at
	 * @return the smallest index of a piece of the given classification yet to be placed
	 * @throws IllegalArgumentException if the classification specified is not in 1-5
	 */
	public int getNextNotPlacedPiece(int classification) throws IllegalArgumentException {
		if(classification < 1 || 5 < classification)
			throw new IllegalArgumentException("Classification must be in range 1 to 5");
		
		// First ensure that there is at least one piece left of the given classification
		if(allPlacedByClass(classification)) return -1;
		
		// All the pieces with the specified classification
		Nomino[] sameSize = pieces.get(classification);
		
		// Find the first piece in the array that has not yet been placed
		int first = 0;
		for(int i = 0; i < sameSize.length; i++) {
			if(sameSize[i].isPlaced() == false) {
				first = i;
				break;
			}
		}
		return first;
	}
	
	/**
	 * Starting from a given position, this method will travel left (wrapping around
	 * when necessary) along the array of pieces of a specified classification, until
	 * it finds a piece that has not yet been placed and returns its position
	 * 
	 * @param classification the classification of pieces to look at
	 * @param index          the position in the array to start from
	 * @return the index of the next piece left of the starting index that is not placed
	 * @throws IllegalArgumentException if the classification specified is not in 1-5
	 */
	public int getNextNotPlacedPieceLeft(int classification, int index) {
		if(classification < 1 || 5 < classification)
			throw new IllegalArgumentException("Classification must be in range 1 to 5");
		
		// All pieces with the given classification
		Nomino[] sameSize = pieces.get(classification);
		
		// Since we are cycling left, we need to set the index back to the end
		// of the array if it is starting at 0
		if(index == 0) index = sameSize.length;
		
		// Subtract 1 from the index to cycle left once
		index--;
		
		// Until we find a Nomino in the array that has not been placed yet, we keep cycling
		while(sameSize[index].isPlaced()) {
			if(index == 0) index = sameSize.length;
			index--;
		}
		
		return index;
	}
	
	/**
	 * Starting from a given position, this method will travel right (wrapping around
	 * when necessary) along the array of pieces of a specified classification, until
	 * it finds a piece that has not yet been placed and returns its position
	 * 
	 * @param classification the classification of pieces to look at
	 * @param index          the position in the array to start from
	 * @return the index of the next piece right of the starting index that is not placed
	 * @throws IllegalArgumentException if the classification specified is not in 1-5
	 */
	public int getNextNotPlacedPieceRight(int classification, int index) {
		if(classification < 1 || 5 < classification)
			throw new IllegalArgumentException("Classification must be in range 1 to 5");
		
		// All pieces with the given classification
		Nomino[] sameSize = pieces.get(classification);
		
		// Since we are cycling right, we need only use addition and modulus
		// to travel through the array
		index = (index + 1) % sameSize.length;
		
		// Until we find a Nomino in the array that has not been placed yet, we keep cycling
		while(sameSize[index].isPlaced()) index = (index + 1) % sameSize.length;
		
		return index;
	}
	
	/**
	 * Returns an Array of all of the pieces that this Player has not yet placed on the Board,
	 * index 0 will be the piece returned by getNextNotPlacedPiece() and from there on the array
	 * will be in order of ascending index and classification
	 * 
	 * @return Array of the pieces that this Player has not yet placed
	 */
	public Nomino[] getRemainingPiecesAsArray() {
		// ArrayList of every piece that this Player has not yet placed
		List<Nomino> remaining = new ArrayList<Nomino>();
		
		// Start by getting the next not placed piece
		Nomino curr = getNextNotPlacedPiece();
		
		// The classification and index of curr
		int currClass = curr.getClassification();
		int currIndex = curr.getIndex();
		
		while(! remaining.contains(curr)) {
			// Add curr to the list of remaining not placed pieces
			remaining.add(curr);
			
			// The index of the next not placed piece to the "right" of curr
			int nextRight = getNextNotPlacedPieceRight(currClass, currIndex);
			
			// If there are no other pieces of currClass that have yet to be placed,
			// then we move to next classification up
			if(nextRight <= currIndex) {
				// If currClass is already 5, then there are no more pieces to look at, so break
				if(currClass == 5) break;
				
				// Otherwise, increase currClass by 1 and find the next not placed piece
				currClass++;
				currIndex = getNextNotPlacedPiece(currClass);
				curr = pieces.get(currClass)[currIndex];
				
			// If instead nextRight > currIndex, we set curr to that piece
			} else {
				currIndex = nextRight;
				curr = pieces.get(currClass)[currIndex];
			}
		}
		
		return remaining.toArray(new Nomino[remaining.size()]);
	}
	
	/**
	 * Getter for the player's pieces map
	 * 
	 * @return the player's pieces field
	 */
	public Map<Integer, Nomino[]> getPieces() {
		return pieces;
	}
	
	/**
	 * Gets the array of all pieces with the same given classification
	 * 
	 * @param classification the classification of pieces to get
	 * @return array of all pieces owned by this player with the given classification
	 * @throws IllegalArgumentException if the classification specified is not in 1-5
	 */
	public Nomino[] getPiecesByClass(int classification) throws IllegalArgumentException {
		if(classification < 1 || 5 < classification)
			throw new IllegalArgumentException("Classification must be in range 1 to 5");
		
		return pieces.get(classification);
	}
	
	/**
	 * Method that returns whether or not one of this player's pieces has been placed
	 * 
	 * @param classification the classification of piece to check
	 * @param index          the index of the piece in its classification array
	 * @return the value of the piece's placed field
	 * @throws IllegalArgumentException if the classification specified is not in 1-5
	 */
	public boolean pieceIsPlaced(int classification, int index) throws IllegalArgumentException {
		if(classification < 1 || 5 < classification)
			throw new IllegalArgumentException("Classification must be in range 1 to 5");
		
		return pieces.get(classification)[index].isPlaced();
	}
	
	/**
	 * Method that checks if every nomino owned by this player with a given classification
	 * has been placed already
	 * 
	 * @param classification the classification of nominos to check
	 * @return true if all owned pieces of the given classification have been placed
	 * @throws IllegalArgumentException if the classification specified is not in 1-5
	 */
	public boolean allPlacedByClass(int classification) throws IllegalArgumentException {
		if(classification < 1 || 5 < classification)
			throw new IllegalArgumentException("Classification must be in range 1 to 5");
		
		// All nominos owned by this player with the given classification
		Nomino[] sameSize = pieces.get(classification);

		// Iterate through sameSize, if any of them have not been placed yet, return false
		for(Nomino n : sameSize) {
			if(n.isPlaced() == false) return false;
		}
		return true;
	}
	
	/**
	 * Gets this player's number
	 * 
	 * @return the value of the playerNum field
	 */
	public int getPlayerNum() {
		return playerNum;
	}
	
	/**
	 * Method that takes the index (size*row + col) of a space on the board and adds it
	 * to the Player's set of occupied spaces
	 * 
	 * @param index the index of a Space on the Board (size*row + col)
	 */
	public void occupy(int index) {
		occupying.add(index);
	}
	
	/**
	 * Returns the number of spaces occupied by this Player
	 * 
	 * @return the size of the occupying field
	 */
	public int getNumberOfSpacesOccupied() {
		return occupying.size();
	}
	
	/**
	 * Sets this Player's hasPassed field to true, so this Player's turn will now pass
	 * every time it comes up in the rotation
	 */
	public void setHasPassed() {
		hasPassed = true;
	}
	
	/**
	 * Gets the current value of the hasPassed field
	 * 
	 * @return the current of value of hasPassed
	 */
	public boolean getHasPassed() {
		return hasPassed;
	}
}



















