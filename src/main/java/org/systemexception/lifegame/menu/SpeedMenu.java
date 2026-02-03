package org.systemexception.lifegame.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.systemexception.lifegame.enums.GameSpeeds;
import org.systemexception.lifegame.gui.MainGui;

public class SpeedMenu extends Menu {

    public SpeedMenu() {
        this.setText("Speed");

        GameSpeeds[] speeds = GameSpeeds.values();
        for (int i = 0; i < speeds.length; i++) {
            final MenuItem speedMenuItem = new MenuItem(speeds[i].name());
            final int gameSpeed = speeds[i].getGameSpeed();

            // Key codes: VK_5 = KeyCode.DIGIT5, so we need DIGIT5, DIGIT4, DIGIT3, etc.
            KeyCode keyCode = getKeyCodeForIndex(i);
            speedMenuItem.setAccelerator(new KeyCodeCombination(keyCode, MainGui.metaKey));

            speedMenuItem.setOnAction(e -> {
                if (MainGui.gameTimer != null && MainGui.gameTimer.isRunning()) {
                    MainGui.gameTimer.setDelay(gameSpeed);
                    resetFont();
                    speedMenuItem.setStyle(MainGui.FONT_NORMAL);
                }
            });

            speedMenuItem.setStyle(MainGui.FONT_NORMAL);
            this.getItems().add(speedMenuItem);
        }
    }

    private void resetFont() {
        for (MenuItem item : this.getItems()) {
            item.setStyle(MainGui.FONT_NORMAL);
        }
    }

    private KeyCode getKeyCodeForIndex(int index) {
        // GameSpeeds.values() order determines the key: 5-index
        // Index 0 -> KeyCode.DIGIT5
        // Index 1 -> KeyCode.DIGIT4
        // Index 2 -> KeyCode.DIGIT3, etc.
        switch (5 - index) {
            case 5: return KeyCode.DIGIT5;
            case 4: return KeyCode.DIGIT4;
            case 3: return KeyCode.DIGIT3;
            case 2: return KeyCode.DIGIT2;
            case 1: return KeyCode.DIGIT1;
            default: return KeyCode.DIGIT0;
        }
    }
}