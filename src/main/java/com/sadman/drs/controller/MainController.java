package com.sadman.drs.controller;

import com.sadman.drs.client.bridge.DRSClientService;
import com.sadman.drs.controller.validation.ReportValidationService;
import com.sadman.drs.controller.ui.ComboBoxInitializer;
import com.sadman.drs.controller.ui.TableSetupHelper;
import com.sadman.drs.controller.ui.UiDataRefresher;
import com.sadman.drs.controller.ui.ViewFormatter;
import com.sadman.drs.model.*;
import com.sadman.drs.protocol.AssessmentRequest;
import com.sadman.drs.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class that handles all JavaFX UI events.
 * The business logic is delegated to service classes and data persistence is delegated to repositories.
 */
public class MainController {

    @FXML private Label pageTitleLabel;
    @FXML private Label databaseStatusLabel;
    @FXML private Label userStatusLabel;

    @FXML private VBox dashboardPane;
    @FXML private VBox reportPane;
    @FXML private VBox assessmentPane;
    @FXML private VBox coordinationPane;
    @FXML private VBox departmentsPane;
    @FXML private VBox resourcesPane;
    @FXML private VBox reportsPane;
    @FXML private VBox auditPane;

    @FXML private Label totalReportsLabel;
    @FXML private Label criticalReportsLabel;
    @FXML private Label openTasksLabel;
    @FXML private Label availableResourcesLabel;
    @FXML private PieChart reportStatusChart;
    @FXML private BarChart<String, Number> taskDepartmentChart;
    @FXML private BarChart<String, Number> resourceAvailabilityChart;

    @FXML private TextField reportTitleField;
    @FXML private ComboBox<String> disasterTypeComboBox;
    @FXML private ComboBox<String> severityComboBox;
    @FXML private Button dashboardButton;
    @FXML private Button reportButton;
    @FXML private Button assessmentButton;
    @FXML private Button coordinationButton;
    @FXML private Button departmentsButton;
    @FXML private Button resourcesButton;
    @FXML private Button reportsButton;
    @FXML private Button auditButton;
    @FXML private TextField locationField;
    @FXML private TextField reportedByField;
    @FXML private TextField contactNumberField;
    @FXML private TextArea descriptionArea;
    @FXML private Label duplicateWarningLabel;
    @FXML private Button submitReportButton;
    @FXML private TextArea reportResultArea;

    private boolean duplicateCheckedAndPassed = false;
    private String lastCheckedDisasterType = "";
    private String lastCheckedLocation = "";
    private User currentUser;

    @FXML private ComboBox<DisasterReport> assessmentReportComboBox;
    @FXML private ComboBox<String> damageLevelComboBox;
    @FXML private TextField peopleAffectedField;
    @FXML private CheckBox infrastructureDamageCheckBox;
    @FXML private TextArea assessmentOutputArea;
    @FXML private TableView<AssessmentResult> assessmentTable;
    @FXML private TableColumn<AssessmentResult, Integer> assessmentIdColumn;
    @FXML private TableColumn<AssessmentResult, String> assessmentReportIdColumn;
    @FXML private TableColumn<AssessmentResult, String> assessmentDamageColumn;
    @FXML private TableColumn<AssessmentResult, Integer> assessmentPeopleColumn;
    @FXML private TableColumn<AssessmentResult, Integer> assessmentScoreColumn;
    @FXML private TableColumn<AssessmentResult, String> assessmentPriorityColumn;

    @FXML private ComboBox<DisasterReport> taskReportComboBox;
    @FXML private ComboBox<Department> taskDepartmentComboBox;
    @FXML private ComboBox<String> activityTypeComboBox;
    @FXML private ComboBox<String> taskPriorityComboBox;
    @FXML private TextArea taskDescriptionArea;
    @FXML private TextArea coordinationOutputArea;
    @FXML private TableView<ResponseTask> taskTable;
    @FXML private TableColumn<ResponseTask, Integer> taskIdColumn;
    @FXML private TableColumn<ResponseTask, String> taskReportIdColumn;
    @FXML private TableColumn<ResponseTask, String> taskDepartmentColumn;
    @FXML private TableColumn<ResponseTask, String> taskActivityColumn;
    @FXML private TableColumn<ResponseTask, String> taskPriorityColumn;
    @FXML private TableColumn<ResponseTask, String> taskStatusColumn;

    @FXML private TableView<Department> departmentTable;
    @FXML private TableColumn<Department, Integer> departmentIdColumn;
    @FXML private TableColumn<Department, String> departmentNameColumn;
    @FXML private TableColumn<Department, String> departmentServiceColumn;
    @FXML private TableColumn<Department, String> departmentContactColumn;
    @FXML private TableColumn<Department, String> departmentStatusColumn;
    @FXML private TableView<ResponseTask> departmentTaskTable;
    @FXML private TableColumn<ResponseTask, Integer> departmentTaskIdColumn;
    @FXML private TableColumn<ResponseTask, String> departmentTaskReportColumn;
    @FXML private TableColumn<ResponseTask, String> departmentTaskDepartmentColumn;
    @FXML private TableColumn<ResponseTask, String> departmentTaskActivityColumn;
    @FXML private TableColumn<ResponseTask, String> departmentTaskPriorityColumn;
    @FXML private TableColumn<ResponseTask, String> departmentTaskStatusColumn;
    @FXML private ComboBox<String> departmentTaskStatusComboBox;

    @FXML private ComboBox<DisasterReport> resourceReportComboBox;
    @FXML private ComboBox<Resource> resourceComboBox;
    @FXML private TextField quantityField;
    @FXML private TextArea resourceOutputArea;
    @FXML private TableView<Resource> resourceTable;
    @FXML private TableColumn<Resource, Integer> resourceIdColumn;
    @FXML private TableColumn<Resource, String> resourceNameColumn;
    @FXML private TableColumn<Resource, String> resourceCategoryColumn;
    @FXML private TableColumn<Resource, Integer> resourceQuantityColumn;
    @FXML private TableView<ResourceAllocation> allocationTable;
    @FXML private TableColumn<ResourceAllocation, Integer> allocationIdColumn;
    @FXML private TableColumn<ResourceAllocation, Integer> allocationReportIdColumn;
    @FXML private TableColumn<ResourceAllocation, String> allocationResourceColumn;
    @FXML private TableColumn<ResourceAllocation, Integer> allocationQuantityColumn;

    @FXML private TextField searchField;
    @FXML private TableView<DisasterReport> reportTable;
    @FXML private TableColumn<DisasterReport, String> reportDisplayColumn;
    @FXML private TableColumn<DisasterReport, String> reportTypeColumn;
    @FXML private TableColumn<DisasterReport, String> reportSeverityColumn;
    @FXML private TableColumn<DisasterReport, String> reportLocationColumn;
    @FXML private TableColumn<DisasterReport, String> reportPriorityColumn;
    @FXML private TableColumn<DisasterReport, String> reportStatusColumn;
    @FXML private TextArea reportDetailsArea;
    @FXML private VBox reportStatusUpdatePane;
    @FXML private ComboBox<String> reportStatusComboBox;
    @FXML private ComboBox<ResponseTask> reportTaskComboBox;
    @FXML private ComboBox<String> reportTaskStatusComboBox;

    @FXML private TextField auditSearchField;
    @FXML private TableView<AuditRecord> auditTable;
    @FXML private TableColumn<AuditRecord, Integer> auditIdColumn;
    @FXML private TableColumn<AuditRecord, String> auditWhenColumn;
    @FXML private TableColumn<AuditRecord, String> auditUserColumn;
    @FXML private TableColumn<AuditRecord, String> auditActionColumn;
    @FXML private TableColumn<AuditRecord, String> auditEntityTypeColumn;
    @FXML private TableColumn<AuditRecord, String> auditEntityLabelColumn;
    @FXML private TableColumn<AuditRecord, String> auditDetailsColumn;
    @FXML private BarChart<String, Number> auditActionChart;

    private DRSClientService clientService;
    private final ReportValidationService reportValidationService = new ReportValidationService();

    @FXML
    private void initialize() {
        ComboBoxInitializer.initializeComboBoxes(
                disasterTypeComboBox,
                severityComboBox,
                damageLevelComboBox,
                activityTypeComboBox,
                taskPriorityComboBox,
                reportStatusComboBox,
                reportTaskStatusComboBox,
                departmentTaskStatusComboBox);

        TableSetupHelper.initializeTables(
                assessmentIdColumn,
                assessmentReportIdColumn,
                assessmentDamageColumn,
                assessmentPeopleColumn,
                assessmentScoreColumn,
                assessmentPriorityColumn,
                taskIdColumn,
                taskReportIdColumn,
                taskDepartmentColumn,
                taskActivityColumn,
                taskPriorityColumn,
                taskStatusColumn,
                departmentIdColumn,
                departmentNameColumn,
                departmentServiceColumn,
                departmentContactColumn,
                departmentStatusColumn,
                departmentTaskTable,
                departmentTaskStatusComboBox,
                departmentTaskIdColumn,
                departmentTaskReportColumn,
                departmentTaskDepartmentColumn,
                departmentTaskActivityColumn,
                departmentTaskPriorityColumn,
                departmentTaskStatusColumn,
                resourceIdColumn,
                resourceNameColumn,
                resourceCategoryColumn,
                resourceQuantityColumn,
                allocationIdColumn,
                allocationReportIdColumn,
                allocationResourceColumn,
                allocationQuantityColumn,
                reportDisplayColumn,
                reportTypeColumn,
                reportSeverityColumn,
                reportLocationColumn,
                reportPriorityColumn,
                reportStatusColumn,
                reportTable,
                reportStatusComboBox,
                this::showSelectedReportDetails);

        if (auditTable != null) {
            auditIdColumn.setCellValueFactory(new PropertyValueFactory<>("auditId"));
            auditWhenColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            auditUserColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            auditActionColumn.setCellValueFactory(new PropertyValueFactory<>("actionType"));
            auditEntityTypeColumn.setCellValueFactory(new PropertyValueFactory<>("entityType"));
            auditEntityLabelColumn.setCellValueFactory(new PropertyValueFactory<>("entityLabel"));
            auditDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("changeDetails"));
        }

        initializeDuplicateCheckWorkflow();
    }

    public void initializeWithClient(DRSClientService clientService, User currentUser) {
        this.clientService = clientService;
        this.currentUser = currentUser;
        userStatusLabel.setText("Logged in: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        databaseStatusLabel.setText("DRS server available");
        applyRolePermissions();
        refreshAllData();
        showDashboard();
    }

    private void initializeDuplicateCheckWorkflow() {
        if (submitReportButton != null) {
            submitReportButton.setDisable(true);
        }

        if (duplicateWarningLabel != null) {
            showDuplicateStatus("Please check duplicate before submitting.", "info-label");
        }

        disasterTypeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> resetDuplicateCheck());
        locationField.textProperty().addListener((obs, oldValue, newValue) -> resetDuplicateCheck());
    }

    private void resetDuplicateCheck() {
        duplicateCheckedAndPassed = false;
        lastCheckedDisasterType = "";
        lastCheckedLocation = "";

        if (submitReportButton != null) {
            submitReportButton.setDisable(true);
        }

        if (duplicateWarningLabel != null) {
            showDuplicateStatus("Disaster type or location changed. Please check duplicate again.", "info-label");
        }
    }

    private void showDuplicateStatus(String message, String styleClass) {
        duplicateWarningLabel.setText(message);
        duplicateWarningLabel.getStyleClass().removeAll(
                "warning-text", "info-label", "success-label", "error-label"
        );
        duplicateWarningLabel.getStyleClass().add(styleClass);
    }

    @FXML
    private void showDashboard() {
        setVisiblePane(dashboardPane, "Dashboard");
        UiDataRefresher.refreshDashboard(clientService,
                totalReportsLabel,
                criticalReportsLabel,
                openTasksLabel,
                availableResourcesLabel,
                reportStatusChart,
                taskDepartmentChart,
                resourceAvailabilityChart,
                databaseStatusLabel);
    }

    @FXML
    private void showReportDisaster() {
        setVisiblePane(reportPane, "Report Disaster");
    }

    @FXML
    private void showAssessment() {
        setVisiblePane(assessmentPane, "Assess Report & Auto-Assign Tasks");
        UiDataRefresher.refreshAssessmentData(clientService,
                assessmentTable,
                assessmentReportComboBox);
    }

    @FXML
    private void showCoordination() {
        setVisiblePane(coordinationPane, "Add Extra Response Task");
        UiDataRefresher.refreshCoordinationData(clientService,
                taskReportComboBox,
                taskDepartmentComboBox,
                taskTable);
    }

    @FXML
    private void showDepartments() {
        setVisiblePane(departmentsPane, "Update Department Task Status");
        UiDataRefresher.refreshDepartmentData(clientService,
                departmentTable,
                departmentTaskTable);
    }

    @FXML
    private void showResources() {
        setVisiblePane(resourcesPane, "Manage Emergency Resources");
        UiDataRefresher.refreshResourceData(clientService,
                resourceReportComboBox,
                resourceComboBox,
                resourceTable,
                allocationTable);
    }

    @FXML
    private void showReports() {
        setVisiblePane(reportsPane, "Report Status & Search");
        UiDataRefresher.refreshReportData(clientService,
                reportTable,
                assessmentReportComboBox,
                taskReportComboBox,
                resourceReportComboBox);
    }

    @FXML
    private void showAuditLogs() {
        setVisiblePane(auditPane, "Audit Logs");
        refreshAuditData();
    }

    private void setVisiblePane(VBox activePane, String title) {
        List<VBox> panes = List.of(dashboardPane, reportPane, assessmentPane, coordinationPane,
departmentsPane, resourcesPane, reportsPane, auditPane);
        for (VBox pane : panes) {
            boolean visible = pane == activePane;
            pane.setVisible(visible);
            pane.setManaged(visible);
        }
        pageTitleLabel.setText(title);
    }

    @FXML
    private void submitDisasterReport() {
        String reportTitle = reportTitleField.getText();
        String disasterType = getValue(disasterTypeComboBox);
        String severity = getValue(severityComboBox);
        String location = locationField.getText();
        String description = descriptionArea.getText();
        String reportedBy = reportedByField.getText();
        String contactNumber = contactNumberField.getText();

        String validationError = reportValidationService.validateReport(reportTitle, disasterType, severity, location,
                description, reportedBy, contactNumber);

        if (validationError != null) {
            showWarning(validationError);
            return;
        }

        if (!duplicateCheckedAndPassed) {
            showDuplicateStatus("Duplicate check required before submitting.", "error-label");
            submitReportButton.setDisable(true);
            showWarning("Please check duplicate before submitting the disaster report.");
            return;
        }

        if (!disasterType.trim().equalsIgnoreCase(lastCheckedDisasterType)
                || !location.trim().equalsIgnoreCase(lastCheckedLocation)) {

            duplicateCheckedAndPassed = false;
            submitReportButton.setDisable(true);
            showDuplicateStatus("Disaster type or location changed after duplicate check. Please check again.", "error-label");
            showWarning("Disaster type or location changed after duplicate check. Please check duplicate again.");
            return;
        }

        try {
            boolean duplicate = clientService.checkDuplicate(disasterType, location);
            if (duplicate) {
                duplicateCheckedAndPassed = false;
                submitReportButton.setDisable(true);
                showDuplicateStatus("Duplicate found. Report was not submitted.", "error-label");
                reportResultArea.setText("Duplicate report detected. Please check the Reports page before submitting again.");
                return;
            }

            DisasterReport report = new DisasterReport(reportTitle.trim(), disasterType, severity, location.trim(),
                    description.trim(), reportedBy.trim(), contactNumber.trim(), "Reported",
                    null, null, null);

            DisasterReport savedReport = clientService.submitReport(report);

            reportResultArea.setText(ViewFormatter.buildReportResult(savedReport));
            clearReportForm();

            duplicateCheckedAndPassed = false;
            lastCheckedDisasterType = "";
            lastCheckedLocation = "";
            submitReportButton.setDisable(true);
            showDuplicateStatus("Report saved successfully. Please check duplicate before submitting another report.", "success-label");

            refreshAllData();
        } catch (IOException | ClassNotFoundException exception) {
            showError("Submit Error", exception.getMessage());
        }
    }

    @FXML
    private void checkDuplicateReport() {
        String disasterType = getValue(disasterTypeComboBox);
        String location = locationField.getText();

        if (isBlank(disasterType)) {
            duplicateCheckedAndPassed = false;
            submitReportButton.setDisable(true);
            showDuplicateStatus("Please select disaster type first.", "error-label");
            return;
        }

        if (isBlank(location)) {
            duplicateCheckedAndPassed = false;
            submitReportButton.setDisable(true);
            showDuplicateStatus("Please enter location first.", "error-label");
            return;
        }

        try {
            if (clientService.checkDuplicate(disasterType, location)) {
                duplicateCheckedAndPassed = false;
                submitReportButton.setDisable(true);
                showDuplicateStatus("Duplicate found. A similar active disaster report already exists.", "error-label");
            } else {
                duplicateCheckedAndPassed = true;
                lastCheckedDisasterType = disasterType.trim();
                lastCheckedLocation = location.trim();

                submitReportButton.setDisable(false);
                showDuplicateStatus("No duplicate found. You can now submit the report.", "success-label");
            }
        } catch (IOException | ClassNotFoundException exception) {
            duplicateCheckedAndPassed = false;
            submitReportButton.setDisable(true);
            showError("Duplicate Check Error", exception.getMessage());
        }
    }

    @FXML
    private void generateAssessment() {
        DisasterReport selectedReport = assessmentReportComboBox.getValue();
        String damageLevel = getValue(damageLevelComboBox);

        if (selectedReport == null) {
            showWarning("Select a disaster report to assess.");
            return;
        }
        if (isBlank(damageLevel)) {
            showWarning("Select damage level.");
            return;
        }

        int peopleAffected;
        try {
            peopleAffected = Integer.parseInt(peopleAffectedField.getText().trim());
            if (peopleAffected < 0) {
                showWarning("People affected cannot be negative.");
                return;
            }
        } catch (NumberFormatException exception) {
            showWarning("People affected must be a valid number.");
            return;
        }

        try {
            AssessmentRequest request = new AssessmentRequest(selectedReport, damageLevel,
                    peopleAffected, infrastructureDamageCheckBox.isSelected());
            var assessmentResponse = clientService.saveAssessment(request);
            AssessmentResult result = assessmentResponse.getAssessmentResult();
            List<ResponseTask> generatedTasks = assessmentResponse.getGeneratedTasks();

            selectedReport.setPriorityLevel(result.getPriorityLevel());
            selectedReport.setStatus("Assessed");

            String generatedTaskText = generatedTasks.isEmpty()
                    ? "No new standard tasks were created because the required tasks already exist."
                    : generatedTasks.stream()
                    .map(task -> "• " + task.getActivityType() + " -> " + task.getDepartmentName())
                    .collect(Collectors.joining("\n"));

            assessmentOutputArea.setText(result.getAssessmentSummary()
                    + "\n\nAuto-generated response tasks after assessment:\n"
                    + generatedTaskText);

            String taskDialogMessage = generatedTasks.isEmpty()
                    ? "Standard response tasks already exist for this report, so duplicate tasks were not created."
                    : generatedTasks.size() + " standard response task(s) were auto-generated for this report.";

            showInfo("Assessment Saved & Tasks Prepared",
                    taskDialogMessage
                            + "\n\nYou can add extra/manual tasks from the Add Extra Task tab."
                            + "\nYou can update task progress from the Update Task Status tab.");

            refreshAllData();
            showAssessment();
        } catch (IOException | ClassNotFoundException exception) {
            showError("Assessment Error", exception.getMessage());
        }
    }

    @FXML
    private void createResponseTask() {
        DisasterReport report = taskReportComboBox.getValue();
        Department department = taskDepartmentComboBox.getValue();
        String activityType = getValue(activityTypeComboBox);
        String priority = getValue(taskPriorityComboBox);
        String description = taskDescriptionArea.getText();

        if (report == null || department == null || isBlank(activityType) || isBlank(priority) || isBlank(description)) {
            showWarning("Select report, department, activity, priority and enter task description.");
            return;
        }

        try {
            ResponseTask task = new ResponseTask(report.getReportId(), department.getDepartmentId(),
                    activityType, description.trim(), priority, "Pending");

            clientService.createResponseTask(task);

            coordinationOutputArea.setText("Response task created successfully for " + department.getDepartmentName()
                    + ".\nActivity: " + activityType + "\nPriority: " + priority);

            taskDescriptionArea.clear();
            refreshAllData();
            showCoordination();
        } catch (IOException | ClassNotFoundException exception) {
            showError("Task Error", exception.getMessage());
        }
    }

    @FXML
    private void recommendResourcesForSelectedReport() {
        DisasterReport report = resourceReportComboBox.getValue();

        if (report == null) {
            showWarning("Select a disaster report first.");
            return;
        }

        try {
            String recommendation = clientService.recommendResources(report);

            resourceOutputArea.setText("Recommended resources for " + report + ":\n\n" + recommendation
                    + "\n\nThese recommendations are generated automatically based on disaster type and severity.");
        } catch (IOException | ClassNotFoundException exception) {
            showError("Resource Recommendation Error", exception.getMessage());
        }
    }

    @FXML
    private void allocateSelectedResource() {
        DisasterReport report = resourceReportComboBox.getValue();
        Resource resource = resourceComboBox.getValue();

        if (report == null || resource == null) {
            showWarning("Select report and resource first.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException exception) {
            showWarning("Quantity must be a valid number.");
            return;
        }

        try {
            clientService.allocateResource(report.getReportId(), resource, quantity,
                    "Allocated from Resources page");

            resourceOutputArea.setText("Allocated " + quantity + " x " + resource.getResourceName()
                    + " to " + report.getReportDisplayName() + ".");

            quantityField.clear();
            refreshAllData();
            showResources();
        } catch (IOException | ClassNotFoundException | IllegalArgumentException exception) {
            showError("Resource Allocation Error", exception.getMessage());
        }
    }

    @FXML
    private void searchReports() {
        String keyword = searchField.getText();

        try {
            List<DisasterReport> reports = isBlank(keyword)
                    ? clientService.findAllReports()
                    : clientService.searchReports(keyword.trim());

            reportTable.setItems(FXCollections.observableArrayList(reports));
        } catch (IOException | ClassNotFoundException exception) {
            showError("Search Error", exception.getMessage());
        }
    }

    @FXML
    private void searchAuditRecords() {
        loadAuditRecords(auditSearchField == null ? "" : auditSearchField.getText());
    }

    private void refreshAuditData() {
        loadAuditRecords(auditSearchField == null ? "" : auditSearchField.getText());
    }

    private void loadAuditRecords(String keyword) {
        try {
            List<AuditRecord> auditRecords = isBlank(keyword)
                    ? clientService.findAllAuditEvents()
                    : clientService.searchAuditEvents(keyword.trim());
            auditTable.setItems(FXCollections.observableArrayList(auditRecords));
            updateAuditCharts(auditRecords);
        } catch (IOException | ClassNotFoundException exception) {
            showError("Audit Refresh Error", exception.getMessage());
        }
    }

    private void updateAuditCharts(List<AuditRecord> auditRecords) {
        if (auditActionChart == null) {
            return;
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        auditRecords.stream()
                .collect(Collectors.groupingBy(AuditRecord::getActionType, Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        auditActionChart.getData().setAll(series);
    }

    @FXML
    private void updateSelectedReportStatus() {
        DisasterReport selectedReport = reportTable.getSelectionModel().getSelectedItem();
        String status = getValue(reportStatusComboBox);

        if (selectedReport == null) {
            showWarning("Select a report from the Reports table first.");
            return;
        }

        if (isBlank(status)) {
            showWarning("Select the new report status.");
            return;
        }

        updateReportStatus(selectedReport, status);
    }

    @FXML
    private void markSelectedReportAssessed() {
        DisasterReport selectedReport = reportTable.getSelectionModel().getSelectedItem();

        if (selectedReport == null) {
            showWarning("Select a report from the Reports table first.");
            return;
        }

        updateReportStatus(selectedReport, "Assessed");
    }

    @FXML
    private void completeSelectedReport() {
        DisasterReport selectedReport = reportTable.getSelectionModel().getSelectedItem();

        if (selectedReport == null) {
            showWarning("Select a report from the Reports table first.");
            return;
        }

        updateReportStatus(selectedReport, "Completed");
    }

    private void updateReportStatus(DisasterReport report, String status) {
        try {
            clientService.updateReportStatus(report.getReportId(), status);

            report.setStatus(status);
            reportStatusComboBox.setValue(status);

            List<ResponseTask> tasks = clientService.findTasksByReportId(report.getReportId());
            reportDetailsArea.setText(report.getReportDisplayName() + " status updated to " + status
                    + ".\n\n" + ViewFormatter.buildReportDetails(report, tasks));

            refreshAllData();
        } catch (IOException | ClassNotFoundException exception) {
            showError("Report Status Update Error", exception.getMessage());
        }
    }

    @FXML
    private void updateSelectedDepartmentTaskStatus() {
        ResponseTask selectedTask = departmentTaskTable.getSelectionModel().getSelectedItem();
        String status = getValue(departmentTaskStatusComboBox);

        if (selectedTask == null) {
            showWarning("Select a response task from the Departments page first.");
            return;
        }

        if (isBlank(status)) {
            showWarning("Select the new task status.");
            return;
        }

        updateTaskStatus(selectedTask, status);
    }

    @FXML
    private void markDepartmentTaskPending() {
        ResponseTask selectedTask = departmentTaskTable.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showWarning("Select a response task from the Departments page first.");
            return;
        }

        updateTaskStatus(selectedTask, "Pending");
    }

    @FXML
    private void markDepartmentTaskInProgress() {
        ResponseTask selectedTask = departmentTaskTable.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showWarning("Select a response task from the Departments page first.");
            return;
        }

        updateTaskStatus(selectedTask, "In Progress");
    }

    @FXML
    private void completeDepartmentTask() {
        ResponseTask selectedTask = departmentTaskTable.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showWarning("Select a response task from the Departments page first.");
            return;
        }

        updateTaskStatus(selectedTask, "Completed");
    }

    @FXML
    private void updateSelectedReportTaskStatus() {
        ResponseTask selectedTask = reportTaskComboBox.getValue();
        String status = getValue(reportTaskStatusComboBox);

        if (selectedTask == null) {
            showWarning("Select a response task for the selected report first.");
            return;
        }

        if (isBlank(status)) {
            showWarning("Select the new task status.");
            return;
        }

        updateTaskStatus(selectedTask, status);
    }

    @FXML
    private void markSelectedTaskPending() {
        ResponseTask selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showWarning("Select a task from the Coordination Task List first.");
            return;
        }

        updateTaskStatus(selectedTask, "Pending");
    }

    @FXML
    private void markSelectedTaskInProgress() {
        ResponseTask selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showWarning("Select a task from the Coordination Task List first.");
            return;
        }

        updateTaskStatus(selectedTask, "In Progress");
    }

    @FXML
    private void completeSelectedTask() {
        ResponseTask selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showWarning("Select a task from the Coordination Task List first.");
            return;
        }

        updateTaskStatus(selectedTask, "Completed");
    }

    private void updateTaskStatus(ResponseTask task, String status) {
        try {
            clientService.updateTaskStatus(task.getTaskId(), status);

            task.setStatus(status);

            if (taskTable != null) {
                taskTable.refresh();
            }

            if (departmentTaskTable != null) {
                departmentTaskTable.refresh();
            }

            DisasterReport selectedReport = reportTable.getSelectionModel().getSelectedItem();

            if (selectedReport != null) {
                loadTasksForSelectedReport(selectedReport);
                List<ResponseTask> tasks = clientService.findTasksByReportId(selectedReport.getReportId());
                reportDetailsArea.setText("Task #" + task.getTaskId() + " status updated to " + status
                        + ".\n\n" + ViewFormatter.buildReportDetails(selectedReport, tasks));
            }

            coordinationOutputArea.setText("Task #" + task.getTaskId() + " status updated to " + status + ".");

            refreshAllData();
        } catch (IOException | ClassNotFoundException exception) {
            showError("Task Status Update Error", exception.getMessage());
        }
    }

    @FXML
    private void refreshAllData() {
        UiDataRefresher.refreshDashboard(clientService,
                totalReportsLabel,
                criticalReportsLabel,
                openTasksLabel,
                availableResourcesLabel,
                reportStatusChart,
                taskDepartmentChart,
                resourceAvailabilityChart,
                databaseStatusLabel);

        UiDataRefresher.refreshReportData(clientService,
                reportTable,
                assessmentReportComboBox,
                taskReportComboBox,
                resourceReportComboBox);

        String role = currentUser == null ? "" : currentUser.getRole();

        if (canAssess(role)) {
            UiDataRefresher.refreshAssessmentData(clientService,
                    assessmentTable,
                    assessmentReportComboBox);
            UiDataRefresher.refreshCoordinationData(clientService,
                    taskReportComboBox,
                    taskDepartmentComboBox,
                    taskTable);
        }

        if (canUpdateDepartmentTasks(role)) {
            UiDataRefresher.refreshDepartmentData(clientService,
                    departmentTable,
                    departmentTaskTable);
        }

        if (canManageResources(role)) {
            UiDataRefresher.refreshResourceData(clientService,
                    resourceReportComboBox,
                    resourceComboBox,
                    resourceTable,
                    allocationTable);
        }

        if (canViewAudit(role)) {
            refreshAuditData();
        }
    }

    private void showSelectedReportDetails(DisasterReport report) {
        if (report == null) {
            reportDetailsArea.clear();

            if (reportTaskComboBox != null) {
                reportTaskComboBox.getSelectionModel().clearSelection();
                reportTaskComboBox.setItems(FXCollections.observableArrayList());
            }

            return;
        }

        try {
            List<ResponseTask> tasks = clientService.findTasksByReportId(report.getReportId());
            reportDetailsArea.setText(ViewFormatter.buildReportDetails(report, tasks));
        } catch (IOException | ClassNotFoundException exception) {
            reportDetailsArea.setText(ViewFormatter.buildReportDetails(report, List.of()));
        }
    }

    private void loadTasksForSelectedReport(DisasterReport report) {
        if (reportTaskComboBox == null) {
            return;
        }

        try {
            if (report == null) {
                reportTaskComboBox.setItems(FXCollections.observableArrayList());
                reportTaskComboBox.getSelectionModel().clearSelection();
                return;
            }

            List<ResponseTask> tasks = clientService.findTasksByReportId(report.getReportId());

            reportTaskComboBox.setItems(FXCollections.observableArrayList(tasks));
            reportTaskComboBox.getSelectionModel().clearSelection();
        } catch (IOException | ClassNotFoundException exception) {
            showError("Task Load Error", exception.getMessage());
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

    private String getValue(ComboBox<String> comboBox) {
        return comboBox.getValue() == null ? "" : comboBox.getValue();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void applyRolePermissions() {
        if (currentUser == null) {
            return;
        }

        String role = currentUser.getRole();
        boolean canUpdateReportStatus = hasAnyRole(role, "ADMIN");

        dashboardButton.setDisable(false);
        setButtonAvailable(reportButton, canReport(role));
        setButtonAvailable(assessmentButton, canAssess(role));
        setButtonAvailable(coordinationButton, canCoordinate(role));
        setButtonAvailable(departmentsButton, canUpdateDepartmentTasks(role));
        setButtonAvailable(resourcesButton, canManageResources(role));
        setButtonAvailable(auditButton, canViewAudit(role));
        reportsButton.setDisable(false);
        setButtonAvailable(reportStatusUpdatePane, canUpdateReportStatus);
    }

    private boolean canReport(String role) {
        return hasAnyRole(role, "ADMIN", "REPORTER");
    }

    private boolean canAssess(String role) {
        return hasAnyRole(role, "ADMIN", "ASSESSMENT_OFFICER");
    }

    private boolean canCoordinate(String role) {
        return hasAnyRole(role, "ADMIN", "ASSESSMENT_OFFICER");
    }

    private boolean canUpdateDepartmentTasks(String role) {
        return hasAnyRole(role, "ADMIN", "DEPARTMENT_OFFICER");
    }

    private boolean canManageResources(String role) {
        return hasAnyRole(role, "ADMIN", "RESOURCE_OFFICER");
    }

    private boolean canViewAudit(String role) {
        return hasAnyRole(role, "ADMIN", "AUDITOR");
    }

    private boolean hasAnyRole(String role, String... allowedRoles) {
        for (String allowedRole : allowedRoles) {
            if (allowedRole.equals(role)) {
                return true;
            }
        }
        return false;
    }

    private void setButtonAvailable(Node node, boolean available) {
        node.setDisable(!available);
        node.setVisible(available);
        node.setManaged(available);
    }
}
