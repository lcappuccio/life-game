/**
 * @author leo
 * Nov 9, 2014 1:06:14 PM
 */
package org.systemexception.lifegame.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import org.systemexception.lifegame.board.Board;

@SuppressWarnings("serial")
public class Grid extends JComponent {

	private Board board;
	private int cellSize, gridRows, gridCols;

	public Grid(int cellSize, int gridRows, int gridCols) {
		this.cellSize = cellSize;
		this.gridRows = gridRows;
		this.gridCols = gridCols;
<<<<<<< HEAD
	}
	
	public void setCellValue(int x) {
		cellSize = x;
=======
>>>>>>> master
	}

	public Board getBoard() {
		return board;
	}
	
	public int getCellSize() {
		return cellSize;
	}

	public void paintComponent(Graphics g) {
		System.out.println("Grid paintComponent: " + cellSize + "\t" + gridRows + "\t" + gridCols);
		this.board = new Board(gridRows, gridCols);
		for (int i = 0; i < board.getBoardRows(); i++) {
			for (int j = 0; j < board.getBoardCols(); j++) {
				g.setColor(board.getCellAt(i, j).isAlive() ? Color.DARK_GRAY : Color.WHITE);
				g.fillRect(cellSize * i, cellSize * j, cellSize, cellSize);
			}
		}
	}
}
