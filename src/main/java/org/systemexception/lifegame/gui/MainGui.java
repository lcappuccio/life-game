/*
 LifeGame
 Copyright (C) 2014 Leonardo Cappuccio

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.systemexception.lifegame.gui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.systemexception.lifegame.enums.BoardSizes;
import org.systemexception.lifegame.enums.GameSpeeds;
import org.systemexception.lifegame.menu.FileMenu;
import org.systemexception.lifegame.menu.LifeGameMenu;
import org.systemexception.lifegame.menu.PresetsMenu;
import org.systemexception.lifegame.menu.SpeedMenu;
import org.systemexception.lifegame.pojo.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class MainGui {

    private static final Logger LOGGER = Logger.getLogger(MainGui.class.getName());

    public static final String APP_NAME = "LifeGame";
    public static final String FONT_NORMAL = "-fx-font-family: 'Lucida Grande'; -fx-font-size: 12px;";
    public static final String FONT_BOLD = "-fx-font-family: 'Lucida Grande'; -fx-font-size: 12px; -fx-font-weight: bold;";

    public static KeyCombination.Modifier metaKey;
    public static int windowPositionX;
    public static int windowPositionY;

    public static javafx.scene.control.Button btnReset;
    public static javafx.scene.control.Button btnStop;
    public static javafx.scene.control.Button btnStart;
    public static javafx.scene.control.Button btnStep;

    public static javafx.scene.control.Label lblCountIteration;
    private static javafx.scene.control.Label lblCountLiveCells;

    private static JPanel centerPanel;

    public static Timer gameTimer;

    private static final int INITIAL_SPEED = GameSpeeds.JACKRABBIT.getGameSpeed();
    private static final String PLATFORM = System.getProperty("os.name").toLowerCase();
    private static int iterationCounter;

    private static final int LABEL_HEIGHT = 29;

    private final int mainAppWindowHeightExclude;
    private final int panelAndLabelHeightExclude;

    private FileMenu menuFile;
    private FileUtils fileUtils;
    private JFXPanel menuBarPanel;
    private JFXPanel lowerPanel;
    private JFrame mainAppWindow;

    private PreferencesGui preferencesGui;
    public GridGui gridGui;

    private static MainGui mainGui;

    /**
     * Launch the application.
     *
     * @param args UNUSED
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(MainGui::getInstance);
    }

    public static void getInstance() {
        if (mainGui == null) {
            javafx.application.Platform.startup(() -> {
                // No need to do anything here
            });
            Platform.setImplicitExit(false);

            mainGui = new MainGui();
            mainGui.mainAppWindow.setVisible(true);
        }
    }

    /**
     * Create the application.
     */
    public MainGui() {
        mainAppWindowHeightExclude = 99;
        panelAndLabelHeightExclude = 70;

        // Set menu accelerator enabler key varies on PLATFORM
        if (PLATFORM.contains("linux") || PLATFORM.contains("windows")) {
            metaKey = KeyCombination.CONTROL_DOWN;
        } else if (PLATFORM.contains("mac")) {
            metaKey = KeyCombination.META_DOWN;
        }

        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        mainAppWindow = new JFrame();
        mainAppWindow.setTitle(APP_NAME + " - " + PLATFORM);
        mainAppWindow.setBounds(0, 0, 1280, 1024);
        mainAppWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainAppWindow.getContentPane().setLayout(null);
        mainAppWindow.setResizable(false);

        setUpMenuBar();
        setUpCenterPanel();
        setUpLowerPanel();
        setWindowSize();
    }

    private void iterateGrid() {
        menuFile.getMenuSave().setDisable(false);
        gridGui.iterateBoard();
        menuFile.setBoard(gridGui.getBoard());
        iterationCounter++;

        // Wrap in Platform.runLater to update JavaFX labels from Swing EDT
        int liveCells = gridGui.getTotalLiveCells();
        int iteration = iterationCounter;
        Platform.runLater(() -> {
            lblCountLiveCells.setText(String.valueOf(liveCells));
            lblCountIteration.setText(String.valueOf(iteration));
        });
    }

    private void resetGrid() {
        menuFile.getMenuSave().setDisable(false);
        centerPanel.remove(gridGui);
        gridGui = new GridGui(preferencesGui.getCellSize(), centerPanel.getWidth() / preferencesGui.getCellSize(),
                centerPanel.getHeight() / preferencesGui.getCellSize(), preferencesGui.getColorTheme());
        gridGui.resetBoard();
        centerPanel.add(gridGui);

        // Force Swing to update
        centerPanel.revalidate();
        centerPanel.repaint();

        iterationCounter = 0;

        // Wrap in Platform.runLater to update JavaFX labels from Swing EDT
        int liveCells = gridGui.getTotalLiveCells();
        Platform.runLater(() -> {
            lblCountLiveCells.setText(String.valueOf(liveCells));
            lblCountIteration.setText("0");
        });
    }

    private void stopGame() {
        menuFile.getMenuSave().setDisable(false);
        menuFile.setBoard(gridGui.getBoard());
        if (gameTimer != null && gameTimer.isRunning()) {
            btnStart.setDisable(false);
            gameTimer.stop();
        }
    }

    private final ActionListener taskPerformer = (ActionEvent evt) -> {
        menuFile.getMenuSave().setDisable(true);
        gridGui.iterateBoard();
        iterationCounter++;

        // Wrap in Platform.runLater to update JavaFX labels from Swing EDT
        int liveCells = gridGui.getTotalLiveCells();
        int iteration = iterationCounter;
        Platform.runLater(() -> {
            lblCountLiveCells.setText(String.valueOf(liveCells));
            lblCountIteration.setText(String.valueOf(iteration));
        });
    };

    public void openFile(File selectedFile) throws IOException {
        btnStop.fire();
        centerPanel.remove(gridGui);
        gridGui = fileUtils.gridGuiFromFile(selectedFile);
        centerPanel.add(gridGui);

        setWindowSize();
        centerPanel.revalidate();
        centerPanel.repaint();

        // Wrap in Platform.runLater to update JavaFX labels from Swing EDT
        int liveCells = gridGui.getTotalLiveCells();
        Platform.runLater(() -> lblCountLiveCells.setText(String.valueOf(liveCells)));
        iterationCounter = Integer.parseInt(lblCountIteration.getText());
    }

    private void setWindowSize() {
        if (!mainAppWindow.isVisible()) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            windowPositionX = (screenSize.width - mainAppWindow.getWidth()) / 2;
            windowPositionY = (screenSize.height - mainAppWindow.getHeight()) / 2;
        } else {
            windowPositionX = mainAppWindow.getX();
            windowPositionY = mainAppWindow.getY();
        }
        if (preferencesGui.getBoardSize().equals(BoardSizes.LARGE.toString())) {
            setLargeWindowLayout();
            return;
        }
        if (preferencesGui.getBoardSize().equals(BoardSizes.MEDIUM.toString())) {
            setMediumWindowLayout();
            return;
        }
        if (preferencesGui.getBoardSize().equals(BoardSizes.SMALL.toString())) {
            setSmallWindowLayout();
        }
    }

    private void setLargeWindowLayout() {
        mainAppWindow.setBounds(windowPositionX, windowPositionY, 1280, 1024);
        menuBarPanel.setBounds(0, 0, mainAppWindow.getWidth(), 25);
        centerPanel.setBounds(0, 25, mainAppWindow.getWidth(), mainAppWindow.getHeight() - mainAppWindowHeightExclude);
        lowerPanel.setBounds(0, mainAppWindow.getHeight() - panelAndLabelHeightExclude, mainAppWindow.getWidth() - 20, LABEL_HEIGHT);
    }

    private void setMediumWindowLayout() {
        mainAppWindow.setBounds(windowPositionX, windowPositionY, 1024, 768);
        menuBarPanel.setBounds(0, 0, mainAppWindow.getWidth(), 25);
        centerPanel.setBounds(0, 25, mainAppWindow.getWidth(), mainAppWindow.getHeight() - mainAppWindowHeightExclude);
        lowerPanel.setBounds(0, mainAppWindow.getHeight() - panelAndLabelHeightExclude, mainAppWindow.getWidth() - 20, LABEL_HEIGHT);
    }

    private void setSmallWindowLayout() {
        mainAppWindow.setBounds(windowPositionX, windowPositionY, 800, 600);
        menuBarPanel.setBounds(0, 0, mainAppWindow.getWidth(), 25);
        centerPanel.setBounds(0, 25, mainAppWindow.getWidth(), mainAppWindow.getHeight() - mainAppWindowHeightExclude);
        lowerPanel.setBounds(0, mainAppWindow.getHeight() - panelAndLabelHeightExclude, mainAppWindow.getWidth() - 20, LABEL_HEIGHT);
    }

    private void setUpCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setBounds(0, 25, mainAppWindow.getWidth(), mainAppWindow.getHeight() - mainAppWindowHeightExclude);
        mainAppWindow.getContentPane().add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(new BorderLayout(0, 0));
        gridGui = new GridGui(preferencesGui.getCellSize(), centerPanel.getWidth() / preferencesGui.getCellSize(),
                centerPanel.getHeight() / preferencesGui.getCellSize(), preferencesGui.getColorTheme());
        menuFile.setBoard(gridGui.getBoard());
        centerPanel.add(gridGui);
    }

    private void setUpLowerPanel() {
        // Create JavaFX panel to embed in Swing
        lowerPanel = new JFXPanel();

        // Set size BEFORE Platform.runLater
        lowerPanel.setPreferredSize(new Dimension(1280, LABEL_HEIGHT));

        // Build the JavaFX scene on the FX thread
        Platform.runLater(() -> {
            // Create buttons
            btnStart = new javafx.scene.control.Button("Start");
            btnStop = new javafx.scene.control.Button("Stop");
            btnStep = new javafx.scene.control.Button("Step");
            btnReset = new javafx.scene.control.Button("Reset");

            // Set button styles
            String buttonStyle = "-fx-font-size: 12px; -fx-padding: 5px 10px;";
            btnStart.setStyle(buttonStyle);
            btnStop.setStyle(buttonStyle);
            btnStep.setStyle(buttonStyle);
            btnReset.setStyle(buttonStyle);

            // Create button HBox
            HBox buttonBox = new HBox(5, btnStart, btnStop, btnStep, btnReset);
            buttonBox.setAlignment(Pos.CENTER_LEFT);
            buttonBox.setPadding(new javafx.geometry.Insets(5));

            // Create labels for live cells counter
            javafx.scene.control.Label lblLiveCells = new javafx.scene.control.Label("Live Cells:");
            lblLiveCells.setStyle(MainGui.FONT_BOLD);

            javafx.scene.control.Label lblCountLiveCellsFx = new javafx.scene.control.Label("0");
            lblCountLiveCellsFx.setStyle(MainGui.FONT_NORMAL);

            // Create labels for iteration counter
            javafx.scene.control.Label lblIteration = new javafx.scene.control.Label("Iteration:");
            lblIteration.setStyle(MainGui.FONT_BOLD);

            javafx.scene.control.Label lblCountIterationFx = new javafx.scene.control.Label("0");
            lblCountIterationFx.setStyle(MainGui.FONT_NORMAL);

            // Create spacer to push counters to the right
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Create counter HBox
            HBox counterBox = new HBox(5, lblLiveCells, lblCountLiveCellsFx, lblIteration, lblCountIterationFx);
            counterBox.setAlignment(Pos.CENTER_RIGHT);
            counterBox.setPadding(new javafx.geometry.Insets(5));

            // Combine everything in one HBox
            HBox mainBox = new HBox(buttonBox, spacer, counterBox);
            mainBox.setAlignment(Pos.CENTER_LEFT);

            // Create scene and attach to JFXPanel
            javafx.scene.Scene scene = new javafx.scene.Scene(mainBox);
            lowerPanel.setScene(scene);

            // Store references
            MainGui.lblCountLiveCells = lblCountLiveCellsFx;
            MainGui.lblCountIteration = lblCountIterationFx;

            // Set up event handlers
            setUpButtonEventHandlers(btnStart, btnStop, btnStep, btnReset);
        });

        // Add JFXPanel to Swing JFrame
        mainAppWindow.getContentPane().add(lowerPanel, BorderLayout.SOUTH);

        // Add this line to position it with null layout:
        lowerPanel.setBounds(0, mainAppWindow.getHeight() - LABEL_HEIGHT - 30, mainAppWindow.getWidth(), LABEL_HEIGHT);
    }

    private void setUpButtonEventHandlers(javafx.scene.control.Button btnStart,
                                          javafx.scene.control.Button btnStop,
                                          javafx.scene.control.Button btnStep,
                                          javafx.scene.control.Button btnReset) {
        // Start button handler
        btnStart.setOnAction(e -> {
            btnStart.setDisable(true);
            if (gameTimer == null) {
                gameTimer = new Timer(INITIAL_SPEED, taskPerformer);
                gameTimer.start();
            } else {
                gameTimer.restart();
            }
        });

        // Stop button handler
        btnStop.setOnAction(e -> stopGame());

        // Step button handler
        btnStep.setOnAction(e -> {
            if (gameTimer != null && gameTimer.isRunning()) {
                btnStart.setDisable(false);
                gameTimer.stop();
            }
            iterateGrid();
        });

        // Reset button handler
        btnReset.setOnAction(e -> {
            setWindowSize();
            resetGrid();
            stopGame();
        });
    }

    private void setUpMenuBar() {
        // Create JavaFX panel to embed menu bar
        menuBarPanel = new JFXPanel();

        // Create the menuFile instance synchronously BEFORE setUpCenterPanel is called
        preferencesGui = new PreferencesGui();
        menuFile = new FileMenu(preferencesGui);
        fileUtils = new FileUtils(preferencesGui);

        // Build the JavaFX MenuBar on the FX thread
        Platform.runLater(() -> {
            javafx.scene.control.MenuBar menuBar = new javafx.scene.control.MenuBar();

            // Create other menus
            LifeGameMenu menuLifeGame = new LifeGameMenu(preferencesGui);
            SpeedMenu menuGameSpeed = new SpeedMenu();
            PresetsMenu menuPresets = new PresetsMenu(this);

            // Set up file menu callback
            menuFile.setOnFileOpened(file -> {
                try {
                    openFile(file);
                } catch (IOException exception) {
                    LOGGER.severe(exception.getMessage());
                }
            });

            // Add menus to menu bar
            menuBar.getMenus().addAll(menuLifeGame, menuFile, menuGameSpeed, menuPresets);

            // Style the menu bar
            menuBar.setStyle(MainGui.FONT_NORMAL);

            // Create scene and attach to JFXPanel
            javafx.scene.Scene scene = new javafx.scene.Scene(menuBar);
            menuBarPanel.setScene(scene);
        });

        // Set size for the menu bar panel
        menuBarPanel.setPreferredSize(new Dimension(mainAppWindow.getWidth(), 25));

        // Add to main window
        mainAppWindow.getContentPane().add(menuBarPanel);
        menuBarPanel.setBounds(0, 0, mainAppWindow.getWidth(), 25);
    }

}
