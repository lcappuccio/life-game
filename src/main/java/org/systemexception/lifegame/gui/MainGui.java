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
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class MainGui {

    private static final Logger LOGGER = Logger.getLogger(MainGui.class.getName());

	public static final String FONT_NAME = "Lucida Grande", APP_NAME = "LifeGame";
	public static final Font MENU_FONT = new Font(FONT_NAME, Font.BOLD, 12);
	public static int metaKey, windowPositionX, windowPositionY;
	public static GridGui gridGui;
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
	private JFrame mainAppWindow;

	private JFXPanel lowerPanel;

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
			javafx.application.Platform.startup(() -> {});
			Platform.setImplicitExit(false);

			mainGui = new MainGui();
			mainGui.mainAppWindow.setVisible(true);
}
	}

	/**
	 * Create the application.
	 */
	public MainGui() {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			// Opt for Nimbus
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (Exception e) {
					LOGGER.severe(e.getMessage());
				}
				break;
			}
		}
		mainAppWindowHeightExclude = 99;
		panelAndLabelHeightExclude = 70;

		// Set menu accelerator enabler key varies on PLATFORM
		if (PLATFORM.contains("linux") || PLATFORM.contains("windows")) {
			metaKey = InputEvent.CTRL_DOWN_MASK;
		} else if (PLATFORM.contains("mac")) {
			metaKey = InputEvent.META_DOWN_MASK;
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
		menuFile.menuSave.setEnabled(true);
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
		menuFile.menuSave.setEnabled(true);
		centerPanel.remove(gridGui);
		gridGui = new GridGui(PreferencesGui.getCellSize(), centerPanel.getWidth() / PreferencesGui.getCellSize(),
				centerPanel.getHeight() / PreferencesGui.getCellSize(), PreferencesGui.getColorTheme());
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
		menuFile.menuSave.setEnabled(true);
		menuFile.setBoard(gridGui.getBoard());
		if (gameTimer != null && gameTimer.isRunning()) {
			btnStart.setDisable(false);
			gameTimer.stop();
		}
	}

	private final ActionListener taskPerformer = (ActionEvent evt) -> {
		menuFile.menuSave.setEnabled(false);
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

	public static void openFile(File selectedFile) throws IOException {
		btnStop.fire();
		centerPanel.remove(gridGui);
		gridGui = FileUtils.gridGuiFromFile(selectedFile);
		centerPanel.add(gridGui);

		// Force Swing to update
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
		if (PreferencesGui.getBoardSize().equals(BoardSizes.LARGE.toString())) {
			setLargeWindowLayout();
			return;
		}
		if (PreferencesGui.getBoardSize().equals(BoardSizes.MEDIUM.toString())) {
			setMediumWindowLayout();
			return;
		}
		if (PreferencesGui.getBoardSize().equals(BoardSizes.SMALL.toString())) {
			setSmallWindowLayout();
		}
	}

	private void setLargeWindowLayout() {
		mainAppWindow.setBounds(windowPositionX, windowPositionY, 1280, 1024);
		centerPanel.setBounds(0, 25, mainAppWindow.getWidth(), mainAppWindow.getHeight() - mainAppWindowHeightExclude);
		lowerPanel.setBounds(0, mainAppWindow.getHeight() - panelAndLabelHeightExclude, mainAppWindow.getWidth() - 20, LABEL_HEIGHT);
	}

	private void setMediumWindowLayout() {
		mainAppWindow.setBounds(windowPositionX, windowPositionY, 1024, 768);
		centerPanel.setBounds(0, 25, mainAppWindow.getWidth(), mainAppWindow.getHeight() - mainAppWindowHeightExclude);
		lowerPanel.setBounds(0, mainAppWindow.getHeight() - panelAndLabelHeightExclude, mainAppWindow.getWidth() - 20, LABEL_HEIGHT);
	}

	private void setSmallWindowLayout() {
		mainAppWindow.setBounds(windowPositionX, windowPositionY, 800, 600);
		centerPanel.setBounds(0, 25, mainAppWindow.getWidth(), mainAppWindow.getHeight() - mainAppWindowHeightExclude);
		lowerPanel.setBounds(0, mainAppWindow.getHeight() - panelAndLabelHeightExclude, mainAppWindow.getWidth() - 20, LABEL_HEIGHT);
	}

	private void setUpCenterPanel() {
		centerPanel = new JPanel();
		centerPanel.setBounds(0, 25, mainAppWindow.getWidth(), mainAppWindow.getHeight() - mainAppWindowHeightExclude);
		mainAppWindow.getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		gridGui = new GridGui(PreferencesGui.getCellSize(), centerPanel.getWidth() / PreferencesGui.getCellSize(),
				centerPanel.getHeight() / PreferencesGui.getCellSize(), PreferencesGui.getColorTheme());
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
			javafx.scene.control.Button btnStart = new javafx.scene.control.Button("Start");
			javafx.scene.control.Button btnStop = new javafx.scene.control.Button("Stop");
			javafx.scene.control.Button btnStep = new javafx.scene.control.Button("Step");
			javafx.scene.control.Button btnReset = new javafx.scene.control.Button("Reset");

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
			lblLiveCells.setStyle("-fx-font-family: 'Lucida Grande'; -fx-font-size: 10px; -fx-font-weight: bold;");

			javafx.scene.control.Label lblCountLiveCellsFx = new javafx.scene.control.Label("0");
			lblCountLiveCellsFx.setStyle("-fx-font-family: 'Lucida Grande'; -fx-font-size: 10px;");

			// Create labels for iteration counter
			javafx.scene.control.Label lblIteration = new javafx.scene.control.Label("Iteration:");
			lblIteration.setStyle("-fx-font-family: 'Lucida Grande'; -fx-font-size: 10px; -fx-font-weight: bold;");

			javafx.scene.control.Label lblCountIterationFx = new javafx.scene.control.Label("0");
			lblCountIterationFx.setStyle("-fx-font-family: 'Lucida Grande'; -fx-font-size: 10px;");

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
			MainGui.btnStart = btnStart;
			MainGui.btnStop = btnStop;
			MainGui.btnStep = btnStep;
			MainGui.btnReset = btnReset;
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
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, mainAppWindow.getWidth(), 20);
		mainAppWindow.getContentPane().add(menuBar, BorderLayout.NORTH);
		menuBar.setBorderPainted(false);

		JMenu menuLifeGame = new LifeGameMenu();
		JMenu menuGameSpeed = new SpeedMenu();
		JMenu menuPresets = new PresetsMenu();

		menuFile = new FileMenu();
		menuFile.setOnFileOpened(file -> {
			try {
				openFile(file);
			} catch (IOException exception) {
				LOGGER.severe(exception.getMessage());
			}
		});

		menuBar.add(menuLifeGame);
		menuBar.add(menuFile);
		menuBar.add(menuGameSpeed);
		menuBar.add(menuPresets);
	}

}
