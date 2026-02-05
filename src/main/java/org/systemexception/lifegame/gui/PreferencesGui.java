package org.systemexception.lifegame.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.systemexception.lifegame.enums.Automata;
import org.systemexception.lifegame.enums.BoardSizes;
import org.systemexception.lifegame.enums.Themes;

public class PreferencesGui {

    private static final int WINDOW_WIDTH = 270;
    private static final int WINDOW_HEIGHT = 240;
    private static final int MIN_CELL_SIZE = 1;
    private static final int MAX_CELL_SIZE = 4;
    private static final int MIN_CELL_LIFE_PROBABILITY = 0;
    private static final int MAX_CELL_LIFE_PROBABILITY = 100;

    private int cellSize = 2;
    private int cellLifeProbability = 50;
    private String colourTheme = Themes.BW.toString();
    private Automata lifeAutomata = Automata.CONWAY;
    private String boardSize = BoardSizes.MEDIUM.toString();

    public PreferencesGui() {
        // Defaults already set above
    }

    // Optionally allow passing initial values
    public PreferencesGui(int cellSize, int cellLifeProbability, String colourTheme, Automata lifeAutomata, String boardSize) {
        this.cellSize = cellSize;
        this.cellLifeProbability = cellLifeProbability;
        this.colourTheme = colourTheme;
        this.lifeAutomata = lifeAutomata;
        this.boardSize = boardSize;
    }

    /**
     * Launch the JavaFX Preferences window from the Swing event thread.
     * Usage: PreferencesGui preferencesGui = new PreferencesGui(); preferencesGui.show(parentX, parentY);
     *
     * @param parentX MainGui.windowPositionX (used to offset the window)
     * @param parentY MainGui.windowPositionY
     */
    public void show(int parentX, int parentY) {
        Platform.runLater(() -> {
            Stage stage = new Stage(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Preferences");
            stage.setResizable(false);

            // Create controls
            Label lblCellSize = new Label("Cell Size");
            Spinner<Integer> spinnerCellSize = new Spinner<>(MIN_CELL_SIZE, MAX_CELL_SIZE, cellSize, 1);
            spinnerCellSize.setEditable(true);
            spinnerCellSize.setMaxWidth(Double.MAX_VALUE);

            Label lblTheme = new Label("Theme");
            ComboBox<String> comboBoxTheme = new ComboBox<>();
            comboBoxTheme.getItems().addAll(
                    Themes.BW.toString(),
                    Themes.INVERSE.toString(),
                    Themes.BLUE.toString(),
                    Themes.GREEN.toString(),
                    Themes.RED.toString()
            );
            comboBoxTheme.setValue(colourTheme);
            comboBoxTheme.setMaxWidth(Double.MAX_VALUE);

            Label lblAutomata = new Label("Automata");
            ComboBox<Automata> comboBoxAutomata = new ComboBox<>();
            comboBoxAutomata.getItems().addAll(
                    Automata.ASSIMILATION,
                    Automata.CONWAY,
                    Automata.CORAL,
                    Automata.DRYLIFE,
                    Automata.HIGHLIFE,
                    Automata.LIVEFREEORDIE,
                    Automata.MAZE,
                    Automata.MOVE,
                    Automata.SERVIETTES
            );
            comboBoxAutomata.setValue(lifeAutomata);
            comboBoxAutomata.setMaxWidth(Double.MAX_VALUE);

            Label lblLifeProbability = new Label("Life Probability");
            Spinner<Integer> spinnerLifeProbability = new Spinner<>(MIN_CELL_LIFE_PROBABILITY,
                    MAX_CELL_LIFE_PROBABILITY, cellLifeProbability, 1);
            spinnerLifeProbability.setEditable(true);
            spinnerLifeProbability.setMaxWidth(Double.MAX_VALUE);

            Label lblBoardSize = new Label("Board Size");
            ComboBox<String> comboBoxBoardSize = new ComboBox<>();
            comboBoxBoardSize.getItems().addAll(
                    BoardSizes.SMALL.toString(),
                    BoardSizes.MEDIUM.toString(),
                    BoardSizes.LARGE.toString()
            );
            comboBoxBoardSize.setValue(boardSize);
            comboBoxBoardSize.setMaxWidth(Double.MAX_VALUE);

            Button btnApply = new Button("Apply");
            Button btnCancel = new Button("Cancel");

            // Create layout
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(15, 15, 15, 15));

            grid.add(lblCellSize, 0, 0);
            grid.add(spinnerCellSize, 1, 0);
            grid.add(lblTheme, 0, 1);
            grid.add(comboBoxTheme, 1, 1);
            grid.add(lblAutomata, 0, 2);
            grid.add(comboBoxAutomata, 1, 2);
            grid.add(lblLifeProbability, 0, 3);
            grid.add(spinnerLifeProbability, 1, 3);
            grid.add(lblBoardSize, 0, 4);
            grid.add(comboBoxBoardSize, 1, 4);

            HBox buttonBox = new HBox(10, btnApply, btnCancel);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));
            buttonBox.setMaxWidth(Double.MAX_VALUE);
            grid.add(buttonBox, 0, 5, 2, 1);

            Scene scene = new Scene(grid, WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.setX(parentX + 25d);
            stage.setY(parentY + 25d);

            // Set action handlers
            btnApply.setOnAction(e -> {
                // Update instance values from controls
                cellSize = spinnerCellSize.getValue();
                colourTheme = comboBoxTheme.getValue();
                lifeAutomata = comboBoxAutomata.getValue();
                cellLifeProbability = spinnerLifeProbability.getValue();
                boardSize = comboBoxBoardSize.getValue();

                // Reset the grid in MainGui
                if (MainGui.gridGui != null) {
                    Platform.runLater(() -> MainGui.btnReset.fire());
                }

                stage.close();
            });

            btnCancel.setOnAction(e -> stage.close());

            stage.showAndWait();
        });
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public String getColorTheme() {
        return colourTheme;
    }

    public void setColorTheme(String colourTheme) {
        this.colourTheme = colourTheme;
    }

    public Automata getLifeAutomata() {
        return lifeAutomata;
    }

    public void setLifeAutomata(Automata automata) {
        this.lifeAutomata = automata;
    }

    public int getCellLifeProbability() {
        return cellLifeProbability;
    }

    public void setCellLifeProbability(int cellLifeProbability) {
        this.cellLifeProbability = cellLifeProbability;
    }

    public String getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(String boardSize) {
        this.boardSize = boardSize;
    }
}
