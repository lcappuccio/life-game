/**
 * @author leo
 * Jan 28, 2015 10:25:38 PM
 */
package org.systemexception.lifegame.menu;

import org.systemexception.lifegame.enums.SavedBoardProperties;
import org.systemexception.lifegame.gui.MainGui;
import org.systemexception.lifegame.gui.PreferencesGui;
import org.systemexception.lifegame.model.Board;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;

public class FileMenu extends JMenu {

	public JMenuItem menuOpen, menuSave;
	private Board board;
	private final String lineSeparator = System.getProperty("line.separator");

	public FileMenu() {
		this.setFont(MainGui.MENU_FONT);
		UIManager.put("FileChooser.readOnly", Boolean.FALSE);
		this.setText("File");
		this.add(menuOpen());
		this.add(menuSave());
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	private JMenuItem menuOpen() {
		menuOpen = new JMenuItem("Open");
		menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, MainGui.metaKey));
		return (menuOpen);
	}

	private JMenuItem menuSave() {
		menuSave = new JMenuItem("Save");
		menuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MainGui.metaKey));
		menuSave.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			fileChooser.showSaveDialog(null);
			File file = fileChooser.getSelectedFile();
			boolean exists = true;
			// Check if file exists
			if (file != null) {
				exists = file.exists();
			}
			if (exists && file != null) {
				int response = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to overwrite existing file?", "Confirm", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION) {
					if (!file.delete()) {
						JOptionPane.showMessageDialog(null, "Error overwriting file", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						saveFile(file);
					}
				}
			} else {
				saveFile(file);
			}
		});
		return (menuSave);
	}

	private void saveFile(final File file) {
		try {
			String fileName = file.getAbsolutePath();
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
			// Save board properties first
			try (PrintWriter fileWriter = new PrintWriter(bufferedWriter)) {
				// Save board properties first
				fileWriter.print("#" + SavedBoardProperties.COLS + "=" + board.getBoardCols() + lineSeparator);
				fileWriter.print("#" + SavedBoardProperties.ROWS + "=" + board.getBoardRows() + lineSeparator);
				fileWriter.print("#" + SavedBoardProperties.CELLSIZE + "=" + PreferencesGui.getCellSize() +
						lineSeparator);
				fileWriter.print("#" + SavedBoardProperties.AUTOMATA + "=" + PreferencesGui.getLifeAutomata() +
						lineSeparator);
				fileWriter.print("#" + SavedBoardProperties.THEME + "=" + PreferencesGui.getColorTheme() +
						lineSeparator);
				writeToFile(fileWriter);
			}
			if (!fileName.endsWith(".life")) {
				Files.move(file.toPath(), new File(fileName + ".life").toPath());
			}
		} catch (Exception fileException) {
			fileException.printStackTrace(System.out);
		}
	}

	private void writeToFile(PrintWriter fileWriter) {
		for (int i = 0; i < board.getBoardCols(); i++) {
			for (int j = 0; j < board.getBoardRows(); j++) {
				if (board.getCellAt(j, i).getCellState()) {
					fileWriter.print(Board.ALIVE_CELL);
				} else {
					fileWriter.print(Board.DEAD_CELL);
				}
			}
			fileWriter.print(lineSeparator);
		}
	}
}
