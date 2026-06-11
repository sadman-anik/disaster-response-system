package com.sadman.drs.controller;

import com.sadman.drs.client.bridge.DRSClientService;
import com.sadman.drs.controller.security.RolePermissionService;
import com.sadman.drs.controller.validation.ReportValidationService;
import com.sadman.drs.controller.ui.AuditViewHelper;
import com.sadman.drs.controller.ui.ComboBoxInitializer;
import com.sadman.drs.controller.ui.TableSetupHelper;
import com.sadman.drs.controller.ui.UiDataRefresher;
import com.sadman.drs.controller.workflow.AssessmentWorkflow;
import com.sadman.drs.controller.workflow.AuditWorkflow;
import com.sadman.drs.controller.workflow.DuplicateCheckWorkflow;
import com.sadman.drs.controller.workflow.ReportWorkflow;
import com.sadman.drs.controller.workflow.ResourceWorkflow;
import com.sadman.drs.controller.workflow.TaskWorkflow;
import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.AuditRecord;
import com.sadman.drs.model.Department;
import com.sadman.drs.model.DepartmentResourceAlert;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Resource;
import com.sadman.drs.model.ResourceAllocation;
import com.sadman.drs.model.ResponseTask;
import com.sadman.drs.model.StatusValues;
import com.sadman.drs.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

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
    @FXML private Label criticalResourceAlertsLabel;
    @FXML private PieChart reportStatusChart;
    @FXML private BarChart<String, Number> taskDepartmentChart;
    @FXML private BarChart<String, Number> resourceAvailabilityChart;
    @FXML private CategoryAxis resourceAxis;

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
    @FXML private Label resourceCriticalAlertLabel;
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
    @FXML private Label resourceAlertSummaryLabel;
    @FXML private TableView<DepartmentResourceAlert> resourceAlertTable;
    @FXML private TableColumn<DepartmentResourceAlert, String> resourceAlertDepartmentColumn;
    @FXML private TableColumn<DepartmentResourceAlert, String> resourceAlertServiceColumn;
    @FXML private TableColumn<DepartmentResourceAlert, String> resourceAlertResourceColumn;
    @FXML private TableColumn<DepartmentResourceAlert, Integer> resourceAlertQuantityColumn;

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
    @FXML private Button updateReportStatusButton;
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
    private final RolePermissionService rolePermissionService = new RolePermissionService();
    private final DuplicateCheckWorkflow duplicateCheckWorkflow = new DuplicateCheckWorkflow();
    private AssessmentWorkflow assessmentWorkflow;
    private ResourceWorkflow resourceWorkflow;
    private AuditWorkflow auditWorkflow;
    private TaskWorkflow taskWorkflow;
    private ReportWorkflow reportWorkflow;

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
                resourceAlertDepartmentColumn,
                resourceAlertServiceColumn,
                resourceAlertResourceColumn,
                resourceAlertQuantityColumn,
                reportDisplayColumn,
                reportTypeColumn,
                reportSeverityColumn,
                reportLocationColumn,
                reportPriorityColumn,
                reportStatusColumn,
                reportTable,
                reportStatusComboBox,
                this::showSelectedReportDetails);

        AuditViewHelper.initializeAuditTable(
                auditTable,
                auditIdColumn,
                auditWhenColumn,
                auditUserColumn,
                auditActionColumn,
                auditEntityTypeColumn,
                auditEntityLabelColumn,
                auditDetailsColumn);

        initializeDuplicateCheckWorkflow();
        initializeFeatureWorkflows();
        configureReportStatusSelection();
        resourceAxis.setTickLabelRotation(-35);
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
        duplicateCheckWorkflow.initialize(submitReportButton, duplicateWarningLabel, disasterTypeComboBox, locationField);
    }

    private void initializeFeatureWorkflows() {
        reportWorkflow = new ReportWorkflow(
                () -> clientService,
                reportValidationService,
                duplicateCheckWorkflow,
                reportTitleField,
                disasterTypeComboBox,
                severityComboBox,
                locationField,
                reportedByField,
                contactNumberField,
                descriptionArea,
                reportResultArea,
                searchField,
                reportTable,
                reportStatusComboBox,
                reportDetailsArea,
                reportTaskComboBox,
                this::refreshAllData);

        assessmentWorkflow = new AssessmentWorkflow(
                () -> clientService,
                assessmentReportComboBox,
                damageLevelComboBox,
                peopleAffectedField,
                infrastructureDamageCheckBox,
                assessmentOutputArea,
                this::refreshAllData,
                this::showAssessment);

        resourceWorkflow = new ResourceWorkflow(
                () -> clientService,
                resourceReportComboBox,
                resourceComboBox,
                quantityField,
                resourceOutputArea,
                resourceCriticalAlertLabel,
                this::refreshAllData,
                this::showResources);

        auditWorkflow = new AuditWorkflow(
                () -> clientService,
                auditSearchField,
                auditTable,
                auditActionChart);

        taskWorkflow = new TaskWorkflow(
                () -> clientService,
                taskReportComboBox,
                taskDepartmentComboBox,
                activityTypeComboBox,
                taskPriorityComboBox,
                taskDescriptionArea,
                coordinationOutputArea,
                taskTable,
                departmentTaskTable,
                departmentTaskStatusComboBox,
                reportTaskComboBox,
                reportTaskStatusComboBox,
                () -> reportTable.getSelectionModel().getSelectedItem(),
                reportDetailsArea,
                this::refreshAllData,
                this::showCoordination,
                this::loadTasksForSelectedReport);
    }

    @FXML
    private void showDashboard() {
        setVisiblePane(dashboardPane, "Dashboard");
        UiDataRefresher.refreshDashboard(clientService,
                totalReportsLabel,
                criticalReportsLabel,
                openTasksLabel,
                availableResourcesLabel,
                criticalResourceAlertsLabel,
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
                allocationTable,
                resourceAlertSummaryLabel,
                resourceAlertTable);
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
        reportWorkflow.submitDisasterReport();
    }

    @FXML
    private void checkDuplicateReport() {
        reportWorkflow.checkDuplicateReport();
    }

    @FXML
    private void generateAssessment() {
        assessmentWorkflow.generateAssessment();
    }

    @FXML
    private void createResponseTask() {
        taskWorkflow.createResponseTask();
    }

    @FXML
    private void recommendResourcesForSelectedReport() {
        resourceWorkflow.recommendResourcesForSelectedReport();
    }

    @FXML
    private void allocateSelectedResource() {
        resourceWorkflow.allocateSelectedResource();
    }

    @FXML
    private void searchReports() {
        reportWorkflow.searchReports();
    }

    @FXML
    private void searchAuditRecords() {
        auditWorkflow.searchAuditRecords();
    }

    private void refreshAuditData() {
        auditWorkflow.refreshAuditData();
    }

    @FXML
    private void updateSelectedReportStatus() {
        reportWorkflow.updateSelectedReportStatus();
    }

    @FXML
    private void markSelectedReportAssessed() {
        reportWorkflow.markSelectedReportAssessed();
    }

    @FXML
    private void completeSelectedReport() {
        reportWorkflow.completeSelectedReport();
    }

    @FXML
    private void updateSelectedDepartmentTaskStatus() {
        taskWorkflow.updateSelectedDepartmentTaskStatus();
    }

    @FXML
    private void markDepartmentTaskPending() {
        taskWorkflow.markDepartmentTaskPending();
    }

    @FXML
    private void markDepartmentTaskInProgress() {
        taskWorkflow.markDepartmentTaskInProgress();
    }

    @FXML
    private void completeDepartmentTask() {
        taskWorkflow.completeDepartmentTask();
    }

    @FXML
    private void updateSelectedReportTaskStatus() {
        taskWorkflow.updateSelectedReportTaskStatus();
    }

    @FXML
    private void markSelectedTaskPending() {
        taskWorkflow.markSelectedTaskPending();
    }

    @FXML
    private void markSelectedTaskInProgress() {
        taskWorkflow.markSelectedTaskInProgress();
    }

    @FXML
    private void completeSelectedTask() {
        taskWorkflow.completeSelectedTask();
    }

    @FXML
    private void handleLogout() {
        if (clientService != null) {
            clientService.close();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1450, 900);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            Stage stage = (Stage) userStatusLabel.getScene().getWindow();
            stage.setTitle("DRS Login - Disaster Response System");
            stage.setMinWidth(1300);
            stage.setMinHeight(800);
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            databaseStatusLabel.setText("Logout failed");
        }
    }

    @FXML
    private void refreshAllData() {
        UiDataRefresher.refreshDashboard(clientService,
                totalReportsLabel,
                criticalReportsLabel,
                openTasksLabel,
                availableResourcesLabel,
                criticalResourceAlertsLabel,
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

        if (rolePermissionService.canAssess(role)) {
            UiDataRefresher.refreshAssessmentData(clientService,
                    assessmentTable,
                    assessmentReportComboBox);
            UiDataRefresher.refreshCoordinationData(clientService,
                    taskReportComboBox,
                    taskDepartmentComboBox,
                    taskTable);
        }

        if (rolePermissionService.canUpdateDepartmentTasks(role)) {
            UiDataRefresher.refreshDepartmentData(clientService,
                    departmentTable,
                    departmentTaskTable);
        }

        if (rolePermissionService.canManageResources(role)) {
            UiDataRefresher.refreshResourceData(clientService,
                    resourceReportComboBox,
                    resourceComboBox,
                    resourceTable,
                    allocationTable,
                    resourceAlertSummaryLabel,
                    resourceAlertTable);
        }

        if (rolePermissionService.canViewAudit(role)) {
            refreshAuditData();
        }
    }

    private void showSelectedReportDetails(DisasterReport report) {
        if (reportWorkflow == null) {
            return;
        }
        reportWorkflow.showSelectedReportDetails(report);
    }

    private void loadTasksForSelectedReport(DisasterReport report) {
        if (reportWorkflow == null) {
            return;
        }
        reportWorkflow.loadTasksForSelectedReport(report);
    }

    private void applyRolePermissions() {
        if (currentUser == null) {
            return;
        }

        String role = currentUser.getRole();

        dashboardButton.setDisable(false);
        setButtonAvailable(reportButton, rolePermissionService.canReport(role));
        setButtonAvailable(assessmentButton, rolePermissionService.canAssess(role));
        setButtonAvailable(coordinationButton, rolePermissionService.canCoordinate(role));
        setButtonAvailable(departmentsButton, rolePermissionService.canUpdateDepartmentTasks(role));
        setButtonAvailable(resourcesButton, rolePermissionService.canManageResources(role));
        setButtonAvailable(auditButton, rolePermissionService.canViewAudit(role));
        reportsButton.setDisable(false);
        setButtonAvailable(reportStatusUpdatePane, rolePermissionService.canUpdateReportStatus(role));
    }

    private void configureReportStatusSelection() {
        reportTable.setRowFactory(table -> new TableRow<>() {
            @Override
            protected void updateItem(DisasterReport report, boolean empty) {
                super.updateItem(report, empty);
                if (empty || report == null) {
                    setStyle("");
                } else if (StatusValues.isTerminalReportStatus(report.getStatus())) {
                    setStyle("-fx-background-color: #e6e6e6;");
                } else {
                    setStyle("");
                }
            }
        });

        reportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldReport, newReport) -> {
            boolean terminal = newReport != null && StatusValues.isTerminalReportStatus(newReport.getStatus());
            reportStatusComboBox.setDisable(terminal);
            updateReportStatusButton.setDisable(terminal);
        });
    }

    private void setButtonAvailable(Node node, boolean available) {
        node.setDisable(!available);
        node.setVisible(available);
        node.setManaged(available);
    }
}
