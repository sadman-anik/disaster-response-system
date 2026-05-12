package com.sadman.drs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main JavaFX application class for DRS.
 */
public class MainApp extends Application {

@Override
public void start(Stage stage) throws IOException {
    FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/sadman/drs/main.fxml"));

    Scene scene = new Scene(loader.load(), 1450, 900);

    scene.getStylesheets().add(MainApp.class.getResource("/com/sadman/drs/style.css").toExternalForm());

    stage.setTitle("DRS - Disaster Response System");
    stage.setScene(scene);

    // Minimum size to stop the UI/table from becoming too compressed
    stage.setMinWidth(1300);
    stage.setMinHeight(800);

    // Optional: opens the app maximized
    // stage.setMaximized(true);

    stage.show();
}

    public static void main(String[] args) {
        launch(args);
    }
}
