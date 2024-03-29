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

import org.systemexception.lifegame.enums.BoardSizes;
import org.systemexception.lifegame.enums.GameSpeeds;
import org.systemexception.lifegame.menu.FileMenu;
import org.systemexception.lifegame.menu.LifeGameMenu;
import org.systemexception.lifegame.menu.PresetsMenu;
import org.systemexception.lifegame.menu.SpeedMenu;
import org.systemexception.lifegame.pojo.FileUtils;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
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
	public static JButton btnReset;
    public static JButton btnStop;
    public static JButton btnStart;
    public static JButton btnStep;
	public static JLabel lblCountIteration;
	public static Timer gameTimer;

	private static final Font labelFontBold = new Font(FONT_NAME, Font.BOLD, 10), labelFontPlain = new Font
			(FONT_NAME, Font.PLAIN, 10);
	private static final int INITIAL_SPEED = GameSpeeds.JACKRABBIT.getGameSpeed();
	private static final String PLATFORM = System.getProperty("os.name").toLowerCase();
	private static int iterationCounter;
	private static JLabel lblCountLiveCells;
	private static JPanel centerPanel;

	private static final int LABEL_HEIGHT = 29;
    private static final int LABEL_WIDTH = 75;
    private static final int PANEL_WIDTH = 390;

	private final int mainAppWindowHeightExclude;
    private final int panelAndLabelHeightExclude;

	private FileMenu menuFile;
	private JFrame mainAppWindow;
	private JLabel lblLiveCells;
    private JLabel lblIteration;
	private JPanel lowerPanel;

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
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
						UnsupportedLookAndFeelException e) {
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
		}
		if (PLATFORM.contains("mac")) {
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
		setUpStartButton();
		setUpStepButton();
		setUpStopButton();
		setUpResetButton();
		setUpLiveCellsCounter();
		setUpIterationCounter();
		setWindowSize();
	}

	private void iterateGrid() {
		menuFile.menuSave.setEnabled(true);
		gridGui.iterateBoard();
		menuFile.setBoard(gridGui.getBoard());
		iterationCounter++;
		lblCountLiveCells.setText(String.valueOf(gridGui.getTotalLiveCells()));
		lblCountIteration.setText(String.valueOf(iterationCounter));
	}

	private void resetGrid() {
		menuFile.menuSave.setEnabled(true);
		centerPanel.remove(gridGui);
		gridGui = new GridGui(PreferencesGui.getCellSize(), centerPanel.getWidth() / PreferencesGui.getCellSize(),
				centerPanel.getHeight() / PreferencesGui.getCellSize(), PreferencesGui.getColorTheme());
		gridGui.resetBoard();
		centerPanel.add(gridGui);
		iterationCounter = 0;
		lblCountLiveCells.setText(String.valueOf(gridGui.getTotalLiveCells()));
		lblCountIteration.setText(String.valueOf(iterationCounter));
	}

	private void stopGame() {
		menuFile.menuSave.setEnabled(true);
		menuFile.setBoard(gridGui.getBoard());
		if (gameTimer != null && gameTimer.isRunning()) {
			btnStart.setEnabled(true);
			gameTimer.stop();
		}
	}

	private final ActionListener taskPerformer = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			menuFile.menuSave.setEnabled(false);
			gridGui.iterateBoard();
			iterationCounter++;
			lblCountLiveCells.setText(String.valueOf(gridGui.getTotalLiveCells()));
			lblCountIteration.setText(String.valueOf(iterationCounter));
		}
	};

	public static void openFile(File selectedFile) throws IOException {
		btnStop.doClick();
		centerPanel.remove(gridGui);
		gridGui = FileUtils.gridGuiFromFile(selectedFile);
		centerPanel.add(gridGui);
		lblCountLiveCells.setText(String.valueOf(gridGui.getTotalLiveCells()));
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
		lowerPanel.setBounds(0, mainAppWindow.getHeight() - panelAndLabelHeightExclude, PANEL_WIDTH, LABEL_HEIGHT);
		lblLiveCells.setBounds(986, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH, LABEL_HEIGHT);
		lblCountLiveCells.setBounds(1073, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH,
                LABEL_HEIGHT);
		lblIteration.setBounds(1136, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH, LABEL_HEIGHT);
		lblCountIteration.setBounds(1223, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH,
                LABEL_HEIGHT);
	}

	private void setMediumWindowLayout() {
		mainAppWindow.setBounds(windowPositionX, windowPositionY, 1024, 768);
		centerPanel.setBounds(0, 25, mainAppWindow.getWidth(), mainAppWindow.getHeight() - mainAppWindowHeightExclude);
		lowerPanel.setBounds(0, mainAppWindow.getHeight() - panelAndLabelHeightExclude, PANEL_WIDTH, LABEL_HEIGHT);
		lblLiveCells.setBounds(700, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH, LABEL_HEIGHT);
		lblCountLiveCells.setBounds(787, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH,
                LABEL_HEIGHT);
		lblIteration.setBounds(860, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH, LABEL_HEIGHT);
		lblCountIteration.setBounds(947, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH,
                LABEL_HEIGHT);
	}

	private void setSmallWindowLayout() {
		mainAppWindow.setBounds(windowPositionX, windowPositionY, 800, 600);
		centerPanel.setBounds(0, 25, mainAppWindow.getWidth(), mainAppWindow.getHeight() - mainAppWindowHeightExclude);
		lowerPanel.setBounds(0, mainAppWindow.getHeight() - panelAndLabelHeightExclude, PANEL_WIDTH, LABEL_HEIGHT);
		lblLiveCells.setBounds(506, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH, LABEL_HEIGHT);
		lblCountLiveCells.setBounds(593, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH,
                LABEL_HEIGHT);
		lblIteration.setBounds(656, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH, LABEL_HEIGHT);
		lblCountIteration.setBounds(743, mainAppWindow.getHeight() - panelAndLabelHeightExclude, LABEL_WIDTH,
                LABEL_HEIGHT);
	}

	private void setUpLiveCellsCounter() {
		lblLiveCells = new JLabel("Live Cells:");
		mainAppWindow.getContentPane().add(lblLiveCells, BorderLayout.SOUTH);
		lblLiveCells.setFont(labelFontBold);
		lblCountLiveCells = new JLabel(String.valueOf(gridGui.getTotalLiveCells()));
		mainAppWindow.getContentPane().add(lblCountLiveCells, BorderLayout.SOUTH);
		lblCountLiveCells.setFont(labelFontPlain);
	}

	private void setUpIterationCounter() {
		lblIteration = new JLabel("Iteration:");
		mainAppWindow.getContentPane().add(lblIteration, BorderLayout.SOUTH);
		lblIteration.setFont(labelFontBold);
		lblCountIteration = new JLabel("0");
		mainAppWindow.getContentPane().add(lblCountIteration, BorderLayout.SOUTH);
		lblCountIteration.setFont(labelFontPlain);
	}

	private void setUpStartButton() {
		btnStart = new JButton("Start");
		btnStart.addActionListener(e -> {
			btnStart.setEnabled(false);
			if (gameTimer == null) {
				gameTimer = new Timer(INITIAL_SPEED, taskPerformer);
				gameTimer.start();
			} else {
				gameTimer.restart();
			}
		});
		lowerPanel.add(btnStart);
	}

	private void setUpStopButton() {
		btnStop = new JButton("Stop");
		btnStop.addActionListener(e -> stopGame());
		lowerPanel.add(btnStop);
	}

	private void setUpStepButton() {
		btnStep = new JButton("Step");
		btnStep.addActionListener(e -> {
			if (gameTimer != null && gameTimer.isRunning()) {
				btnStart.setEnabled(true);
				gameTimer.stop();
			}
			iterateGrid();
		});
		lowerPanel.add(btnStep);
	}

	private void setUpResetButton() {
		btnReset = new JButton("Reset");
		btnReset.addActionListener(e -> {
			setWindowSize();
			resetGrid();
			stopGame();
		});
		lowerPanel.add(btnReset);
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
		lowerPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) lowerPanel.getLayout();
		flowLayout.setHgap(0);
		flowLayout.setVgap(0);
		mainAppWindow.getContentPane().add(lowerPanel, BorderLayout.SOUTH);
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
		menuFileSetOpenAction();

		menuBar.add(menuLifeGame);
		menuBar.add(menuFile);
		menuBar.add(menuGameSpeed);
		menuBar.add(menuPresets);
	}

	private void menuFileSetOpenAction() {
		menuFile.menuOpen.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			FileFilter fileFilter = new FileNameExtensionFilter(APP_NAME, "life");
			fileChooser.setFileFilter(fileFilter);
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = fileChooser.showOpenDialog(fileChooser);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				try {
					openFile(selectedFile);
				} catch (IOException exception) {
					LOGGER.severe(exception.getMessage());
				}
			}
		});
	}

}
