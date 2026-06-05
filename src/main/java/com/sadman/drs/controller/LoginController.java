package com.sadman.drs.controller;

import com.sadman.drs.client.bridge.DRSClientService;
import com.sadman.drs.model.User;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the login/register screen.
 */
public class LoginController {

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginStatusLabel;

    @FXML private TextField registerUsernameField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerConfirmPasswordField;
    @FXML private ComboBox<String> registerRoleComboBox;
    @FXML private Label registerStatusLabel;

    private final DRSClientService clientService = new DRSClientService();

    @FXML
    private void initialize() {
        registerRoleComboBox.getItems().addAll("VIEWER", "RESPONDER");
        registerRoleComboBox.getSelectionModel().selectFirst();
        loginStatusLabel.setText("Connecting to DRS server...");
        registerStatusLabel.setText("Create a viewer or responder account.");

        try {
            clientService.connect();
            if (clientService.pingServer()) {
                loginStatusLabel.setText("Connected. Login or register to continue.");
            } else {
                loginStatusLabel.setText("Unable to ping server.");
            }
        } catch (IOException | ClassNotFoundException exception) {
            loginStatusLabel.setText("Server connection failed: " + exception.getMessage());
            registerStatusLabel.setText("Server connection failed.");
        }
    }

    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Username and password are required.");
            return;
        }

        try {
            User user = clientService.authenticate(username, password);
            if (user == null) {
                loginStatusLabel.setText("Invalid username or password.");
                return;
            }
            openMainStage(user);
        } catch (IOException | ClassNotFoundException exception) {
            loginStatusLabel.setText("Login failed: " + exception.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        String username = registerUsernameField.getText().trim();
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();
        String role = registerRoleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            registerStatusLabel.setText("All registration fields are required.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            registerStatusLabel.setText("Passwords do not match.");
            return;
        }
        if (password.length() < 8) {
            registerStatusLabel.setText("Password must be at least 8 characters.");
            return;
        }
        try {
            User user = clientService.registerUser(username, password, role);
            if (user == null) {
                registerStatusLabel.setText("Username already exists. Choose another.");
                return;
            }
            registerStatusLabel.setText("Registration successful. You may now login.");
        } catch (IOException | ClassNotFoundException exception) {
            registerStatusLabel.setText("Registration failed: " + exception.getMessage());
        }
    }

    private void openMainStage(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            mainController.initializeWithClient(clientService, user);

            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            Scene scene = new Scene(root, 1450, 900);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("DRS - Disaster Response System");
            stage.setMinWidth(1300);
            stage.setMinHeight(800);
            stage.show();
        } catch (IOException exception) {
            loginStatusLabel.setText("Unable to load main application: " + exception.getMessage());
        }
    }
}
