package org.systemexception.lifegame.menu;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.systemexception.lifegame.enums.SavedBoardProperties;
import org.systemexception.lifegame.gui.MainGui;
import org.systemexception.lifegame.gui.PreferencesGui;
import org.systemexception.lifegame.model.Board;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileMenu extends JMenu {

    private static final Logger LOGGER = Logger.getLogger(FileMenu.class.getName());
    public static final String FILE_PROPERTIES_LINE = "#";
    public static final String FILE_OPEN = "Open";
    public static final String FILE_SAVE = "Save";

    private static final String SAVE_FILE_EXTENSION = ".life";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String FILE_PROPERTIES_SEPARATOR = "=";

    public JMenuItem menuOpen;
    public JMenuItem menuSave;

    private transient Board board;
    private transient Consumer<File> onFileOpened;
    private transient Stage fileChooserStage;

    public FileMenu() {
        this.setFont(MainGui.MENU_FONT);
        this.setText("File");
        this.add(menuOpen());
        this.add(menuSave());
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Set callback for when a file is opened successfully.
     * Called from Swing EDT after file is loaded.
     */
    public void setOnFileOpened(Consumer<File> onFileOpened) {
        this.onFileOpened = onFileOpened;
    }

    private JMenuItem menuOpen() {
        menuOpen = new JMenuItem(FILE_OPEN);
        menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, MainGui.metaKey));
        menuOpen.addActionListener(e -> {
            Platform.runLater(() -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open File");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("LifeGame Files", "*.life")
                );
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

                File selectedFile = fileChooser.showOpenDialog(fileChooserStage);

                if (selectedFile != null && onFileOpened != null) {
                    // Execute callback on Swing EDT
                    SwingUtilities.invokeLater(() -> {
                        try {
                            onFileOpened.accept(selectedFile);
                        } catch (Exception ex) {
                            LOGGER.log(Level.SEVERE, ex.getMessage());
                        }
                    });
                }
            });
        });
        return menuOpen;
    }

    private JMenuItem menuSave() {
        menuSave = new JMenuItem(FILE_SAVE);
        menuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MainGui.metaKey));
        menuSave.addActionListener(e -> {
            Platform.runLater(() -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save File");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("LifeGame Files", "*.life")
                );
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                fileChooser.setInitialFileName("game" + SAVE_FILE_EXTENSION);

                File file = fileChooser.showSaveDialog(fileChooserStage);

                if (file != null) {
                    SwingUtilities.invokeLater(() -> handleSaveFile(file));
                }
            });
        });
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
}