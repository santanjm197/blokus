package santanjm.blokus;

import java.awt.*;

import santanjm.blokus.*;

public class Space {
	// The column and row of this space on the board
	private int col, row;
	
	// Flag telling whether or not this space is covered by a nomino
	private boolean occupied = false;
	
	// The number of the player whose polynomino is occupying this space
	private int playerNum;
	
	// The panel which will represent this space in GUI
	SpacePanel panel;
	
	// The Graphics object used by the Space Panel
	private Graphics panelGraphics;
	
	/**
	 * Construct a new Space with the given column and row
	 * 
	 * @param col the column of this space
	 * @param row the row of this space
	 */
	public Space(int col, int row) {
		this.col = col;
		this.row = row;
		panel = new SpacePanel(col, row);
		panelGraphics = panel.getGraphics();
	}
	
	/**
	 * Tell the panel that a piece is hovering over this space
	 * 
	 * @param pNum the number of the player who owns the piece hovering over this space
	 */
	public void hover(int pNum) {
		panel.hover(pNum);
		panelGraphics = panel.getGraphics();
		panel.update(panelGraphics);
	}
	
	/**
	 * Tell the panel that a piece is no longer hovering over it
	 */
	public void dehover() {
		panel.dehover();
		panelGraphics = panel.getGraphics();
		panel.update(panelGraphics);
	}
	
	/**
	 * Occupy this space by the player with the given player number
	 * 
	 * @param pNum the number of the player who is now occupying this space
	 */
	public void occupy(int pNum) {
		occupied = true;
		playerNum = pNum;
		panel.occupy(pNum);
		panelGraphics = panel.getGraphics();
		panel.update(panelGraphics);
	}
	
	/**
	 * Unoccupy the space, setting its occupied flag to false and setting
	 * its playerNum field to 0
	 */
	public void unoccupy() {
		occupied = false;
		playerNum = 0;
	}
	
	/**
	 * Gets whether or not this space is currently occupied
	 * 
	 * @return current value of the occupied field
	 */
	public boolean isOccupied() {
		return occupied;
	}
	
	/**
	 * Gets the number of the player that is occupying this space
	 * 
	 * @return the number of the player occupying this space
	 */
	public int occupyingPlayer() {
		return playerNum;
	}
	
	/**
	 * Method that returns a length two array of the coordinates of a space: [col, row]
	 * 
	 * @return array of the coordinates of the space: [col, row]
	 */
	public int[] getCoords() {
		return new int[] {col, row};
	}
	
	/**
	 * Returns a String represntation of this Space in the following format:
	 * Space: [col, row]
	 * Occupied: occupied
	 * Occupied By: playerNum (if occupied is true)
	 */
	@Override
	public String toString() {
		String s = "Space: [" + col + ", " + row + "]\n" + "Occupied: " + occupied;
		if(occupied) s = s + "\nOccupied By: " + playerNum;
		
		return s;
	}
}














