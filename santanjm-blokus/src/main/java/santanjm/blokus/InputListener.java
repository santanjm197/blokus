package santanjm.blokus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import santanjm.blokus.*;

public class InputListener implements KeyListener {
	// The Board in which the game is being played
	private Board board;
	
	boolean turnOver;
	
	/**
	 * Constructs a new InputListener with a given Board instance
	 * 
	 * @param board the Board in which the game is being played
	 */
	public InputListener(Board board) {
		this.board = board;
	}
	
	public void keyTyped(KeyEvent e) {
		
	}
	
	public void keyPressed(KeyEvent e) {
		// The player whose turn it currently is
		Player active = board.getActivePlayer();
		
		// The currently selected piece
		Nomino selected = board.getSelectedPiece();
		
		// The current coordinates of the selected piece
		int[] oldCoords = selected.getCoords();
		
		// The keycode of the typed key
		int key = e.getKeyCode();
		
		// Attempt to place the currently selected piece at its current position
		if(key == KeyEvent.VK_ENTER) {
			// If the active player has not placed any pieces yet (they have 21 remaining)
			// check the selected piece's placement against the first turn rules
			if(active.remainingPieces == 21) {
				if(board.isLegalFirstTurn(selected, oldCoords[0], oldCoords[1])) {
					board.placePiece(selected, oldCoords[0], oldCoords[1]);
					turnOver = true;
					return;
				} else {
					System.out.println("Illegal first placement, place any piece in a corner");
					return;
				}
			}
			// If the active player has placed at least one piece, then regular rules apply
			else {
				if(board.isLegal(selected, oldCoords[0], oldCoords[1])) {
					board.placePiece(selected, oldCoords[0], oldCoords[1]);
					turnOver = true;
					return;
				} else {
					System.out.println("Illegal move");
					return;
				}
			}
		}
		
		// Moving the selected piece around the grid
		if(key == KeyEvent.VK_W) selected.move(Direction.TOP);
		if(key == KeyEvent.VK_D) selected.move(Direction.RIGHT);
		if(key == KeyEvent.VK_S) selected.move(Direction.BOTTOM);
		if(key == KeyEvent.VK_A) selected.move(Direction.LEFT);
		
		// The new hovering coordinates of the selected piece
		int[] coords = selected.getCoords();
		
		// Hover the selected piece over these new coordinates (if they have changed
		if(coords[0] != oldCoords[0] || coords[1] != oldCoords[1]) {
			board.dehoverPiece(selected, oldCoords[0], oldCoords[1]);
			board.hoverPiece(selected, coords[0], coords[1]);
			return;
		}
		
		// Rotate the piece 90 degrees clockwise
		if(key == KeyEvent.VK_R) {
			board.dehoverPiece(selected, oldCoords[0], oldCoords[1]);
			selected.rotate();
			coords = selected.getCoords();
			board.hoverPiece(selected, coords[0], coords[1]);
			return;
		}
		
		// Flip the piece vertically and horizontally
		if(key == KeyEvent.VK_Q) {
			board.dehoverPiece(selected, oldCoords[0], oldCoords[1]);
			selected.reflect(0);
			coords = selected.getCoords();
			board.hoverPiece(selected, coords[0], coords[1]);
			return;
		}
		if(key == KeyEvent.VK_E) {
			board.dehoverPiece(selected, oldCoords[0], oldCoords[1]);
			selected.reflect(1);
			coords = selected.getCoords();
			board.hoverPiece(selected, coords[0], coords[1]);
			return;
		}
		
		// Change the classification of the selected piece
		if(49 <= key && key <= 53) {
			if(active.allPlacedByClass(key-48) == false) board.changeSelectedPieceByClass(key-48);
			return;
		}
		
		// Cycle through the pieces with the same classification as the currently selected one
		if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
			int selectedIndex = selected.getIndex();
			if(key == KeyEvent.VK_LEFT) {
				// Set selectedIndex to the index of the next not placed piece, cycling
				// left starting from the position of the selected piece
				selectedIndex = 
					active.getNextNotPlacedPieceLeft(selected.getClassification(), selectedIndex);
			} else {
				// Set selectedIndex to the index of the next not placed piece, cycling
				// right starting from the position of the selected piece
				selectedIndex =
					active.getNextNotPlacedPieceRight(selected.getClassification(), selectedIndex);
			}
			
			// Change the selected piece
			board.changeSelectedPieceByIndex(selectedIndex);
			return;
		}
		
		// If the player hits the 'P' key, pass thier turn and mark them as passaple for
		// the remainder of the game
		if(key == KeyEvent.VK_P) {
			board.dehoverPiece(selected, oldCoords[0], oldCoords[1]);
			active.setHasPassed();
			turnOver = true;
			return;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		
	}
}
















