/**
 * @author leo
 * Nov 9, 2014 1:27:42 PM
 */
package org.systemexception.lifegame.model;

import org.junit.jupiter.api.Test;
import org.systemexception.lifegame.enums.Automata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    private final int boardSizeX = 5, boardSizeY = 5;
    private Board sut = new Board(boardSizeX, boardSizeY, Automata.CONWAY, 50);

    @Test
    void testBordExists() {
        assertNotNull(sut);
    }

    @Test
    void boardHasCorrectSize() {
        assertEquals(sut.getBoardRows(), boardSizeX);
        assertEquals(sut.getBoardCols(), boardSizeY);
    }

    @Test
    void testConwayVerticalOscillator() {
        sut = new Board(3, 3, Automata.CONWAY, 50);
        sut.setCellAt(0, 0, false);
        sut.setCellAt(0, 1, true);
        sut.setCellAt(0, 2, false);
        sut.setCellAt(1, 0, false);
        sut.setCellAt(1, 1, true);
        sut.setCellAt(1, 2, false);
        sut.setCellAt(2, 0, false);
        sut.setCellAt(2, 1, true);
        sut.setCellAt(2, 2, false);
        sut.iterateBoard();
        assertTrue(sut.getCellAt(1, 0));
        assertTrue(sut.getCellAt(1, 1));
        assertTrue(sut.getCellAt(1, 2));
    }


    @Test
    void testAssimilationIteration() {
        sut = new Board(100, 100, Automata.ASSIMILATION, 50);
        int cellsCounterBefore = sut.getCellAliveCount();
        sut.iterateBoard();
        int cellsCounterAfter = sut.getCellAliveCount();
        assertFalse(compareCounters(cellsCounterAfter, cellsCounterBefore));
    }

    @Test
    void testDryLifeIteration() {
        sut = new Board(100, 100, Automata.DRYLIFE, 50);
        int cellsCounterBefore = sut.getCellAliveCount();
        sut.iterateBoard();
        int cellsCounterAfter = sut.getCellAliveCount();
        assertFalse(compareCounters(cellsCounterAfter, cellsCounterBefore));
    }

    @Test
    void testHighLifeIteration() {
        sut = new Board(100, 100, Automata.HIGHLIFE, 50);
        int cellsCounterBefore = sut.getCellAliveCount();
        sut.iterateBoard();
        int cellsCounterAfter = sut.getCellAliveCount();
        assertFalse(compareCounters(cellsCounterAfter, cellsCounterBefore));
    }

    @Test
    void testLiveFreeOrDieIteration() {
        sut = new Board(100, 100, Automata.LIVEFREEORDIE, 50);
        int cellsCounterBefore = sut.getCellAliveCount();
        sut.iterateBoard();
        int cellsCounterAfter = sut.getCellAliveCount();
        assertFalse(compareCounters(cellsCounterAfter, cellsCounterBefore));
    }

    @Test
    void testCoralIteration() {
        sut = new Board(100, 100, Automata.CORAL, 50);
        int cellsCounterBefore = sut.getCellAliveCount();
        sut.iterateBoard();
        int cellsCounterAfter = sut.getCellAliveCount();
        assertFalse(compareCounters(cellsCounterAfter, cellsCounterBefore));
    }

    @Test
    void testMazeIteration() {
        sut = new Board(100, 100, Automata.MAZE, 50);
        int cellsCounterBefore = sut.getCellAliveCount();
        sut.iterateBoard();
        int cellsCounterAfter = sut.getCellAliveCount();
        assertFalse(compareCounters(cellsCounterAfter, cellsCounterBefore));
    }

    @Test
    void testMoveIteration() {
        sut = new Board(100, 100, Automata.MOVE, 50);
        int cellsCounterBefore = sut.getCellAliveCount();
        sut.iterateBoard();
        int cellsCounterAfter = sut.getCellAliveCount();
        assertFalse(compareCounters(cellsCounterAfter, cellsCounterBefore));
    }

    @Test
    void testServiettesIteration() {
        sut = new Board(100, 100, Automata.SERVIETTES, 50);
        int cellsCounterBefore = sut.getCellAliveCount();
        sut.iterateBoard();
        int cellsCounterAfter = sut.getCellAliveCount();
        assertFalse(compareCounters(cellsCounterAfter, cellsCounterBefore));
    }

    private boolean compareCounters(final int aliveCellsBefore, final int aliveCellsAfter) {
        return aliveCellsAfter == aliveCellsBefore;
    }
}
