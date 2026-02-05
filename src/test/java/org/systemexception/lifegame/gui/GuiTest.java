package org.systemexception.lifegame.gui;

import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.systemexception.lifegame.enums.BoardSizes;
import org.systemexception.lifegame.enums.GameSpeeds;
import org.systemexception.lifegame.enums.Themes;
import org.systemexception.lifegame.menu.FileMenu;
import org.systemexception.lifegame.menu.LifeGameMenu;
import org.systemexception.lifegame.menu.PresetsMenu;
import org.systemexception.lifegame.menu.SpeedMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class GuiTest {

    private PreferencesGui preferencesGui= new PreferencesGui();
    private LifeGameMenu lifeGameMenu;
    private FileMenu fileMenu;
    private PresetsMenu presetsMenu;
    private SpeedMenu speedMenu;

    @BeforeEach
    void setUp() throws InterruptedException {
        MainGui.getInstance();

        waitForFxInitialization();

        lifeGameMenu = new LifeGameMenu(preferencesGui);
        fileMenu = new FileMenu(preferencesGui);
        presetsMenu = new PresetsMenu();
        speedMenu = new SpeedMenu();
    }

    @Test
    void fileMenuTest() {
        assertEquals(2, fileMenu.getItems().size());
        List<String> menuItems = new ArrayList<>();
        for (int i = 0; i < fileMenu.getItems().size(); i++) {
            MenuItem jMenuItem = fileMenu.getItems().get(i);
            menuItems.add(jMenuItem.getText());
        }
        assertTrue(menuItems.contains(FileMenu.FILE_OPEN));
        assertTrue(menuItems.contains(FileMenu.FILE_SAVE));
    }

    @Test
    void lifeGameMenuTest() {
        assertEquals(3, lifeGameMenu.getItems().size());
        List<String> menuItems = new ArrayList<>();
        for (int i = 0; i < lifeGameMenu.getItems().size(); i++) {
            menuItems.add(lifeGameMenu.getItems().get(i).getText());
            if (!LifeGameMenu.MENU_ITEM_QUIT.equals(lifeGameMenu.getItems().get(i).getText())) {
                lifeGameMenu.getItems().get(i).fire();
            }
        }
        assertTrue(menuItems.contains(LifeGameMenu.MENU_ITEM_ABOUT));
        assertTrue(menuItems.contains(LifeGameMenu.MENU_ITEM_PREFERENCES));
        assertTrue(menuItems.contains(LifeGameMenu.MENU_ITEM_QUIT));
    }

    @Test
    void presetsMenuTest() {
        assertEquals(7, presetsMenu.getItems().size());
        List<String> menuItems = new ArrayList<>();
        for (int i = 0; i < presetsMenu.getItems().size(); i++) {
            menuItems.add(presetsMenu.getItems().get(i).getText());
            presetsMenu.getItems().get(i).fire();
        }
        assertTrue(menuItems.contains(PresetsMenu.PRESET_7468M));
        assertTrue(menuItems.contains(PresetsMenu.PRESET_ACORN));
        assertTrue(menuItems.contains(PresetsMenu.PRESET_B_HEPTOMINO));
        assertTrue(menuItems.contains(PresetsMenu.PRESET_EMPTY_BOARD));
        assertTrue(menuItems.contains(PresetsMenu.PRESET_R_PENTOMINO));
        assertTrue(menuItems.contains(PresetsMenu.PRESET_RABBITS));
        assertTrue(menuItems.contains(PresetsMenu.PRESET_CONWAY_SINGLE_LINE));
    }

    @Test
    void speedMenuTest() {
        assertEquals(6, (long) speedMenu.getItems().size());
        List<String> menuItems = new ArrayList<>();
        for (int i = 0; i < speedMenu.getItems().size(); i++) {
            speedMenu.getItems().forEach(MenuItem::fire);
            MenuItem jMenuItem = speedMenu.getItems().get(i);
            String speedText = jMenuItem.getText();
            menuItems.add(speedText);
        }
        assertTrue(menuItems.contains(GameSpeeds.NEUTRINO.name()));
        assertTrue(menuItems.contains(GameSpeeds.CHEETAH.name()));
        assertTrue(menuItems.contains(GameSpeeds.HORSE.name()));
        assertTrue(menuItems.contains(GameSpeeds.JACKRABBIT.name()));
        assertTrue(menuItems.contains(GameSpeeds.LLAMA.name()));
        assertTrue(menuItems.contains(GameSpeeds.TURTLE.name()));
    }

    @Test
    void testStartStop() {
        MainGui.btnStart.fire();
        await().atLeast(2L, TimeUnit.SECONDS).await();
        MainGui.btnStop.fire();
        assertFalse(MainGui.gameTimer.isRunning());
    }

    @Test
    void testReset() {
        MainGui.btnReset.fire();
        assertFalse(MainGui.gameTimer.isRunning());
    }

    @Test
    void testSingleIteration() {
        MainGui.btnStep.fire();
        assertFalse(MainGui.gameTimer.isRunning());
    }

    @Test
    void testChangeTheme() {
        int iterationCounter = 0;
        Themes[] values = Themes.values();
        for (Themes value : values) {
            MainGui.gridGui.setColours(value.toString());
            iterationCounter++;
        }
        assertEquals(iterationCounter, values.length);
    }

    @Test
    void testChangeSpeed() {
        MainGui.btnStart.fire();
        await().atLeast(2L, TimeUnit.SECONDS).await();
        for (int i = 0; i < speedMenu.getItems().size(); i++) {
            speedMenu.getItems().get(i).fire();
        }
        MainGui.btnStop.fire();
        assertFalse(MainGui.gameTimer.isRunning());
    }

    @Test
    void testBoardSizes() {
        int iterationCounter = 0;

        preferencesGui.setBoardSize(BoardSizes.LARGE.toString());
        MainGui.btnReset.fire();
        iterationCounter++;

        preferencesGui.setBoardSize(BoardSizes.MEDIUM.toString());
        MainGui.btnReset.fire();
        iterationCounter++;

        preferencesGui.setBoardSize(BoardSizes.SMALL.toString());
        MainGui.btnReset.fire();
        iterationCounter++;

        assertEquals(3, iterationCounter);
    }

    private void waitForFxInitialization() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        latch.await(5, java.util.concurrent.TimeUnit.SECONDS);
    }
}
