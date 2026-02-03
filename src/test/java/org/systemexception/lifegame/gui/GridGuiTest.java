package org.systemexception.lifegame.gui;

import org.junit.jupiter.api.Test;
import org.systemexception.lifegame.model.Board;

import static org.junit.jupiter.api.Assertions.*;

class GridGuiTest {

    private final int cellSize = 10, gridRows = 10, gridCols = 5;
    private final GridGui sut = new GridGui(cellSize, gridRows, gridCols, "B & W");

    @Test
    void testGridExists() {
        assertNotNull(sut);
    }

    @Test
    void testGridHasBoard() {
        assertNotNull(sut.getBoard());
    }

    @Test
    void testGridBoardHasAttributes() {
        Board board = sut.getBoard();
        assertTrue(board.getBoardCols() > 0);
        assertTrue(board.getBoardRows() > 0);
    }

    @Test
    void testChangeCellSize() {
        int innerCellSize = sut.getCellSize();
        GridGui innerSut = new GridGui(5, gridRows, gridCols, "B & W");
        int cellSizeNew = innerSut.getCellSize();
        assertNotEquals(innerCellSize, cellSizeNew);
    }

}
