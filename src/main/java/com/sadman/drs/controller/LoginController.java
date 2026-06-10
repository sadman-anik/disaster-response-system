package com.sadman.drs.controller;

import com.sadman.drs.client.bridge.DRSClientService;
import com.sadman.drs.controller.ui.AlertHelper;
import com.sadman.drs.controller.validation.FormValueHelper;
import com.sadman.drs.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the login/register screen.
 */
public class LoginController {

    private static final int SERVER_RETRY_DELAY_MILLIS = 2000;
    private static final String SUCCESS_STYLE = "success-label";
    private static final String ERROR_STYLE = "error-label";
    private static final String SERVER_CONNECTED_STYLE = "server-connected";
    private static final String SERVER_DISCONNECTED_STYLE = "server-disconnected";

    @FXML private VBox loginPane;
    @FXML private VBox registerPane;
    @FXML private Label serverStatusLabel;

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginStatusLabel;
    @FXML private Button loginButton;

    @FXML private TextField registerUsernameField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerConfirmPasswordField;
    @FXML private Label passwordLengthLabel;
    @FXML private Label passwordUppercaseLabel;
    @FXML private Label passwordLowercaseLabel;
    @FXML private Label passwordSpecialLabel;
    @FXML private Label passwordMatchLabel;
    @FXML private ComboBox<String> registerRoleComboBox;
    @FXML private Label registerStatusLabel;
    @FXML private Button registerButton;

    private final DRSClientService clientService = new DRSClientService();
    private Task<Void> connectTask;
    private boolean serverConnected = false;

    @FXML
    private void initialize() {
        registerRoleComboBox.getItems().addAll("REPORTER", "ASSESSMENT_OFFICER", "RESOURCE_OFFICER", "DEPARTMENT_OFFICER", "AUDITOR");
        registerRoleComboBox.getSelectionModel().selectFirst();
        setValidationMessage(loginStatusLabel, "", false);
        setValidationMessage(registerStatusLabel, "", false);
        updateServerStatus("Connecting to DRS server", false);
        registerPasswordField.textProperty().addListener((observable, oldValue, newValue) -> updateRegistrationValidation());
        registerConfirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> updateRegistrationValidation());
        updateRegistrationValidation();
        updateAuthControls();
        connectToServerInBackground();
    }

    private void connectToServerInBackground() {
        connectTask = new Task<>() {
            @Override
            protected Void call() throws InterruptedException {
                while (!isCancelled()) {
                    try {
                        updateMessage("✕ Connecting to DRS server...");
                        clientService.connect();
                        if (clientService.pingServer()) {
                            updateMessage("✓ Connected to DRS server");
                            return null;
                        }
                        clientService.close();
                        updateMessage("✕ Waiting for DRS server");
                    } catch (IOException | ClassNotFoundException | RuntimeException exception) {
                        clientService.close();
                        updateMessage("✕ Waiting for DRS server: " + exception.getMessage());
                    }
                    Thread.sleep(SERVER_RETRY_DELAY_MILLIS);
                }
                return null;
            }
        };

        serverStatusLabel.textProperty().bind(connectTask.messageProperty());
        connectTask.setOnSucceeded(event -> {
            serverStatusLabel.textProperty().unbind();
            if (clientService.isConnected()) {
                serverConnected = true;
                updateServerStatus("Connected to DRS server", true);
            } else {
                serverConnected = false;
                updateServerStatus("Server connection stopped", false);
            }
            updateAuthControls();
        });
        connectTask.setOnFailed(event -> {
            Throwable exception = connectTask.getException();
            serverStatusLabel.textProperty().unbind();
            clientService.close();
            serverConnected = false;
            updateServerStatus("Server connection failed: " + exception.getMessage(), false);
            updateAuthControls();
        });

        Thread connectionThread = new Thread(connectTask, "drs-login-server-connect");
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    @FXML
    private void showRegisterForm() {
        setVisibleAuthPane(registerPane);
        setValidationMessage(loginStatusLabel, "", false);
        registerUsernameField.requestFocus();
    }

    @FXML
    private void showLoginForm() {
        setVisibleAuthPane(loginPane);
        setValidationMessage(registerStatusLabel, "", false);
        loginUsernameField.requestFocus();
    }

    private void setVisibleAuthPane(VBox activePane) {
        boolean loginVisible = activePane == loginPane;
        loginPane.setVisible(loginVisible);
        loginPane.setManaged(loginVisible);
        registerPane.setVisible(!loginVisible);
        registerPane.setManaged(!loginVisible);
    }

    private void updateServerStatus(String message, boolean connected) {
        serverStatusLabel.setText((connected ? "✓ " : "✕ ") + message);
        serverStatusLabel.getStyleClass().removeAll(SERVER_CONNECTED_STYLE, SERVER_DISCONNECTED_STYLE);
        serverStatusLabel.getStyleClass().add(connected ? SERVER_CONNECTED_STYLE : SERVER_DISCONNECTED_STYLE);
    }

    private void updateAuthControls() {
        loginButton.setDisable(!serverConnected);
        registerButton.setDisable(!serverConnected || !isRegistrationPasswordValid());
    }

    private void updateRegistrationValidation() {
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();

        updateValidationLabel(passwordLengthLabel, password.length() >= 8);
        updateValidationLabel(passwordUppercaseLabel, password.chars().anyMatch(Character::isUpperCase));
        updateValidationLabel(passwordLowercaseLabel, password.chars().anyMatch(Character::isLowerCase));
        updateValidationLabel(passwordSpecialLabel, containsSpecialCharacter(password));
        updateValidationLabel(passwordMatchLabel, !password.isEmpty() && password.equals(confirmPassword));
        updateAuthControls();
    }

    private boolean isRegistrationPasswordValid() {
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();
        return password.length() >= 8
                && password.chars().anyMatch(Character::isUpperCase)
                && password.chars().anyMatch(Character::isLowerCase)
                && containsSpecialCharacter(password)
                && password.equals(confirmPassword);
    }

    private boolean containsSpecialCharacter(String value) {
        return value.chars().anyMatch(character -> !Character.isLetterOrDigit(character));
    }

    private void updateValidationLabel(Label label, boolean valid) {
        label.getStyleClass().removeAll(SUCCESS_STYLE, ERROR_STYLE);
        label.getStyleClass().add(valid ? SUCCESS_STYLE : ERROR_STYLE);
    }

    private void setValidationMessage(Label label, String message, boolean error) {
        label.setText(message);
        label.getStyleClass().removeAll(SUCCESS_STYLE, ERROR_STYLE);
        if (!message.isEmpty()) {
            label.getStyleClass().add(error ? ERROR_STYLE : SUCCESS_STYLE);
        }
    }

    @FXML
    private void handleLogin() {
        if (!clientService.isConnected()) {
            setValidationMessage(loginStatusLabel, "Server is not connected yet.", true);
            return;
        }

        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText();

        if (FormValueHelper.isBlank(username) || FormValueHelper.isBlank(password)) {
            setValidationMessage(loginStatusLabel, "Username and password are required.", true);
            return;
        }

        try {
            User user = clientService.authenticate(username, password);
            if (user == null) {
                setValidationMessage(loginStatusLabel, "Invalid username or password.", true);
                return;
            }
            openMainStage(user);
        } catch (IOException | ClassNotFoundException | IllegalStateException exception) {
            setValidationMessage(loginStatusLabel, "Login failed: " + exception.getMessage(), true);
        }
    }

    @FXML
    private void handleRegister() {
        if (!clientService.isConnected()) {
            setValidationMessage(registerStatusLabel, "Server is not connected yet.", true);
            return;
        }

        String username = registerUsernameField.getText().trim();
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();
        String role = FormValueHelper.getValue(registerRoleComboBox);

        if (FormValueHelper.isBlank(username) || FormValueHelper.isBlank(password)
                || FormValueHelper.isBlank(confirmPassword)) {
            setValidationMessage(registerStatusLabel, "All registration fields are required.", true);
            return;
        }
        if (!password.equals(confirmPassword)) {
            setValidationMessage(registerStatusLabel, "Passwords do not match.", true);
            return;
        }
        if (!isRegistrationPasswordValid()) {
            setValidationMessage(registerStatusLabel, "Password must meet all requirements and match confirmation.", true);
            return;
        }
        try {
            User user = clientService.registerUser(username, password, role);
            if (user == null) {
                setValidationMessage(registerStatusLabel, "Username already exists. Choose another.", true);
                return;
            }
            showRegistrationSuccess(username);
        } catch (IOException | ClassNotFoundException | IllegalStateException exception) {
            setValidationMessage(registerStatusLabel, "Registration failed: " + exception.getMessage(), true);
        }
    }

    private void showRegistrationSuccess(String username) {
        AlertHelper.showInfo("Registration Successful", "Your account has been created. You can now login.");
        loginUsernameField.setText(username);
        loginPasswordField.clear();
        registerUsernameField.clear();
        registerPasswordField.clear();
        registerConfirmPasswordField.clear();
        registerRoleComboBox.getSelectionModel().selectFirst();
        setValidationMessage(registerStatusLabel, "", false);
        updateRegistrationValidation();
        showLoginForm();
        setValidationMessage(loginStatusLabel, "Registration successful. Please enter your password.", false);
        loginPasswordField.requestFocus();
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
            setValidationMessage(loginStatusLabel, "Unable to load main application: " + exception.getMessage(), true);
        }
    }
}
