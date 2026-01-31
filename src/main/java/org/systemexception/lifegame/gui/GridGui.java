/**
 * @author leo
 * Nov 9, 2014 1:06:14 PM
 */
package org.systemexception.lifegame.gui;

import org.systemexception.lifegame.enums.Themes;
import org.systemexception.lifegame.model.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GridGui extends JPanel {

	private final int gridRows;
	private final int gridCols;
	private final int cellSize;
	private int totalLiveCells;

	private Color colorDark = Color.DARK_GRAY;
	private Color colorLight = Color.WHITE;

	private Board board;

	private boolean[][] previousState;
	private final Set<Point> changedCells = new HashSet<>();

	public GridGui(final int cellSize, int gridRows, int gridCols, String colourTheme) {
		this.cellSize = cellSize;
		this.gridRows = gridRows;
		this.gridCols = gridCols;
		this.board = new Board(gridRows, gridCols);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int i = e.getX() / cellSize;
				int j = e.getY() / cellSize;
				board.setCellAt(i, j, !board.getCellAt(i, j));
				getParent().repaint();
			}
		});

		// Initialize previousState array
		this.previousState = new boolean[gridRows][gridCols];
		initializePreviousState();

		totalLiveCells = board.getCellAliveCount();
		setColours(colourTheme);
	}

	public GridGui(int cellSize, int gridRows, int gridCols, List<List<String>> savedBoard, String
			colourTheme) {
		this.cellSize = cellSize;
		this.gridRows = gridRows;
		this.gridCols = gridCols;
		this.board = new Board(gridRows, gridCols, savedBoard);

		// Initialize previousState array
		this.previousState = new boolean[gridRows][gridCols];
		initializePreviousState();

		totalLiveCells = board.getCellAliveCount();
		setColours(colourTheme);
		this.repaint();
	}

	/**
	 * Initialize previousState to match current board state
	 */
	private void initializePreviousState() {
		for (int i = 0; i < gridRows; i++) {
			for (int j = 0; j < gridCols; j++) {
				previousState[i][j] = board.getCellAt(i, j);
			}
		}
	}

	/**
	 * Resets the board
	 */
	public void resetBoard() {
		this.board = new Board(gridRows, gridCols);
		totalLiveCells = board.getCellAliveCount();

		// Reset previousState
		initializePreviousState();

		// Full repaint after reset
		repaint();
	}

	public Board getBoard() {
		return board;
	}

	public int getCellSize() {
		return cellSize;
	}

	/**
	 * Iterates the board using the chosen automata
	 */
	public void iterateBoard() {
		board.iterateBoard();
		totalLiveCells = board.getCellAliveCount();

		// Track what changed
		changedCells.clear();
		for (int i = 0; i < gridRows; i++) {
			for (int j = 0; j < gridCols; j++) {
				boolean current = board.getCellAt(i, j);
				if (previousState[i][j] != current) {
					changedCells.add(new Point(i, j));
					previousState[i][j] = current;
				}
			}
		}

		// Repaint only changed cells
		if (changedCells.isEmpty()) {
			return;
		}

		// Only repaint changed areas
		changedCells.forEach(p -> repaint(p.x * cellSize, p.y * cellSize, cellSize, cellSize));
		changedCells.clear();
	}

	public int getTotalLiveCells() {
		return totalLiveCells;
	}

	/**
	 * Colours from: <a href="http://www.flatuicolorpicker.com">...</a>
	 *
	 * @param colourTheme is the colour enum value
	 */
	public void setColours(String colourTheme) {
		Map<String, Color> themeColors = colors(colourTheme);
		this.colorDark = themeColors.get("dark");
		this.colorLight = themeColors.get("light");
	}

	private static Map<String, Color> colors(String colourTheme) {
		return Map.ofEntries(
			Map.entry("dark", getDarkColor(colourTheme)),
			Map.entry("light", getLightColor(colourTheme))
		);
	}

    private static Color getDarkColor(String colourTheme) {
        if (colourTheme == null) {
            return Color.DARK_GRAY;
        }
        if (Themes.BW.toString().equals(colourTheme)) {
            return Color.DARK_GRAY;
        } else if (Themes.INVERSE.toString().equals(colourTheme)) {
            return Color.WHITE;
        } else if (Themes.BLUE.toString().equals(colourTheme)) {
            return hex2Rgb("#003366");
        } else if (Themes.GREEN.toString().equals(colourTheme)) {
            return hex2Rgb("#336600");
        } else if (Themes.RED.toString().equals(colourTheme)) {
            return hex2Rgb("#660000");
        } else {
            return Color.DARK_GRAY;
        }
    }

    private static Color getLightColor(String colourTheme) {
        if (colourTheme == null) {
            return Color.WHITE;
        }
        if (Themes.BW.toString().equals(colourTheme)) {
            return Color.WHITE;
        } else if (Themes.INVERSE.toString().equals(colourTheme)) {
            return Color.DARK_GRAY;
        } else if (Themes.BLUE.toString().equals(colourTheme)) {
            return hex2Rgb("#19B5FE");
        } else if (Themes.GREEN.toString().equals(colourTheme)) {
            return hex2Rgb("#36D7B7");
        } else if (Themes.RED.toString().equals(colourTheme)) {
            return hex2Rgb("#EF4836");
        } else {
            return Color.WHITE;
        }
    }

	/**
	 * As seen on:
	 * <a href="http://stackoverflow.com/questions/4129666/how-to-convert-hex-">...</a>
	 * to-rgb-using-java
	 *
	 * @param colorStr e.g. "#FFFFFF"
	 * @return the colour in RGB fashion
	 */
	private static Color hex2Rgb(String colorStr) {
		return new Color(
			Integer.parseInt(colorStr.substring(1, 3), 16),
			Integer.parseInt(colorStr.substring(3, 5), 16),
			Integer.parseInt(colorStr.substring(5, 7), 16)
		);
	}

	@Override
	public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        Rectangle2D rectangle2D = new Rectangle2D.Double();
        for (int i = 0; i < gridRows; i++) {
			for (int j = 0; j <gridCols; j++) {
                graphics2D.setColor(board.getCellAt(i, j) ? colorLight : colorDark);
                rectangle2D.setRect(cellSize * i, cellSize * j, cellSize, cellSize);
                graphics2D.fill(rectangle2D);
            }
		}
	}
}
