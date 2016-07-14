/**
 * @author leo
 * Jan 26, 2015 8:20:59 PM
 */
package org.systemexception.lifegame.menu;

import org.systemexception.lifegame.gui.AboutGui;
import org.systemexception.lifegame.gui.MainGui;
import org.systemexception.lifegame.gui.PreferencesGui;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class LifeGameMenu extends JMenu {


	public LifeGameMenu() {
		this.setFont(MainGui.MENU_FONT);
		this.setText("LifeGame");
		this.add(menuAbout());
		this.add(menuPreferences());
		this.add(menuQuit());
	}

	private JMenuItem menuAbout() {
		JMenuItem menuAbout = new JMenuItem("About");
		menuAbout.addActionListener(e -> {
			AboutGui aboutGui = new AboutGui();
			aboutGui.setBounds(MainGui.windowPositionX + 25, MainGui.windowPositionY + 25, aboutGui.getWidth(),
					aboutGui.getHeight());
			aboutGui.setVisible(true);
		});
		menuAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, MainGui.metaKey));
		return (menuAbout);
	}

	private JMenuItem menuPreferences() {
		PreferencesGui preferencesGuiWindow = new PreferencesGui();
		JMenuItem menuPreferences = new JMenuItem("Preferences");
		menuPreferences.addActionListener(e -> {
			preferencesGuiWindow.setVisible(true);
			preferencesGuiWindow.setBounds(MainGui.windowPositionX + 25, MainGui.windowPositionY + 25,
					preferencesGuiWindow.getWidth(), preferencesGuiWindow.getHeight());
		});
		menuPreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, MainGui.metaKey));
		return (menuPreferences);
	}

	private JMenuItem menuQuit() {
		JMenuItem menuQuit = new JMenuItem("Quit");
		menuQuit.addActionListener(e -> System.exit(0));
		menuQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, MainGui.metaKey));
		return (menuQuit);
	}
}
