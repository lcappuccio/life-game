package org.systemexception.lifegame.gui;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.systemexception.lifegame.pojo.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author leona 24/02/2017 21:30
 */
class FileUtilsTest {

    @BeforeAll
    static void setUp() {
        MainGui.getInstance();
    }

    @Test
    void should_read_preset_file() throws URISyntaxException, IOException {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        URI testFileUri = Objects.requireNonNull(systemClassLoader.getResource("presets/acorn.life")).toURI();
        File testFile = new File(testFileUri);
        GridGui gridGui = FileUtils.gridGuiFromFile(testFile);

        assertNotNull(gridGui);
        assertEquals(512, gridGui.getBoard().getBoardRows());
        assertEquals(344, gridGui.getBoard().getBoardCols());
        assertEquals(7, gridGui.getBoard().getCellAliveCount());
    }
}
