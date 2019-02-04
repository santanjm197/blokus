package santanjm.blokus;

import javax.swing.*;
import java.awt.*;

public class GridPanel extends JPanel {
	// The layout manager for the panel
	private GridBagLayout gridbag;
	
	// The constraints for the gridbag
	private GridBagConstraints c;

	/**
	 * Creates a new Grid Panel with a GridBag Layout
	 */
	public GridPanel() {
		// Initialize the layout manager
		initLayout();
		
		// Reset some constraints as will now remain unchanged
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.ipadx = 0;
		c.ipady = 0;
		
		// The size of the panel
		setSize(675, 675);
	}
	
	/**
	 * Initialize the GridBag Layout of the Grid Panel and its constraints
	 */
	private void initLayout() {
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		setLayout(gridbag);
	}
	
	/**
	 * Adds a space to the Grid Panel at its proper coordinates
	 * 
	 * @param s a space
	 */
	public void addSpace(Space s) {
		int[] coords = s.getCoords();
		int gridCol = coords[0]*2;
		int gridRow = coords[1]*2;
		
		c.gridx = gridCol;
		c.gridy = gridRow;
		gridbag.setConstraints(s.panel, c);
		add(s.panel);
		
		c.weightx = 0.0;
	}
}

















