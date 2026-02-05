package org.systemexception.lifegame.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PresetsMenu extends Menu {

    private static final Logger LOGGER = Logger.getLogger(PresetsMenu.class.getName());

    public static final String PRESET_7468M = "7468M.life";
    public static final String PRESET_ACORN = "acorn.life";
    public static final String PRESET_B_HEPTOMINO = "b_heptomino.life";
    public static final String PRESET_EMPTY_BOARD = "empty_board.life";
    public static final String PRESET_R_PENTOMINO = "r_pentomino.life";
    public static final String PRESET_RABBITS = "rabbits.life";
    public static final String PRESET_CONWAY_SINGLE_LINE = "single_line_conway.life";
    private static final String PRESETS_FOLDER = "/presets/";
    private static final String TEMP_LIFE_PRESET = "target" + File.separator + "temp.life";

    public PresetsMenu() {
        this.setText("Presets");
        this.getItems().addAll(
                buildMenuItem(PRESET_7468M),
                buildMenuItem(PRESET_ACORN),
                buildMenuItem(PRESET_B_HEPTOMINO),
                buildMenuItem(PRESET_EMPTY_BOARD),
                buildMenuItem(PRESET_R_PENTOMINO),
                buildMenuItem(PRESET_RABBITS),
                buildMenuItem(PRESET_CONWAY_SINGLE_LINE)
        );
    }

    private MenuItem buildMenuItem(final String fileName) {
        MenuItem menuItem = new MenuItem(fileName);
        menuItem.setOnAction(event -> {
            try (InputStream inputStream = this.getClass().getResourceAsStream(PRESETS_FOLDER + fileName)) {
                if (inputStream == null) {
                    throw new IOException("Missing presets folder");
                }
                FileOutputStream fileOutputStream = new FileOutputStream(TEMP_LIFE_PRESET);
                int read;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, read);
                }
                fileOutputStream.close();
                File tempFile = new File(TEMP_LIFE_PRESET);

                // MainGui.openFile is called from Swing EDT, wrap in SwingUtilities
                SwingUtilities.invokeLater(() -> {
                    try {
                        // TODO fix file opening
                        //MainGui.openFile(tempFile);
                        tempFile.deleteOnExit();
                        throw new IOException();
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, ex.getMessage());
                    }
                });
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage());
            }
        });
        return menuItem;
    }
}