package com.sadman.drs.controller.workflow;

import com.sadman.drs.client.bridge.DRSClientService;
import com.sadman.drs.controller.ui.AlertHelper;
import com.sadman.drs.controller.ui.ViewFormatter;
import com.sadman.drs.controller.validation.FormValueHelper;
import com.sadman.drs.controller.validation.ReportValidationService;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.ResponseTask;
import com.sadman.drs.model.StatusValues;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * Handles report submission, duplicate checking, searching, and report status
 * workflow.
 */
public class ReportWorkflow {

    private final Supplier<DRSClientService> clientServiceSupplier;
    private final ReportValidationService reportValidationService;
    private final DuplicateCheckWorkflow duplicateCheckWorkflow;
    private final TextField reportTitleField;
    private final ComboBox<String> disasterTypeComboBox;
    private final ComboBox<String> severityComboBox;
    private final TextField locationField;
    private final TextField reportedByField;
    private final TextField contactNumberField;
    private final TextArea descriptionArea;
    private final TextArea reportResultArea;
    private final TextField searchField;
    private final ComboBox<String> severityFilterComboBox;
    private final ComboBox<String> statusFilterComboBox;
    private final TableView<DisasterReport> reportTable;
    private final ComboBox<String> reportStatusComboBox;
    private final TextArea reportDetailsArea;
    private final ComboBox<ResponseTask> reportTaskComboBox;
    private final Runnable refreshAllData;

    public ReportWorkflow(Supplier<DRSClientService> clientServiceSupplier,
            ReportValidationService reportValidationService,
            DuplicateCheckWorkflow duplicateCheckWorkflow,
            TextField reportTitleField,
            ComboBox<String> disasterTypeComboBox,
            ComboBox<String> severityComboBox,
            TextField locationField,
            TextField reportedByField,
            TextField contactNumberField,
            TextArea descriptionArea,
            TextArea reportResultArea,
            TextField searchField,
            ComboBox<String> severityFilterComboBox,
            ComboBox<String> statusFilterComboBox,
            TableView<DisasterReport> reportTable,
            ComboBox<String> reportStatusComboBox,
            TextArea reportDetailsArea,
            ComboBox<ResponseTask> reportTaskComboBox,
            Runnable refreshAllData) {
        this.clientServiceSupplier = clientServiceSupplier;
        this.reportValidationService = reportValidationService;
        this.duplicateCheckWorkflow = duplicateCheckWorkflow;
        this.reportTitleField = reportTitleField;
        this.disasterTypeComboBox = disasterTypeComboBox;
        this.severityComboBox = severityComboBox;
        this.locationField = locationField;
        this.reportedByField = reportedByField;
        this.contactNumberField = contactNumberField;
        this.descriptionArea = descriptionArea;
        this.reportResultArea = reportResultArea;
        this.searchField = searchField;
        this.severityFilterComboBox = severityFilterComboBox;
        this.statusFilterComboBox = statusFilterComboBox;
        this.reportTable = reportTable;
        this.reportStatusComboBox = reportStatusComboBox;
        this.reportDetailsArea = reportDetailsArea;
        this.reportTaskComboBox = reportTaskComboBox;
        this.refreshAllData = refreshAllData;
    }

    public void submitDisasterReport() {
        String reportTitle = reportTitleField.getText();
        String disasterType = FormValueHelper.getValue(disasterTypeComboBox);
        String severity = FormValueHelper.getValue(severityComboBox);
        String location = locationField.getText();
        String description = descriptionArea.getText();
        String reportedBy = reportedByField.getText();
        String contactNumber = contactNumberField.getText();

        String validationError = reportValidationService.validateReport(reportTitle, disasterType, severity, location,
                description, reportedBy, contactNumber);
        if (validationError != null) {
            AlertHelper.showWarning(validationError);
            return;
        }

        if (!duplicateCheckWorkflow.isCheckedAndPassed()) {
            duplicateCheckWorkflow.blockSubmission("Duplicate check required before submitting.");
            AlertHelper.showWarning("Please check duplicate before submitting the disaster report.");
            return;
        }
        if (!duplicateCheckWorkflow.matchesLastCheck(disasterType, location)) {
            duplicateCheckWorkflow.blockSubmission("Disaster type or location changed after duplicate check. Please check again.");
            AlertHelper.showWarning("Disaster type or location changed after duplicate check. Please check duplicate again.");
            return;
        }

        try {
            boolean duplicate = clientServiceSupplier.get().checkDuplicate(disasterType, location);
            if (duplicate) {
                duplicateCheckWorkflow.blockSubmission("Duplicate found. Report was not submitted.");
                reportResultArea.setText("Duplicate report detected. Please check the Reports page before submitting again.");
                return;
            }

            DisasterReport report = new DisasterReport(reportTitle.trim(), disasterType, severity, location.trim(),
                    description.trim(), reportedBy.trim(), contactNumber.trim(), StatusValues.REPORTED,
                    null, null, null);
            DisasterReport savedReport = clientServiceSupplier.get().submitReport(report);
            reportResultArea.setText(ViewFormatter.buildReportResult(savedReport));
            clearReportForm();
            duplicateCheckWorkflow.resetAfterSubmit();
            refreshAllData.run();
        } catch (IOException | ClassNotFoundException exception) {
            AlertHelper.showError("Submit Error", exception.getMessage());
        }
    }

    public void checkDuplicateReport() {
        String disasterType = FormValueHelper.getValue(disasterTypeComboBox);
        String location = locationField.getText();

        if (FormValueHelper.isBlank(disasterType)) {
            duplicateCheckWorkflow.blockSubmission("Please select disaster type first.");
            return;
        }
        if (FormValueHelper.isBlank(location)) {
            duplicateCheckWorkflow.blockSubmission("Please enter location first.");
            return;
        }

        try {
            if (clientServiceSupplier.get().checkDuplicate(disasterType, location)) {
                duplicateCheckWorkflow.blockSubmission("Duplicate found. A similar active disaster report already exists.");
            } else {
                duplicateCheckWorkflow.markPassed(disasterType, location);
            }
        } catch (IOException | ClassNotFoundException exception) {
            duplicateCheckWorkflow.blockSubmission("Duplicate check failed. Please try again.");
            AlertHelper.showError("Duplicate Check Error", exception.getMessage());
        }
    }

    public void searchReports() {
        String keyword = searchField.getText();
        String selectedSeverity = FormValueHelper.getValue(severityFilterComboBox);
        String selectedStatus = FormValueHelper.getValue(statusFilterComboBox);

        try {
            List<DisasterReport> reports = FormValueHelper.isBlank(keyword)
                    ? clientServiceSupplier.get().findAllReports()
                    : clientServiceSupplier.get().searchReports(keyword.trim());

            reports = applyReportFilters(reports, selectedSeverity, selectedStatus);

            reportTable.setItems(FXCollections.observableArrayList(reports));
        } catch (IOException | ClassNotFoundException exception) {
            AlertHelper.showError("Search Error", exception.getMessage());
        }
    }

    static List<DisasterReport> applyReportFilters(List<DisasterReport> reports,
                                                   String selectedSeverity,
                                                   String selectedStatus) {
        if (!FormValueHelper.isBlank(selectedSeverity)
                && !"All Severities".equalsIgnoreCase(selectedSeverity)) {
            reports = reports.stream()
                    .filter(report -> selectedSeverity.equalsIgnoreCase(report.getSeverity()))
                    .toList();
        }

        if (!FormValueHelper.isBlank(selectedStatus)
                && !"All Report Statuses".equalsIgnoreCase(selectedStatus)) {
            reports = reports.stream()
                    .filter(report -> selectedStatus.equalsIgnoreCase(report.getStatus()))
                    .toList();
        }

        return reports;
    }

    public void updateSelectedReportStatus() {
        DisasterReport selectedReport = reportTable.getSelectionModel().getSelectedItem();
        String status = FormValueHelper.getValue(reportStatusComboBox);
        if (selectedReport == null) {
            AlertHelper.showWarning("Select a report from the Reports table first.");
            return;
        }
        if (FormValueHelper.isBlank(status)) {
            AlertHelper.showWarning("Select the new report status.");
            return;
        }
        if (StatusValues.isTerminalReportStatus(selectedReport.getStatus())) {
            AlertHelper.showWarning("Completed reports cannot be updated.");
            return;
        }
        updateReportStatus(selectedReport, status);
    }

    public void markSelectedReportAssessed() {
        updateSelectedReport(StatusValues.ASSESSED);
    }

    public void completeSelectedReport() {
        updateSelectedReport(StatusValues.COMPLETED);
    }

    public void showSelectedReportDetails(DisasterReport report) {
        if (report == null) {
            reportDetailsArea.clear();
            if (reportTaskComboBox != null) {
                reportTaskComboBox.getSelectionModel().clearSelection();
                reportTaskComboBox.setItems(FXCollections.observableArrayList());
            }
            return;
        }

        try {
            List<ResponseTask> tasks = clientServiceSupplier.get().findTasksByReportId(report.getReportId());
            reportDetailsArea.setText(ViewFormatter.buildReportDetails(report, tasks));
        } catch (IOException | ClassNotFoundException exception) {
            reportDetailsArea.setText(ViewFormatter.buildReportDetails(report, List.of()));
        }
    }

    public void loadTasksForSelectedReport(DisasterReport report) {
        if (reportTaskComboBox == null) {
            return;
        }
        try {
            if (report == null) {
                reportTaskComboBox.setItems(FXCollections.observableArrayList());
                reportTaskComboBox.getSelectionModel().clearSelection();
                return;
            }
            List<ResponseTask> tasks = clientServiceSupplier.get().findTasksByReportId(report.getReportId());
            reportTaskComboBox.setItems(FXCollections.observableArrayList(tasks));
            reportTaskComboBox.getSelectionModel().clearSelection();
        } catch (IOException | ClassNotFoundException exception) {
            AlertHelper.showError("Task Load Error", exception.getMessage());
        }
    }

    private void updateSelectedReport(String status) {
        DisasterReport selectedReport = reportTable.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            AlertHelper.showWarning("Select a report from the Reports table first.");
            return;
        }
        updateReportStatus(selectedReport, status);
    }

    private void updateReportStatus(DisasterReport report, String status) {
        try {
            clientServiceSupplier.get().updateReportStatus(report.getReportId(), status);
            report.setStatus(status);
            reportStatusComboBox.setValue(status);
            List<ResponseTask> tasks = clientServiceSupplier.get().findTasksByReportId(report.getReportId());
            reportDetailsArea.setText(report.getReportDisplayName() + " status updated to " + status
                    + ".\n\n" + ViewFormatter.buildReportDetails(report, tasks));
            refreshAllData.run();
        } catch (IOException | ClassNotFoundException | IllegalStateException exception) {
            AlertHelper.showError("Report Status Update Error", exception.getMessage());
        }
    }

    private void clearReportForm() {
        reportTitleField.clear();
        disasterTypeComboBox.getSelectionModel().clearSelection();
        severityComboBox.getSelectionModel().clearSelection();
        locationField.clear();
        reportedByField.clear();
        contactNumberField.clear();
        descriptionArea.clear();
    }

}
