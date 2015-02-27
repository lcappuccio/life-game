package org.systemexception.lifegame.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public final class TestDrawRectangle extends JPanel {

	private static final long serialVersionUID = 8786350553649412520L;
	private final int x, y, sizeX, sizeY;

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(5, 5);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(10, 10);
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(30, 30);
	}

	/**
	 * Create the panel.
	 *
	 * @param x
	 * @param y
	 */
	public TestDrawRectangle(int x, int y) {
		this.x = x;
		this.y = y;
		sizeX = this.getPreferredSize().width;
		sizeY = this.getPreferredSize().height;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(x, y, sizeX, sizeY);
	}
}
