package santanjm.blokus;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import santanjm.blokus.*;

public class Board {
	// Array of spaces, the standard board size is 20x20
	private Space[] spaces;
	
	// The size of the board, e.g. 20 in a 20x20 board
	private int size;
	
	// The players in the game
	private Player[] players;
	
	// The number of players in the game
	private int numPlayers;
	
	// The player whose turn it currently is
	private Player active;
	
	// The piece currently selected for placement by the active player
	private Nomino selected;
	
	// The current turn number
	private int turn = 0;
	
	// The Grid Panel in which the game is displayed
	public GridPanel gridpanel;

	/**
	 * Creates a new Board with a grid size of 20x20
	 */
	public Board() {
		// A standard Blokus board is 20x20, that is, 400 spaces
		size = 20;
		spaces = new Space[size*size];
		
		// Now create the grid
		gridpanel = new GridPanel();
		createGrid();
	}
	
	/**
	 * Constructs a standard sized Blokus board with the designated number of players
	 * 
	 * @param numPlayers the number of players on this board
	 */
	public Board(int numPlayers) {
		this();
		players = new Player[numPlayers];
		this.numPlayers = numPlayers;
		for(int i = 0; i < numPlayers; i++) players[i] = new Player(this, i+1);
	}
	
	/**
	 * Constructs a Blokus board of a specified size and number of players
	 * 
	 * @param numPlayers the number of players on this board
	 * @param size       the dimension(s) of the board
	 */
	public Board(int numPlayers, int size) {
		this.size = size;
		spaces = new Space[size*size];
		gridpanel = new GridPanel();
		createGrid();
		players = new Player[numPlayers];
		this.numPlayers = numPlayers;
		for(int i = 0; i < numPlayers; i++) players[i] = new Player(this, i+1);
	}
	
	/**
	 * Construct the grid of Spaces which will make up the board
	 */
	private void createGrid() {
		// Get the square root of the length of the spaces array: that is the
		// number of columns and rows
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				spaces[size*i + j] = new Space(j, i);
				gridpanel.addSpace(spaces[size*i + j]);
			}
		}
	}
	
	/**
	 * Getter for the currently active player
	 * 
	 * @return the active player
	 */
	public Player getActivePlayer() {
		return active;
	}
	
	/**
	 * Sets the active player to the player whose is turn mod numPlayers,
	 * so on turn = 0, active = players[0], on turn = 1, active = players[1] etc
	 */
	public void setActivePlayer() {
		active = getPlayerFromNum((turn % numPlayers) + 1);
	}
	
	/**
	 * Getter for the piece currently selected by the active player
	 * 
	 * @return the selected field
	 */
	public Nomino getSelectedPiece() {
		return selected;
	}
	
	/**
	 * Sets the selected piece of the currently active player
	 * 
	 * @param classification the classification of piece (1-5)
	 * @param index          the position of the piece in its classification array
	 */
	public void setSelectedPiece(int classification, int index) {
		try {
			selected = active.getPiece(classification, index);
		} catch(IllegalArgumentException iae) {
			System.err.println(iae.getMessage());
			return;
		}
	}
	
	/**
	 * Changes the currently selected piece on the board to the first piece still available
	 * to the active player with the given classification
	 * 
	 * @param classification the classification of the piece to swap to (1-5)
	 */
	public void changeSelectedPieceByClass(int classification) {
		// Get coordinates of selected piece
		int[] coords = selected.getCoords();
		
		// Dehove the selected piece
		dehoverPiece(selected, coords[0], coords[1]);
		
		// Set the selected piece
		setSelectedPiece(classification, active.getNextNotPlacedPiece(classification));
		
		// If the new piece ends up outside the grid, shift it into the grid first
		if(selected.setCoords(coords[0], coords[1]) == false) selected.shiftIntoGrid();
		
		// The current coordinates of the selected piece (they may have changed)
		coords = selected.getCoords();
		
		// Hover the newly selected piece over the same coordiantes as before
		hoverPiece(selected, coords[0], coords[1]);
	}
	
	/**
	 * Changes the currently selected piece on the board to the one with the same
	 * classification as the currently selected piece but with the specified index
	 * position in the active player's array of pieces with that classification
	 * 
	 * @param index the position of this piece in the array
	 */
	public void changeSelectedPieceByIndex(int index) {
		// Get the coordinates of the currently selected piece
		int[] coords = selected.getCoords();
		
		// Dehove the selected piece
		dehoverPiece(selected, coords[0], coords[1]);
		
		// Set the selected piece
		setSelectedPiece(selected.getClassification(), index);
		
		// If the new piece ends up outside the grid, shift it into the grid first
		if(selected.setCoords(coords[0], coords[1]) == false) selected.shiftIntoGrid();
		
		// The current coordinates of the selected piece (they may have changed)
		coords = selected.getCoords();
		
		// Hover the newly selected piece over the same coordiantes as before
		hoverPiece(selected, coords[0],coords[1]);
	}
	
	/**
	 * Method that executes the game's loop
	 */
	public void gameLoop() {
		// The InputListener which will listen to player keyboard inputs
		InputListener il = new InputListener(this);
		gridpanel.setFocusable(true);
		gridpanel.addKeyListener(il);
		
		// The Frame in which the game will be displayed
		JFrame gameframe = new JFrame("Blokus");
		gameframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameframe.setResizable(false);
		gameframe.add(gridpanel);
		gameframe.pack();
		gameframe.setSize(gameframe.getPreferredSize());
		gameframe.setVisible(true);
		
		// Begin the actual game loop
		while(true) {
			// First set the active player
			setActivePlayer();
			
			// Skip the active player's turn if they have been marked as having
			// passed thier remaining moves
			if(active.getHasPassed()) {
				// If every player have finished the game, break the loop and end the game
				if(allPlayersPassed()) break;
				
				// If at least one player is still playing, continue the game
				turn++;
				continue;
			}
			
			// The currently selected piece by the active player
			selected = active.getNextNotPlacedPiece();
			
			// Start the selected piece in the top left corner of the grid (hovering)
			hoverPiece(selected, size/2 - 1, size/2 - 1);
			
			// Set the Input Listener's turn over flag to false
			il.turnOver = false;
			
			// Wait for the active player for finish their turn
			while(! il.turnOver) {
				try {
					TimeUnit.MILLISECONDS.sleep(50);
				}catch(InterruptedException ie) {
					System.err.println(ie.getMessage());
				}
			}
			turn++;
		}
		// Display the game's results
		displayResults();
	}
	
	/**
	 * Displays the results of the game, each player and the number of spaces they
	 * occupied at the end of the game
	 */
	public void displayResults() {
		StringBuilder results = new StringBuilder("The game has ended!\n");
		for(Player p : players) {
			results.append("Player ");
			results.append(p.getPlayerNum());
			results.append(": ");
			results.append(p.getNumberOfSpacesOccupied());
			results.append("\n");
		}
		System.out.println(results.toString());
	}
	
	/**
	 * Method that checks the legality of moves (turn 2+) by comparing attempted
	 * piece placements against the rules of Blokus
	 * 
	 * @param n   the piece attempting to be placed
	 * @param col the column in which n is attempting to be placed
	 * @param row the row in which n is attempting to be placed
	 * @return true if the move is legal and false otherwise
	 */
	public boolean isLegal(Nomino n, int col, int row) {
		// First:  Check that n has not already been placed
		if(n.isPlaced()) return false;
		
		// Second: Check to ensure that the placement of n will not overlap another piece
		if(willOverlap(n, col, row)) return false;
		
		// Third:  Ensure that no part of n will be directly touching another piece
		//         that is owned by the same player who owns n
		if(willTouchOwnedPiece(n, col, row)) return false;
		
		// Fourth: Make sure that at least one nomino attached to n meets another
		//         piece owned by the same player who owns n at a corner
		if(isDiagonalToOwnedPiece(n, col, row)) return true;
		
		return false;
	}
	
	/**
	 * Method that is used to check for legal first turn moves only, that is, it
	 * checks whether the player is placing their first piece in on the corners
	 * of the board or not
	 * 
	 * @param n   the player's first nomino that is attempting to be placed
	 * @param col the column in which n is attempting to be placed
	 * @param row the row in which n is attempting to be placed
	 * @return true if n's placement consititues a legal first turn move and false otherwise
	 */
	public boolean isLegalFirstTurn(Nomino n, int col, int row) {
		// First: Check to ensure that the placement of n will not overlap another piece
		if(willOverlap(n, col, row)) return false;
		
		// Second: Ensure that n is being placed in a corner of the board
		n.setCoords(col, row);
		Nomino[] attached = n.searchAllUnique(n.walk());
		for(Nomino att : attached) {
			int[] coords = att.getCoords();
			if((coords[0] == 0 && coords[1] == 0) ||
			   (coords[0] == size-1 && coords[1] == 0) ||
			   (coords[0] == 0 && coords[1] == size-1) ||
			   (coords[0] == size-1 && coords[1] == size-1))
				return true;
		}
		return false;
	}
	
	/**
	 * Method which checks whether or not a nomino, if placed at a certain cell in
	 * the gird, would overlap any pieces already in play
	 * 
	 * @param n   the nomino that is attempting to be placed
	 * @param col the column in which n is attempting to be placed
	 * @param row the row in which n is attempting to be placed
	 * @return true if n would overlap another piece and false otherwise
	 */
	public boolean willOverlap(Nomino n, int col, int row) {
		// If the space at (col, row) is occupied already, then return true right away
		if(spaces[size*row + col].isOccupied()) return true;
		
		// Set the coordinates of n (and all attached nominos), this amounts to
		// holding n over the board without placing it down
		n.setCoords(col, row);
		
		// Next, if n is a single monomino, then at this point we can simply return false
		if(n.getClassification() == 1) return false;
		
		// Every nomino (including n) that is attached to n
		Nomino[] attached = n.searchAllUnique(n.walk());
		
		// For each attached nomino, check the space that is would occupy if placed,
		// if even one of those spaces is already occupied, then return true
		for(Nomino att : attached) {
			int[] coords = att.getCoords();
			if(spaces[size*coords[1] + coords[0]].isOccupied()) return true;
		}
		
		return false;
	}
	
	/**
	 * Method which checks whether or a nomino, if placed at a certain cell in
	 * the grid, will directly touch a space already occupied by a piece owned
	 * by the same player
	 * 
	 * @param n   the nomino that is attempting to be placed
	 * @param col the column in which n is attempting to be placed
	 * @param row the row in which n is attempting to be placed
	 * @return true if n would directly touch a piece owned by n's owner and false otherwise
	 */
	public boolean willTouchOwnedPiece(Nomino n, int col, int row) {
		// The number of the player that owns n
		int ownerNum = n.getOwnerNum();
		
		// Set the coordinates of n (and all attached nominos), this amounts to
		// holding n over the board without placing it down
		n.setCoords(col, row);
		
		// Every nomino that is attached to n (including n itself) as a set
		Set<Nomino> attached = new HashSet<Nomino>(Arrays.asList(n.searchAllUnique(n.walk())));
		
		// Iterator on the set
		Iterator<Nomino> attItr = attached.iterator();
		
		while(attItr.hasNext()) {
			Nomino curr = attItr.next();
			
			// The coordinates of curr
			int[] coords = curr.getCoords();
			
			// The space that curr is trying to occupy
			Space currSpace = spaces[size*coords[1] + coords[0]];
			
			// The sides of curr that are null
			Direction[] nullSides = curr.nullSides();
			
			if(nullSides != null) {
				for(Direction dir : nullSides) {
					Space adj = findAdjacent(currSpace, dir);
					if(adj != null && (adj.isOccupied() && adj.occupyingPlayer() == ownerNum)) 
						return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Tests whether or not placing a nomino at a given space on the board will
	 * mean that nomino will touch another nomino previously placed by the same
	 * player at any of its open corners
	 * 
	 * @param n   the nomino that is attempting to be placed
	 * @param col the column in which n is attempting to be placed
	 * @param row the row in which n is attempting to be placed
	 * @return true if n would touch another owned piece at a corner and false otherwise
	 */
	public boolean isDiagonalToOwnedPiece(Nomino n, int col, int row) {
		// The number of the player that owns n
		int ownerNum = n.getOwnerNum();
				
		// Set the coordinates of n (and all attached nominos), this amounts to
		// holding n over the board without placing it down
		n.setCoords(col, row);
		
		// Every nomino that is attached to n (including n itself) as a set
		Set<Nomino> attached = new HashSet<Nomino>(Arrays.asList(n.searchAllUnique(n.walk())));
		
		// Iterator on the set
		Iterator<Nomino> attItr = attached.iterator();
		
		while(attItr.hasNext()) {
			Nomino curr = attItr.next();
			
			// The coordinates of curr
			int[] coords = curr.getCoords();
			
			// The space with those coordinates
			Space s = spaces[size*coords[1] + coords[0]];
			
			// The number of null sides that curr has
			Direction[] nullSides = curr.nullSides();
			
			// The diagonal space to check
			Space diag;
			
			if(nullSides != null) {
				switch(nullSides.length) {
				// If curr has 0 or 1 null sides, then there are no diagonals worth checking
				case 0:
				case 1:
					continue;
				// If curr has exactly 2 null sides, then ther is 0 or 1 diagonal to check depending
				case 2:
					// If the 2 null sides of curr are opposite of each other, then
					// we don't need to check any diagonals
					if(nullSides[0].equals(nullSides[1].opposite())) continue;
					
					// Otherwise, we need to check the diagonal that does not touch
					// either of the nominos attached to curr
					diag = find(s, nullSides[0], nullSides[1]);
					
					if(diag != null && (diag.isOccupied() && diag.occupyingPlayer() == ownerNum))
						return true;
				// If curr has exactly 3 null sides, then there is 2 diagonals to check
				case 3:
					// The one side of curr that is not null
					Direction[] notNullSide = curr.notNullSides();
					
					// Start with the space in the opposite direction of the not null side
					Space opp = find(s, notNullSide[0].opposite());
					if(opp != null) {
						for(Direction dir : nullSides) {
							if(! dir.equals(notNullSide[0].opposite())) {
								diag = find(opp, dir);
								if(diag != null && 
										(diag.isOccupied() && diag.occupyingPlayer() == ownerNum))
									return true;
							}
						}
					}
				case 4:
					// First check the space northwest of n
					Space nw = find(s, Direction.TOP, Direction.LEFT);
					if(nw != null && (nw.isOccupied() && nw.occupyingPlayer() == ownerNum)) 
						return true;
					
					// Next, chekc the space northeast of n
					Space ne = find(s, Direction.TOP, Direction.RIGHT);
					if(ne != null && (ne.isOccupied() && ne.occupyingPlayer() == ownerNum)) 
						return true;
					
					// Next, chekc the space southwest of n
					Space sw = find(s, Direction.BOTTOM, Direction.LEFT);
					if(sw != null && (sw.isOccupied() && sw.occupyingPlayer() == ownerNum)) 
						return true;
					
					// Next, chekc the space northeast of n
					Space se = find(s, Direction.BOTTOM, Direction.RIGHT);
					if(se != null && (se.isOccupied() && se.occupyingPlayer() == ownerNum)) 
						return true;
				}
			}
			
		}
		return false;
	}
	
	/**
	 * Method that places a given nomino piece on the grid at the coordinates given
	 * 
	 * @param n   the nomino to place
	 * @param col the column of the space in which n will be placed
	 * @param row the row of the space in which n will be placed
	 */
	public void placePiece(Nomino n, int col, int row) {
		// The number of the player that owns n
		int ownerNum = n.getOwnerNum();
		
		// The Player that owns n
		Player owner = n.getOwner();
		
		// Set the coordinates of n (and all attached nominos)
		n.setCoords(col, row);
		
		// Every nomino (including n) that is attached to n
		Nomino[] attached = n.searchAllUnique(n.walk());
		
		// For each attached nomino, we occupy the space on the board with the same coordinates
		for(Nomino att : attached) {
			int[] coords = att.getCoords();
			spaces[size*coords[1] + coords[0]].occupy(ownerNum);
			n.getOwner().occupy(size*coords[1] + coords[0]);
		}
		
		// Flag the nomino as having been placed on the board
		n.place();
		owner.remainingPieces--;
		
		// If the owner of n now has zero pieces left, flag them as passable on all future turns
		if(owner.remainingPieces == 0) owner.setHasPassed();
		
		// Finally, dehover this piece as it has been placed
		dehoverPiece(n, col, row);
	}
	
	/**
	 * (TESTING PURPOSES ONLY)
	 * Removes a piece from the board, unoccupying any spaces it was occupying
	 * and marking it as not having been placed
	 * 
	 * @param n the nomino to deplace
	 */
	public void deplacePiece(Nomino n) {
		// The current coordinates of n
		int[] coords = n.getCoords();
		
		// Every nomino (including n) that is attached to n
		Nomino[] attached = n.searchAllUnique(n.walk());
		
		// For each attached monomino, find the space it occupies and unoccupy it
		for(Nomino att : attached) {
			coords = att.getCoords();
			spaces[size*coords[1] + coords[0]].unoccupy();
		}
		
		// Now deplace n
		n.deplace();
	}
	
	/**
	 * Hovers a nomino above the board at the specified coordinates
	 * 
	 * @param n   the piece that is hovering over the board
	 * @param col the column of the space n is hovering over 
	 * @param row the row of the space n is hovering over
	 */
	public void hoverPiece(Nomino n, int col, int row) {
		// The number of the player that owns n
		int ownerNum = n.getOwnerNum();
		
		// Set the coordinates of n (and all attached nominos)
		n.setCoords(col, row);
		
		// Every nomino (including n) that is attached to n
		Nomino[] attached = n.searchAllUnique(n.walk());
		
		// For each attached nomino, we hover over the space on the board with the same coordinates
		for(Nomino att : attached) {
			int[] coords = att.getCoords();
			spaces[size*coords[1] + coords[0]].hover(ownerNum);
		}
	}
	
	/**
	 * Method which essentially tells a collection of spaces that a given
	 * piece (Nomino) is no longer hovering over it
	 * 
	 * @param n   any Nomino
	 * @param col the column of the Space over which n is currently hovering
	 * @param row the row of the Space over which n is currently hovering
	 */
	public void dehoverPiece(Nomino n, int col, int row) {
		// Make a copy of n
		Nomino ncopy = n.copy();
		
		// Set the copy's coordinates to (col, row)
		ncopy.setCoords(col, row);
		
		// Every nomino (including ncopy) that is attached to ncopy
		Nomino[] attached = ncopy.searchAllUnique(ncopy.walk());
		
		// For each attached nomino, we dehover over the space on the board with 
		// the same coordinates
		for(Nomino att : attached) {
			int[] coords = att.getCoords();
			spaces[size*coords[1] + coords[0]].dehover();
		}
	}
	
	/**
	 * Getter for the spaces array
	 * 
	 * @return the array of spaces
	 */
	public Space[] getSpaces() {
		return spaces;
	}
	
	/**
	 * Gets the space at the specified coordinates
	 * 
	 * @param col the column of the space
	 * @param row the row of the space
	 * @return the space with those coordinates
	 */
	public Space getSpace(int col, int row) {
		return spaces[size*row + col];
	}
	
	/**
	 * Method that, given a space s and a direction, returns the space on the grid
	 * that is directly adjacent to s in the given direction.  If there is no space
	 * on the grid in the given direction from s, the method will return null 
	 * 
	 * @param s    a space on the grid
	 * @param side a direction to look from s
	 * @return either the space adjacent to s, or null
	 */
	public Space findAdjacent(Space s, Direction side) {
		// The coordinates of s
		int[] coords = s.getCoords();
		
		// The coordinates of the space adjacent to s
		int[] adjCoords = new int[2];
		
		switch(side) {
		case TOP:
			adjCoords[0] = coords[0];
			adjCoords[1] = coords[1]-1;
			break;
		case RIGHT:
			adjCoords[0] = coords[0]+1;
			adjCoords[1] = coords[1];
			break;
		case BOTTOM:
			adjCoords[0] = coords[0];
			adjCoords[1] = coords[1]+1;
			break;
		case LEFT:
			adjCoords[0] = coords[0]-1;
			adjCoords[1] = coords[1];
			break;
		}
		
		// Check to ensure that adjCoords defines a space inside the grid
		if((adjCoords[0] < 0 || size <= adjCoords[0]) || 
				(adjCoords[1] < 0 || size <= adjCoords[1])) {
			return null;
		}
		
		// Since the coordinates define a space on the grid, return that space
		return spaces[size*adjCoords[1] + adjCoords[0]];
	}
	
	/**
	 * Method that searches from a starting space in a series of directions,
	 * returning the space it finds at the end of the search, or null.  Note that
	 * if the directions given ever point to a null, the method will return null
	 * immediately without finishing the search
	 * 
	 * @param start the space from which the search begins
	 * @param dirs  any number of directions, defining a route to search in
	 * @return the space found at the end of the search, or null
	 */
	public Space find(Space start, Direction... dirs) {
		// The current space in the search
		Space curr = start;
		
		for(Direction dir : dirs) {
			curr = findAdjacent(curr, dir);
			if(curr == null) return curr;
		}
		return curr;
	}
	
	/**
	 * A method which calculates and returns a HashSet of all of the spaces on the board
	 * that the given player could possibly legally place one of their pieces, that is,
	 * a set of all of the unoccpied spaces on the board diagonal to a space occupied by
	 * the given player that are not also adjacent to a space occupied by the given player
	 * 
	 * @param p any of the players in the current game
	 * @return a HashSet of all spaces that player p might be able to legally place a piece
	 */
	public Set<Space> findPossibleMoveSpaces(Player p) {
		// All spaces currently occupied by player p
		Set<Space> occupiedByP = new HashSet<Space>();
		for(Space s : spaces) {
			if(s.isOccupied() && s.occupyingPlayer() == p.getPlayerNum())
				occupiedByP.add(s);
		}
		
		// Next we iterate thorugh each space occupied by p and create another set, this one
		// containing all of the unoccupied spaces sitting diagonal to a space occupied by p
		Set<Space> unoccupiedDiagSpaces = new HashSet<Space>();
		for(Space s : occupiedByP) {
			// Check the space northwest of s
			Space diag = find(s, Direction.TOP, Direction.LEFT);
			if(diag != null && diag.isOccupied() == false) unoccupiedDiagSpaces.add(diag);
			
			// Check the space northeast of s
			diag = find(s, Direction.TOP, Direction.RIGHT);
			if(diag != null && diag.isOccupied() == false) unoccupiedDiagSpaces.add(diag);
			
			// Check the space southwest of s
			diag = find(s, Direction.BOTTOM, Direction.LEFT);
			if(diag != null && diag.isOccupied() == false) unoccupiedDiagSpaces.add(diag);
			
			// Check the space southeast of s
			diag = find(s, Direction.BOTTOM, Direction.RIGHT);
			if(diag != null && diag.isOccupied() == false) unoccupiedDiagSpaces.add(diag);
		}
		
		// Lastly, we want to go through this rough set of possible move spaces and
		// eliminate those which directly touch a space already occupied by player p
		Set<Space> possibleMoveSpaces = new HashSet<Space>();
		for(Space s : unoccupiedDiagSpaces) {
			boolean isPossibleMoveSpace = true;
			for(Direction dir : Direction.values()) {
				Space adj = find(s, dir);
				if(adj != null && adj.occupyingPlayer() == p.getPlayerNum()) {
					isPossibleMoveSpace = false;
					break;
				}
			}
			if(isPossibleMoveSpace) possibleMoveSpaces.add(s);
		}
		
		return possibleMoveSpaces;
	}
	
	/**
	 * Getter for the size of the board
	 * 
	 * @return the size of the board (number of columns and rows)
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Getter for the array of players in the game
	 * 
	 * @return the array of players in the game
	 */
	public Player[] getPlayers() {
		return players;
	}
	
	/**
	 * Gets the player on the board with the designated player number
	 * 
	 * @param pNum the number of a player on the board
	 * @return the player with the corresponding player number
	 */
	public Player getPlayerFromNum(int pNum) {
		return players[pNum-1];
	}
	
	/**
	 * Getter for the number of players in the game
	 * 
	 * @return the number of players in the game
	 */
	public int getNumPlayers() {
		return numPlayers;
	}
	
	/**
	 * Returns whether all players in the game have been marked as passable, which would
	 * signal the end of the game
	 * 
	 * @return true if all players are marked as passable and false otherwise
	 */
	public boolean allPlayersPassed() {
		for(Player p : players) {
			if(p.getHasPassed() == false) return false;
		}
		return true;
	}
}



















