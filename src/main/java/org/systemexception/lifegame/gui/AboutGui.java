package org.systemexception.lifegame.gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AboutGui {

    private static final int WINDOW_WIDTH = 300;
    private static final int WINDOW_HEIGHT = 100;

    private AboutGui() {
    }

    /**
     * Launch the JavaFX About window from the Swing event thread.
     * Call this where you currently do: new AboutGui(); aboutGui.setVisible(true);
     *
     * @param parentX MainGui.windowPositionX (used to offset the window)
     * @param parentY MainGui.windowPositionY
     */
    public static void show(int parentX, int parentY) {
        Platform.runLater(() -> {
            Label titleLabel = new Label("LifeGame - Java Conway's Game of Life");
            titleLabel.setStyle(MainGui.FONT_BOLD);

            Label copyrightLabel = new Label("Copyright © 2014 - Leonardo Cappuccio");
            copyrightLabel.setStyle(MainGui.FONT_NORMAL);

            Label licenseLabel = new Label("Released under GNU GPL v3.0 License");
            licenseLabel.setStyle(MainGui.FONT_NORMAL);

            VBox layout = new VBox(3, titleLabel, copyrightLabel, licenseLabel);
            layout.setAlignment(Pos.TOP_CENTER);
            layout.setStyle("-fx-padding: 6;");

            Scene scene = new Scene(layout, WINDOW_WIDTH, WINDOW_HEIGHT);

            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setTitle("About LifeGame");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setX(parentX + 25d);
            stage.setY(parentY + 25d);
            stage.show();
        });
    }
}
