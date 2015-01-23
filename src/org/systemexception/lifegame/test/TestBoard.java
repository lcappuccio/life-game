/**
 * @author leo
 * Nov 9, 2014 1:27:42 PM
 */
package org.systemexception.lifegame.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Ignore;
import org.junit.Test;
import org.systemexception.lifegame.model.Board;
import org.systemexception.lifegame.model.Cell;

public class TestBoard {

	private int boardSizeX = 3, boardSizeY = 3;
	Board sut = new Board(boardSizeX, boardSizeY);

	@Test
	public void testBordExists() {
		assertNotNull(sut);
	}

	@Test
	public void boardHasCorrectSize() {
		assertEquals(sut.getBoardRows(), boardSizeX);
		assertEquals(sut.getBoardCols(), boardSizeY);
	}

	@Test
	@Ignore
	public void newBoardHasCells() {
		for (int i = 0; i < sut.getBoardRows(); i++) {
			for (int j = 0; j < sut.getBoardCols(); j++) {
				assertNotNull(sut.getCellAt(i, j));
			}
		}
	}

	@Test
	@Ignore
	public void boardIsRoundHorizontally() {
		Cell myCell = sut.getCellAt(2, 0);
		assertSame(myCell, sut.getCellAt(-1, 0));
	}

	@Test
	public void boardIsRoundVertically() {
		Cell myCell = sut.getCellAt(2, 2);
		assertSame(myCell, sut.getCellAt(2, -1));
	}

	@Test
	public void verifySurroundingAliveCellsCount() {
		int i = 0, j = 2;
		int aliveCellCount = sut.countSurroungingLiveCells(i,j);
		sut.printBoard();
		System.out.println("Live cells around (" + i + "," + j + "): " + aliveCellCount);
	}
}
