package com.sadman.drs.controller.workflow;

import com.sadman.drs.client.bridge.DRSClientService;
import com.sadman.drs.controller.ui.AlertHelper;
import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.ResponseTask;
import com.sadman.drs.model.StatusValues;
import com.sadman.drs.protocol.AssessmentRequest;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Handles assessment form workflow for the main JavaFX view.
 */
public class AssessmentWorkflow {
    private final Supplier<DRSClientService> clientServiceSupplier;
    private final ComboBox<DisasterReport> assessmentReportComboBox;
    private final ComboBox<String> damageLevelComboBox;
    private final TextField peopleAffectedField;
    private final CheckBox infrastructureDamageCheckBox;
    private final TextArea assessmentOutputArea;
    private final Runnable refreshAllData;
    private final Runnable showAssessment;

    public AssessmentWorkflow(Supplier<DRSClientService> clientServiceSupplier,
                              ComboBox<DisasterReport> assessmentReportComboBox,
                              ComboBox<String> damageLevelComboBox,
                              TextField peopleAffectedField,
                              CheckBox infrastructureDamageCheckBox,
                              TextArea assessmentOutputArea,
                              Runnable refreshAllData,
                              Runnable showAssessment) {
        this.clientServiceSupplier = clientServiceSupplier;
        this.assessmentReportComboBox = assessmentReportComboBox;
        this.damageLevelComboBox = damageLevelComboBox;
        this.peopleAffectedField = peopleAffectedField;
        this.infrastructureDamageCheckBox = infrastructureDamageCheckBox;
        this.assessmentOutputArea = assessmentOutputArea;
        this.refreshAllData = refreshAllData;
        this.showAssessment = showAssessment;
    }

    public void generateAssessment() {
        DisasterReport selectedReport = assessmentReportComboBox.getValue();
        String damageLevel = getValue(damageLevelComboBox);

        if (selectedReport == null) {
            AlertHelper.showWarning("Select a disaster report to assess.");
            return;
        }
        if (isBlank(damageLevel)) {
            AlertHelper.showWarning("Select damage level.");
            return;
        }

        int peopleAffected;
        try {
            peopleAffected = Integer.parseInt(peopleAffectedField.getText().trim());
            if (peopleAffected < 0) {
                AlertHelper.showWarning("People affected cannot be negative.");
                return;
            }
        } catch (NumberFormatException exception) {
            AlertHelper.showWarning("People affected must be a valid number.");
            return;
        }

        try {
            AssessmentRequest request = new AssessmentRequest(selectedReport, damageLevel,
                    peopleAffected, infrastructureDamageCheckBox.isSelected());
            var assessmentResponse = clientServiceSupplier.get().saveAssessment(request);
            AssessmentResult result = assessmentResponse.getAssessmentResult();
            List<ResponseTask> generatedTasks = assessmentResponse.getGeneratedTasks();

            selectedReport.setPriorityLevel(result.getPriorityLevel());
            selectedReport.setStatus(StatusValues.ASSESSED);

            String generatedTaskText = generatedTasks.isEmpty()
                    ? "No new standard tasks were created because the required tasks already exist."
                    : generatedTasks.stream()
                    .map(task -> "- " + task.getActivityType() + " -> " + task.getDepartmentName())
                    .collect(Collectors.joining("\n"));

            assessmentOutputArea.setText(result.getAssessmentSummary()
                    + "\n\nAuto-generated response tasks after assessment:\n"
                    + generatedTaskText);

            String taskDialogMessage = generatedTasks.isEmpty()
                    ? "Standard response tasks already exist for this report, so duplicate tasks were not created."
                    : generatedTasks.size() + " standard response task(s) were auto-generated for this report.";

            AlertHelper.showInfo("Assessment Saved & Tasks Prepared",
                    taskDialogMessage
                            + "\n\nYou can add extra/manual tasks from the Add Extra Task tab."
                            + "\nYou can update task progress from the Update Task Status tab.");

            refreshAllData.run();
            showAssessment.run();
        } catch (IOException | ClassNotFoundException exception) {
            AlertHelper.showError("Assessment Error", exception.getMessage());
        }
    }

    private String getValue(ComboBox<String> comboBox) {
        return comboBox.getValue() == null ? "" : comboBox.getValue();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
