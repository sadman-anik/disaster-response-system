package com.sadman.drs.controller.workflow;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Tracks duplicate-check state for the disaster report form.
 */
public class DuplicateCheckWorkflow {
    private Button submitReportButton;
    private Label duplicateWarningLabel;
    private boolean checkedAndPassed = false;
    private String lastCheckedDisasterType = "";
    private String lastCheckedLocation = "";

    public void initialize(Button submitReportButton,
                           Label duplicateWarningLabel,
                           ComboBox<String> disasterTypeComboBox,
                           TextField locationField) {
        this.submitReportButton = submitReportButton;
        this.duplicateWarningLabel = duplicateWarningLabel;
        setSubmitEnabled(false);
        showStatus("Please check duplicate before submitting.", "info-label");
        disasterTypeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> resetAfterInputChange());
        locationField.textProperty().addListener((obs, oldValue, newValue) -> resetAfterInputChange());
    }

    public boolean isCheckedAndPassed() {
        return checkedAndPassed;
    }

    public boolean matchesLastCheck(String disasterType, String location) {
        return disasterType.trim().equalsIgnoreCase(lastCheckedDisasterType)
                && location.trim().equalsIgnoreCase(lastCheckedLocation);
    }

    public void markPassed(String disasterType, String location) {
        checkedAndPassed = true;
        lastCheckedDisasterType = disasterType.trim();
        lastCheckedLocation = location.trim();
        setSubmitEnabled(true);
        showStatus("No duplicate found. You can now submit the report.", "success-label");
    }

    public void blockSubmission(String message) {
        checkedAndPassed = false;
        setSubmitEnabled(false);
        showStatus(message, "error-label");
    }

    public void resetAfterSubmit() {
        checkedAndPassed = false;
        lastCheckedDisasterType = "";
        lastCheckedLocation = "";
        setSubmitEnabled(false);
        showStatus("Report saved successfully. Please check duplicate before submitting another report.", "success-label");
    }

    private void resetAfterInputChange() {
        checkedAndPassed = false;
        lastCheckedDisasterType = "";
        lastCheckedLocation = "";
        setSubmitEnabled(false);
        showStatus("Disaster type or location changed. Please check duplicate again.", "info-label");
    }

    private void setSubmitEnabled(boolean enabled) {
        if (submitReportButton != null) {
            submitReportButton.setDisable(!enabled);
        }
    }

    private void showStatus(String message, String styleClass) {
        if (duplicateWarningLabel == null) {
            return;
        }
        duplicateWarningLabel.setText(message);
        duplicateWarningLabel.getStyleClass().removeAll(
                "warning-text", "info-label", "success-label", "error-label"
        );
        duplicateWarningLabel.getStyleClass().add(styleClass);
    }
}
