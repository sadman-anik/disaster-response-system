package com.sadman.drs.controller.ui;

import javafx.scene.control.Alert;

/**
 * Shared helper for consistent JavaFX alert dialogs.
 */
public class AlertHelper {

    public static void showInfo(String title, String message) {
        show(Alert.AlertType.INFORMATION, title, message);
    }

    public static void showWarning(String message) {
        show(Alert.AlertType.WARNING, "Validation Warning", message);
    }

    public static void showError(String title, String message) {
        show(Alert.AlertType.ERROR, title, message);
    }

    private static void show(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
