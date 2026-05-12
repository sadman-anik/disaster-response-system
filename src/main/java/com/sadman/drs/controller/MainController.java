package com.sadman.drs.controller;

import com.sadman.drs.config.DatabaseConnection;
import com.sadman.drs.model.*;
import com.sadman.drs.repository.*;
import com.sadman.drs.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class that handles all JavaFX UI events.
 * The business logic is delegated to service classes and data persistence is delegated to repositories.
 */
public class MainController {

    @FXML private Label pageTitleLabel;
    @FXML private Label databaseStatusLabel;

    @FXML private VBox dashboardPane;
    @FXML private VBox reportPane;
    @FXML private VBox assessmentPane;
    @FXML private VBox coordinationPane;
    @FXML private VBox departmentsPane;
    @FXML private VBox resourcesPane;
    @FXML private VBox reportsPane;

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
    @FXML private ComboBox<String> reportStatusComboBox;
    @FXML private ComboBox<ResponseTask> reportTaskComboBox;
    @FXML private ComboBox<String> reportTaskStatusComboBox;

    private final DisasterReportRepository disasterReportRepository = new DisasterReportRepository();
    private final AssessmentRepository assessmentRepository = new AssessmentRepository();
    private final DepartmentRepository departmentRepository = new DepartmentRepository();
    private final ResponseTaskRepository responseTaskRepository = new ResponseTaskRepository();
    private final ResourceRepository resourceRepository = new ResourceRepository();

    private final DisasterAssessmentService assessmentService = new DisasterAssessmentService();
    private final EvacuationAdviceService evacuationAdviceService = new EvacuationAdviceService();
    private final ResourceRecommendationService resourceRecommendationService = new ResourceRecommendationService();
    private final ReportValidationService reportValidationService = new ReportValidationService();
    private final DuplicateReportService duplicateReportService = new DuplicateReportService(disasterReportRepository);
    private final DepartmentCoordinationService departmentCoordinationService =
            new DepartmentCoordinationService(departmentRepository, responseTaskRepository);

    @FXML
    private void initialize() {
        initializeComboBoxes();
        initializeTables();
        initializeDuplicateCheckWorkflow();

        try {
            DatabaseConnection.initializeDatabase();
            departmentRepository.seedDefaultDepartments();
            resourceRepository.seedDefaultResources();
            databaseStatusLabel.setText("MySQL connected");
            refreshAllData();
            showDashboard();
        } catch (SQLException | RuntimeException exception) {
            databaseStatusLabel.setText("Database error");
            showError("Database Connection Error",
                    "Could not connect to MySQL. Check database.properties, MySQL server, username and password.\n\n"
                            + exception.getMessage());
        }
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

    private void initializeComboBoxes() {
        disasterTypeComboBox.setItems(FXCollections.observableArrayList(
                "Fire", "Flood", "Earthquake", "Hurricane", "Storm", "Chemical Spill", "Other"));
        severityComboBox.setItems(FXCollections.observableArrayList("Low", "Medium", "High", "Critical"));
        damageLevelComboBox.setItems(FXCollections.observableArrayList("Minor", "Moderate", "Major", "Severe"));
        activityTypeComboBox.setItems(FXCollections.observableArrayList(
                "Warning/Evacuation", "Search and Rescue", "Immediate Assistance",
                "Damage Assessment", "Continuing Assistance", "Infrastructure Restoration", "Debris Removal"));
        taskPriorityComboBox.setItems(FXCollections.observableArrayList("Low", "Medium", "High", "Critical"));
        reportStatusComboBox.setItems(FXCollections.observableArrayList(
                "Reported", "Assessed", "In Progress", "Completed", "Closed"));
        if (reportTaskStatusComboBox != null) {
            reportTaskStatusComboBox.setItems(FXCollections.observableArrayList(
                    "Pending", "In Progress", "Completed"));
        }
        if (departmentTaskStatusComboBox != null) {
            departmentTaskStatusComboBox.setItems(FXCollections.observableArrayList(
                    "Pending", "In Progress", "Completed"));
        }
    }

    private void initializeTables() {
        assessmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("assessmentId"));
        assessmentReportIdColumn.setCellValueFactory(new PropertyValueFactory<>("reportDisplayName"));
        assessmentDamageColumn.setCellValueFactory(new PropertyValueFactory<>("damageLevel"));
        assessmentPeopleColumn.setCellValueFactory(new PropertyValueFactory<>("peopleAffected"));
        assessmentScoreColumn.setCellValueFactory(new PropertyValueFactory<>("priorityScore"));
        assessmentPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priorityLevel"));

        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("taskId"));
        taskReportIdColumn.setCellValueFactory(new PropertyValueFactory<>("reportDisplayName"));
        taskDepartmentColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        taskActivityColumn.setCellValueFactory(new PropertyValueFactory<>("activityType"));
        taskPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priorityLevel"));
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        departmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("departmentId"));
        departmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        departmentServiceColumn.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        departmentContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        departmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("availabilityStatus"));

        departmentTaskIdColumn.setCellValueFactory(new PropertyValueFactory<>("taskId"));
        departmentTaskReportColumn.setCellValueFactory(new PropertyValueFactory<>("reportDisplayName"));
        departmentTaskDepartmentColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        departmentTaskActivityColumn.setCellValueFactory(new PropertyValueFactory<>("activityType"));
        departmentTaskPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priorityLevel"));
        departmentTaskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        departmentTaskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldTask, newTask) -> {
            if (newTask != null) {
                departmentTaskStatusComboBox.setValue(newTask.getStatus());
            }
        });

        resourceIdColumn.setCellValueFactory(new PropertyValueFactory<>("resourceId"));
        resourceNameColumn.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
        resourceCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        resourceQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityAvailable"));

        allocationIdColumn.setCellValueFactory(new PropertyValueFactory<>("allocationId"));
        allocationReportIdColumn.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        allocationResourceColumn.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
        allocationQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityAllocated"));

        reportDisplayColumn.setCellValueFactory(new PropertyValueFactory<>("reportDisplayName"));
        reportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("disasterType"));
        reportSeverityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));
        reportLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        reportPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priorityLevel"));
        reportStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        reportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldReport, newReport) -> {
            showSelectedReportDetails(newReport);
            if (newReport != null) {
                reportStatusComboBox.setValue(newReport.getStatus());
            }
        });
    }

    @FXML
    private void showDashboard() {
        setVisiblePane(dashboardPane, "Dashboard");
        refreshDashboard();
    }

    @FXML
    private void showReportDisaster() {
        setVisiblePane(reportPane, "Report Disaster");
    }

    @FXML
    private void showAssessment() {
        setVisiblePane(assessmentPane, "Assess Report & Auto-Assign Tasks");
        refreshAssessmentData();
    }

    @FXML
    private void showCoordination() {
        setVisiblePane(coordinationPane, "Add Extra Response Task");
        refreshCoordinationData();
    }

    @FXML
    private void showDepartments() {
        setVisiblePane(departmentsPane, "Update Department Task Status");
        refreshDepartmentData();
    }

    @FXML
    private void showResources() {
        setVisiblePane(resourcesPane, "Manage Emergency Resources");
        refreshResourceData();
    }

    @FXML
    private void showReports() {
        setVisiblePane(reportsPane, "Report Status & Search");
        refreshReportData();
    }

    private void setVisiblePane(VBox activePane, String title) {
        List<VBox> panes = List.of(dashboardPane, reportPane, assessmentPane, coordinationPane,
                departmentsPane, resourcesPane, reportsPane);
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
            if (duplicateReportService.isDuplicate(disasterType, location)) {
                duplicateCheckedAndPassed = false;
                submitReportButton.setDisable(true);
                showDuplicateStatus("Duplicate found. Report was not submitted.", "error-label");
                reportResultArea.setText("Duplicate report detected. Please check the Reports page before submitting again.");
                return;
            }

            String priorityLevel = assessmentService.estimateInitialPriority(disasterType, severity);
            String advice = evacuationAdviceService.generateAdvice(disasterType, severity);
            String resources = resourceRecommendationService.recommendResources(disasterType, severity);

            DisasterReport report = new DisasterReport(reportTitle.trim(), disasterType, severity, location.trim(),
                    description.trim(), reportedBy.trim(), contactNumber.trim(), "Reported",
                    priorityLevel, advice, resources);

            disasterReportRepository.save(report);

            reportResultArea.setText(buildReportResult(report));
            clearReportForm();

            duplicateCheckedAndPassed = false;
            lastCheckedDisasterType = "";
            lastCheckedLocation = "";
            submitReportButton.setDisable(true);
            showDuplicateStatus("Report saved successfully. Please check duplicate before submitting another report.", "success-label");

            refreshAllData();
        } catch (SQLException exception) {
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
            if (duplicateReportService.isDuplicate(disasterType, location)) {
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
        } catch (SQLException exception) {
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
            AssessmentResult result = assessmentService.assessDisaster(selectedReport, damageLevel,
                    peopleAffected, infrastructureDamageCheckBox.isSelected());

            assessmentRepository.save(result);
            disasterReportRepository.updateStatusAndPriority(selectedReport.getReportId(), "Assessed", result.getPriorityLevel());

            selectedReport.setPriorityLevel(result.getPriorityLevel());
            selectedReport.setStatus("Assessed");

            List<ResponseTask> generatedTasks = departmentCoordinationService.generateStandardTasks(selectedReport);

            String generatedTaskText = generatedTasks.isEmpty()
                    ? "No new standard tasks were created because the required tasks already exist."
                    : generatedTasks.stream()
                    .map(task -> "• " + task.getActivityType() + " -> " + task.getDepartmentName())
                    .collect(Collectors.joining("\n"));

            assessmentOutputArea.setText(result.getAssessmentSummary()
                    + "\n\nSuggested departments: "
                    + String.join(", ", departmentCoordinationService.determineDepartments(
                    selectedReport.getDisasterType(), selectedReport.getSeverity()))
                    + "\n\nResponse activities: "
                    + String.join(", ", departmentCoordinationService.getStandardActivities(selectedReport.getDisasterType()))
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
        } catch (SQLException exception) {
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

            responseTaskRepository.save(task);

            coordinationOutputArea.setText("Response task created successfully for " + department.getDepartmentName()
                    + ".\nActivity: " + activityType + "\nPriority: " + priority);

            taskDescriptionArea.clear();
            refreshAllData();
            showCoordination();
        } catch (SQLException exception) {
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

        String recommendation = resourceRecommendationService.recommendResources(report.getDisasterType(), report.getSeverity());

        resourceOutputArea.setText("Recommended resources for " + report + ":\n\n" + recommendation
                + "\n\nThese recommendations are generated automatically based on disaster type and severity.");
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
            resourceRepository.allocateResource(report.getReportId(), resource, quantity,
                    "Allocated from Resources page");

            resourceOutputArea.setText("Allocated " + quantity + " x " + resource.getResourceName()
                    + " to " + report.getReportDisplayName() + ".");

            quantityField.clear();
            refreshAllData();
            showResources();
        } catch (SQLException | IllegalArgumentException exception) {
            showError("Resource Allocation Error", exception.getMessage());
        }
    }

    @FXML
    private void searchReports() {
        String keyword = searchField.getText();

        try {
            List<DisasterReport> reports = isBlank(keyword)
                    ? disasterReportRepository.findAll()
                    : disasterReportRepository.search(keyword.trim());

            reportTable.setItems(FXCollections.observableArrayList(reports));
        } catch (SQLException exception) {
            showError("Search Error", exception.getMessage());
        }
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
            disasterReportRepository.updateStatus(report.getReportId(), status);

            report.setStatus(status);
            reportStatusComboBox.setValue(status);

            reportDetailsArea.setText(report.getReportDisplayName() + " status updated to " + status
                    + ".\n\n" + buildReportDetails(report));

            refreshAllData();
        } catch (SQLException exception) {
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
            responseTaskRepository.updateStatus(task.getTaskId(), status);

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
                reportDetailsArea.setText("Task #" + task.getTaskId() + " status updated to " + status
                        + ".\n\n" + buildReportDetails(selectedReport));
            }

            coordinationOutputArea.setText("Task #" + task.getTaskId() + " status updated to " + status + ".");

            refreshAllData();
        } catch (SQLException exception) {
            showError("Task Status Update Error", exception.getMessage());
        }
    }

    @FXML
    private void refreshAllData() {
        refreshDashboard();
        refreshReportData();
        refreshAssessmentData();
        refreshCoordinationData();
        refreshDepartmentData();
        refreshResourceData();
    }

    private void refreshDashboard() {
        try {
            List<DisasterReport> reports = disasterReportRepository.findAll();
            List<ResponseTask> tasks = responseTaskRepository.findAll();
            List<Resource> resources = resourceRepository.findAll();

            long totalReports = reports.size();

            long criticalReports = reports.stream()
                    .filter(report -> "Critical".equalsIgnoreCase(report.getPriorityLevel()))
                    .count();

            long openTasks = tasks.stream()
                    .filter(task -> !"Completed".equalsIgnoreCase(task.getStatus()))
                    .count();

            int totalResources = resources.stream()
                    .mapToInt(Resource::getQuantityAvailable)
                    .sum();

            totalReportsLabel.setText(String.valueOf(totalReports));
            criticalReportsLabel.setText(String.valueOf(criticalReports));
            openTasksLabel.setText(String.valueOf(openTasks));
            availableResourcesLabel.setText(String.valueOf(totalResources));

            updateReportStatusChart(reports);
            updateTaskDepartmentChart(tasks);
            updateResourceAvailabilityChart(resources);
        } catch (SQLException exception) {
            databaseStatusLabel.setText("Database error");
        }
    }

    private void updateReportStatusChart(List<DisasterReport> reports) {
        var chartData = FXCollections.<PieChart.Data>observableArrayList();

        for (String status : List.of("Reported", "Assessed", "In Progress", "Completed", "Closed")) {
            long count = reports.stream()
                    .filter(report -> status.equalsIgnoreCase(report.getStatus()))
                    .count();

            if (count > 0) {
                chartData.add(new PieChart.Data(status, count));
            }
        }

        if (chartData.isEmpty()) {
            chartData.add(new PieChart.Data("No reports yet", 1));
        }

        reportStatusChart.setData(chartData);
    }

    private void updateTaskDepartmentChart(List<ResponseTask> tasks) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("In Progress Tasks");

        tasks.stream()
                .filter(task -> "In Progress".equalsIgnoreCase(task.getStatus()))
                .collect(Collectors.groupingBy(ResponseTask::getDepartmentName, Collectors.counting()))
                .forEach((department, count) -> series.getData().add(new XYChart.Data<>(department, count)));

        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("No in-progress tasks", 0));
        }

        taskDepartmentChart.getData().setAll(series);
    }

    private void updateResourceAvailabilityChart(List<Resource> resources) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Available Resources");

        for (Resource resource : resources) {
            series.getData().add(new XYChart.Data<>(resource.getResourceName(), resource.getQuantityAvailable()));
        }

        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("No resources", 0));
        }

        resourceAvailabilityChart.getData().setAll(series);
    }

    private void refreshReportData() {
        try {
            ObservableList<DisasterReport> reports = FXCollections.observableArrayList(disasterReportRepository.findAll());

            reportTable.setItems(reports);
            assessmentReportComboBox.setItems(reports);
            taskReportComboBox.setItems(reports);
            resourceReportComboBox.setItems(reports);
        } catch (SQLException exception) {
            showError("Report Refresh Error", exception.getMessage());
        }
    }

    private void refreshAssessmentData() {
        try {
            assessmentTable.setItems(FXCollections.observableArrayList(assessmentRepository.findAll()));
            assessmentReportComboBox.setItems(FXCollections.observableArrayList(disasterReportRepository.findAll()));
        } catch (SQLException exception) {
            showError("Assessment Refresh Error", exception.getMessage());
        }
    }

    private void refreshCoordinationData() {
        try {
            taskReportComboBox.setItems(FXCollections.observableArrayList(disasterReportRepository.findAll()));
            taskDepartmentComboBox.setItems(FXCollections.observableArrayList(departmentRepository.findAll()));
            taskTable.setItems(FXCollections.observableArrayList(responseTaskRepository.findAll()));
        } catch (SQLException exception) {
            showError("Coordination Refresh Error", exception.getMessage());
        }
    }

    private void refreshDepartmentData() {
        try {
            departmentTable.setItems(FXCollections.observableArrayList(departmentRepository.findAll()));
            departmentTaskTable.setItems(FXCollections.observableArrayList(responseTaskRepository.findAll()));
        } catch (SQLException exception) {
            showError("Department Refresh Error", exception.getMessage());
        }
    }

    private void refreshResourceData() {
        try {
            resourceReportComboBox.setItems(FXCollections.observableArrayList(disasterReportRepository.findAll()));
            resourceComboBox.setItems(FXCollections.observableArrayList(resourceRepository.findAll()));
            resourceTable.setItems(FXCollections.observableArrayList(resourceRepository.findAll()));
            allocationTable.setItems(FXCollections.observableArrayList(resourceRepository.findAllocations()));
        } catch (SQLException exception) {
            showError("Resource Refresh Error", exception.getMessage());
        }
    }

    private String buildReportResult(DisasterReport report) {
        StringBuilder builder = new StringBuilder();

        builder.append("Disaster report saved successfully.\n\n");
        builder.append("Report: ").append(report.getReportDisplayName()).append("\n");
        builder.append("Title: ").append(report.getReportTitle()).append("\n");
        builder.append("Type: ").append(report.getDisasterType()).append("\n");
        builder.append("Severity: ").append(report.getSeverity()).append("\n");
        builder.append("Location: ").append(report.getLocation()).append("\n");
        builder.append("Initial Priority: ").append(report.getPriorityLevel()).append("\n\n");
        builder.append("Evacuation Advice:\n").append(report.getEvacuationAdvice()).append("\n\n");
        builder.append("Recommended Resources:\n").append(report.getRecommendedResources()).append("\n\n");
        builder.append("Next step: open Assessment & Priority, assess this report, and the system will automatically create the standard response tasks.");

        return builder.toString();
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

        reportDetailsArea.setText(buildReportDetails(report));
    }

    private String buildReportDetails(DisasterReport report) {
        StringBuilder builder = new StringBuilder();

        builder.append("Report: ").append(report.getReportDisplayName())
                .append("\nTitle: ").append(report.getReportTitle())
                .append("\nType: ").append(report.getDisasterType())
                .append("\nSeverity: ").append(report.getSeverity())
                .append("\nLocation: ").append(report.getLocation())
                .append("\nStatus: ").append(report.getStatus())
                .append("\nPriority: ").append(report.getPriorityLevel())
                .append("\nReporter: ").append(report.getReportedBy())
                .append("\nContact: ").append(report.getContactNumber())
                .append("\nCreated: ").append(report.getCreatedAt())
                .append("\n\nDescription:\n").append(report.getDescription())
                .append("\n\nEvacuation Advice:\n").append(report.getEvacuationAdvice())
                .append("\n\nRecommended Resources:\n").append(report.getRecommendedResources());

        try {
            List<ResponseTask> tasks = responseTaskRepository.findByReportId(report.getReportId());

            builder.append("\n\nResponse Tasks:\n");

            if (tasks.isEmpty()) {
                builder.append("No response tasks created yet.");
            } else {
                for (ResponseTask task : tasks) {
                    builder.append("• Task #").append(task.getTaskId())
                            .append(" | ").append(task.getActivityType())
                            .append(" | ").append(task.getDepartmentName())
                            .append(" | ").append(task.getStatus())
                            .append("\n");
                }
            }
        } catch (SQLException exception) {
            builder.append("\n\nCould not load response tasks: ").append(exception.getMessage());
        }

        return builder.toString();
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

            List<ResponseTask> tasks = responseTaskRepository.findByReportId(report.getReportId());

            reportTaskComboBox.setItems(FXCollections.observableArrayList(tasks));
            reportTaskComboBox.getSelectionModel().clearSelection();
        } catch (SQLException exception) {
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
}