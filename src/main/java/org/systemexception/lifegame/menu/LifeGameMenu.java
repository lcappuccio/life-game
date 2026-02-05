package org.systemexception.lifegame.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.systemexception.lifegame.gui.AboutGui;
import org.systemexception.lifegame.gui.MainGui;
import org.systemexception.lifegame.gui.PreferencesGui;

public class LifeGameMenu extends Menu {

    public static final String MENU_ITEM_ABOUT = "About";
    public static final String MENU_ITEM_PREFERENCES = "Preferences";
    public static final String MENU_ITEM_QUIT = "Quit";

    private final PreferencesGui preferencesGui;

    public LifeGameMenu(PreferencesGui preferencesGui) {
        this.preferencesGui = preferencesGui;
        this.setText(MainGui.APP_NAME);
        this.getItems().addAll(menuAbout(), menuPreferences(), menuQuit());
    }

    private MenuItem menuAbout() {
        MenuItem menuAbout = new MenuItem(MENU_ITEM_ABOUT);
        menuAbout.setOnAction(e -> AboutGui.show(MainGui.windowPositionX, MainGui.windowPositionY));
        menuAbout.setAccelerator(new KeyCodeCombination(KeyCode.A, MainGui.metaKey));
        return menuAbout;
    }

    private MenuItem menuPreferences() {
        MenuItem menuPreferences = new MenuItem(MENU_ITEM_PREFERENCES);
        menuPreferences.setOnAction(e -> preferencesGui.show(MainGui.windowPositionX, MainGui.windowPositionY));
        menuPreferences.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, MainGui.metaKey));
        return menuPreferences;
    }

    private MenuItem menuQuit() {
        MenuItem menuQuit = new MenuItem(MENU_ITEM_QUIT);
        menuQuit.setOnAction(e -> System.exit(0));
        menuQuit.setAccelerator(new KeyCodeCombination(KeyCode.Q, MainGui.metaKey));
        return menuQuit;
    }
}