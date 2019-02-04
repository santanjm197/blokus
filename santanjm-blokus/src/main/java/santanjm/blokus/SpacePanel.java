package santanjm.blokus;

import javax.swing.*;
import java.awt.*;

import santanjm.blokus.*;

public class SpacePanel extends JComponent {
	// The width and height of every Space is constant
	private static final int SPACE_WIDTH = 35;
	
	// The x and y coordinates of each Space varies
	private int spacex, spacey;
	
	// Flag to tell whether the controlling space is occupied or not
	private boolean occupied = false;
	
	// The number of the player that is currently occupying the space
	private int playerNum;
	
	// Flag which tells whether a nomino is currently hovering over the controlling space
	private boolean hovering = false;
	
	// The number of the player currently hovering over this space
	private int hoveringPlayerNum;
	
	// The default space color
	private Color defaultGray = new Color(179, 179, 179);
	
	// The colors used for placed pieces for each player
	private Color player1Color = new Color(153, 0, 0);
	private Color player2Color = new Color(0, 0, 153);
	private Color player3Color = new Color(153, 153, 0);
	private Color player4Color = new Color(0, 153, 0);
	
	
	/**
	 * Construct a new Space Panel with specified coordinates
	 * 
	 * @param x the x coordinate of the Space on the screen
	 * @param y the y coordinate of the Space on the screen
	 */
	public SpacePanel(int x, int y) {
		super();
		spacex = x;
		spacey = y;
	}
	
	/**
	 * Flag this panel as currently having a piece hovering over it
	 * 
	 * @param pNum the number of the player who owns the piece hovering over the space
	 */
	public void hover(int pNum) {
		hovering = true;
		hoveringPlayerNum = pNum;
	}
	
	/**
	 * Flag this panel as no longer having a piece hovering over it
	 */
	public void dehover() {
		hovering = false;
	}
	
	/**
	 * Tells this space that it is now occupied by a specific player
	 * 
	 * @param pNum number of the player whose piece now occupies this space
	 */
	public void occupy(int pNum) {
		occupied = true;
		playerNum = pNum;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// If the space is neither occupied nor being hovered over by a piece, then
		// we simply fill it in gray
		if((! hovering) && (! occupied)) {
			g.setColor(defaultGray);
			g.fillRect(0, 0, SPACE_WIDTH, SPACE_WIDTH);
		}	
		// If the space is occupied we want to fill the rectangle in correctly
		if((! hovering) && occupied) {
			switch(playerNum) {
			case 1:
				g.setColor(player1Color);
				g.fillRect(0, 0, SPACE_WIDTH, SPACE_WIDTH);
				break;
			case 2:
				g.setColor(player2Color);
				g.fillRect(0, 0, SPACE_WIDTH, SPACE_WIDTH);
				break;
			case 3:
				g.setColor(player3Color);
				g.fillRect(0, 0, SPACE_WIDTH, SPACE_WIDTH);
				break;
			case 4:
				g.setColor(player4Color);
				g.fillRect(0, 0, SPACE_WIDTH, SPACE_WIDTH);
				break;
			}
		// If a player currently has a piece hovering over this space, we want to display
		// that piece first and foremost
		} else if(hovering) {
			switch(hoveringPlayerNum) {
			case 1:
				g.setColor(Color.RED);
				g.fillRect(0, 0, SPACE_WIDTH, SPACE_WIDTH);
				break;
			case 2:
				g.setColor(Color.BLUE);
				g.fillRect(0, 0, SPACE_WIDTH, SPACE_WIDTH);
				break;
			case 3:
				g.setColor(Color.YELLOW);
				g.fillRect(0, 0, SPACE_WIDTH, SPACE_WIDTH);
				break;
			case 4:
				g.setColor(Color.GREEN);
				g.fillRect(0, 0, SPACE_WIDTH, SPACE_WIDTH);
				break;
			}
		}
		
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, SPACE_WIDTH, SPACE_WIDTH);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(SPACE_WIDTH, SPACE_WIDTH);
	}
}






















