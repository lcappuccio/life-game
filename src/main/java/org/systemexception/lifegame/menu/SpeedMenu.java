/**
 * @author leo
 * Jan 26, 2015 9:35:36 PM
 */
package org.systemexception.lifegame.menu;

import com.sun.glass.events.KeyEvent;
import org.systemexception.lifegame.enums.GameSpeeds;
import org.systemexception.lifegame.gui.Main;

import javax.swing.*;
import java.awt.*;

public class SpeedMenu extends JMenu {

	public SpeedMenu() {
		this.setFont(Main.MENU_FONT);
		this.setText("Speed");
		for (int i = 0; i < GameSpeeds.values().length; i++) {
			final JMenuItem speedMenuItem = new JMenuItem(GameSpeeds.values()[i].name());
			final int gameSpeed = GameSpeeds.values()[i].getGameSpeed();
			speedMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5 - i, Main.metaKey));
			speedMenuItem.addActionListener(e -> {
				if (Main.gameTimer != null && Main.gameTimer.isRunning()) {
					Main.gameTimer.setDelay(gameSpeed);
					resetFont();
					speedMenuItem.setFont(Main.MENU_FONT.deriveFont(Font.BOLD));
				}
			});
			this.add(speedMenuItem);
		}
	}

	private void resetFont() {
		for (int i = 0; i < this.getItemCount(); i++) {
			this.getItem(i).setFont(Main.MENU_FONT.deriveFont(Font.PLAIN));
		}
	}
}
