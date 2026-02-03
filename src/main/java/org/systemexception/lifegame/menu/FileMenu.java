package org.systemexception.lifegame.menu;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.systemexception.lifegame.enums.SavedBoardProperties;
import org.systemexception.lifegame.gui.MainGui;
import org.systemexception.lifegame.gui.PreferencesGui;
import org.systemexception.lifegame.model.Board;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileMenu extends Menu {

    private static final Logger LOGGER = Logger.getLogger(FileMenu.class.getName());
    public static final String FILE_PROPERTIES_LINE = "#";
    public static final String FILE_OPEN = "Open";
    public static final String FILE_SAVE = "Save";

    private static final String SAVE_FILE_EXTENSION = ".life";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String FILE_PROPERTIES_SEPARATOR = "=";

    private MenuItem menuOpen;
    private MenuItem menuSave;

    private Board board;

    public FileMenu() {
        this.setText("File");
        this.getItems().addAll(menuOpen(), menuSave());
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public MenuItem getMenuOpen() {
        return menuOpen;
    }

    public MenuItem getMenuSave() {
        return menuSave;
    }

    private MenuItem menuOpen() {
        menuOpen = new MenuItem(FILE_OPEN);
        menuOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, MainGui.metaKey));
        menuOpen.setOnAction(e ->
                Platform.runLater(() -> {
                    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                    fileChooser.setTitle("Open File");
                    fileChooser.getExtensionFilters().add(
                            new javafx.stage.FileChooser.ExtensionFilter("LifeGame Files", "*.life")
                    );
                    fileChooser.setInitialDirectory(new java.io.File(System.getProperty("user.home")));

                    java.io.File selectedFile = fileChooser.showOpenDialog(getFileChooserStage());

                    if (selectedFile != null) {
                        try {
                            MainGui.openFile(selectedFile);
                        } catch (IOException exception) {
                            LOGGER.severe(exception.getMessage());
                        }
                    }
                }));
        return menuOpen;
    }

    private MenuItem menuSave() {
        menuSave = new MenuItem(FILE_SAVE);
        menuSave.setAccelerator(new KeyCodeCombination(KeyCode.S, MainGui.metaKey));
        menuSave.setOnAction(e ->
                Platform.runLater(() -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save File");
                    fileChooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("LifeGame Files", "*.life")
                    );
                    fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                    fileChooser.setInitialFileName("game" + SAVE_FILE_EXTENSION);

                    File file = fileChooser.showSaveDialog(getFileChooserStage());

                    if (file != null) {
                        handleSaveFile(file);
                    }
                })
        );
        return menuSave;
    }

    private void handleSaveFile(File file) {
        try {
            saveFile(file);
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, exception.getMessage());
        }
    }

    private void saveFile(File file) throws IOException {
        String fileName = file.getAbsolutePath();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false)); // false = overwrite

        try (PrintWriter fileWriter = new PrintWriter(bufferedWriter)) {
            // Write board properties
            fileWriter.print(FILE_PROPERTIES_LINE + SavedBoardProperties.COLS +
                    FILE_PROPERTIES_SEPARATOR + board.getBoardCols() + LINE_SEPARATOR);
            fileWriter.print(FILE_PROPERTIES_LINE + SavedBoardProperties.ROWS +
                    FILE_PROPERTIES_SEPARATOR + board.getBoardRows() + LINE_SEPARATOR);
            fileWriter.print(FILE_PROPERTIES_LINE + SavedBoardProperties.CELLSIZE +
                    FILE_PROPERTIES_SEPARATOR + PreferencesGui.getCellSize() + LINE_SEPARATOR);
            fileWriter.print(FILE_PROPERTIES_LINE + SavedBoardProperties.AUTOMATA +
                    FILE_PROPERTIES_SEPARATOR + PreferencesGui.getLifeAutomata() + LINE_SEPARATOR);
            fileWriter.print(FILE_PROPERTIES_LINE + SavedBoardProperties.ITERATION_COUNTER +
                    FILE_PROPERTIES_SEPARATOR + MainGui.lblCountIteration.getText() + LINE_SEPARATOR);
            fileWriter.print(FILE_PROPERTIES_LINE + SavedBoardProperties.THEME +
                    FILE_PROPERTIES_SEPARATOR + PreferencesGui.getColorTheme() + LINE_SEPARATOR);
            writeToFile(fileWriter);
        }

        if (!fileName.endsWith(SAVE_FILE_EXTENSION)) {
            Files.move(file.toPath(), new File(fileName + SAVE_FILE_EXTENSION).toPath());
        }
    }

    private void writeToFile(PrintWriter fileWriter) {
        for (int i = 0; i < board.getBoardCols(); i++) {
            for (int j = 0; j < board.getBoardRows(); j++) {
                if (board.getCellAt(j, i)) {
                    fileWriter.print(Board.ALIVE_CELL);
                } else {
                    fileWriter.print(Board.DEAD_CELL);
                }
            }
            fileWriter.print(LINE_SEPARATOR);
        }
    }

    // Expose a Stage suitable for use by FileChooser dialogs. Made public so callers (e.g. MainGui) can use it from the FX thread.
    public Stage getFileChooserStage() {
        Stage fileChooserStage = new Stage();
        fileChooserStage.setWidth(0);
        fileChooserStage.setHeight(0);
        fileChooserStage.setOpacity(0);
        return fileChooserStage;
    }
}
