package com.sadman.drs.controller.ui;

import com.sadman.drs.client.bridge.DRSClientService;
import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.Department;
import com.sadman.drs.model.DepartmentResourceAlert;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Resource;
import com.sadman.drs.model.ResourceAllocation;
import com.sadman.drs.model.ResponseTask;
import com.sadman.drs.model.StatusValues;
import com.sadman.drs.server.service.DepartmentResourceAlertService;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.List;

public class UiDataRefresher {
    private static final DepartmentResourceAlertService RESOURCE_ALERT_SERVICE = new DepartmentResourceAlertService();

    public static void refreshDashboard(
            DRSClientService clientService,
            Label totalReportsLabel,
            Label criticalReportsLabel,
            Label openTasksLabel,
            Label availableResourcesLabel,
            Label criticalResourceAlertsLabel,
            PieChart reportStatusChart,
            BarChart<String, Number> taskDepartmentChart,
            BarChart<String, Number> resourceAvailabilityChart,
            Label databaseStatusLabel) {
        try {
            List<DisasterReport> reports = clientService.findAllReports();
            List<ResponseTask> tasks = clientService.findAllTasks();
            List<Resource> resources = clientService.findAllResources();
            List<Department> departments = clientService.findAllDepartments();
            List<DepartmentResourceAlert> resourceAlerts =
                    RESOURCE_ALERT_SERVICE.findCriticalAlerts(departments, resources);

            long totalReports = reports.size();
            long criticalReports = reports.stream()
                    .filter(report -> StatusValues.CRITICAL.equalsIgnoreCase(report.getPriorityLevel()))
                    .count();
            long openTasks = tasks.stream()
                    .filter(task -> !StatusValues.COMPLETED.equalsIgnoreCase(task.getStatus()))
                    .count();
            int totalResources = resources.stream()
                    .mapToInt(Resource::getQuantityAvailable)
                    .sum();

            totalReportsLabel.setText(String.valueOf(totalReports));
            criticalReportsLabel.setText(String.valueOf(criticalReports));
            openTasksLabel.setText(String.valueOf(openTasks));
            availableResourcesLabel.setText(String.valueOf(totalResources));
            criticalResourceAlertsLabel.setText(String.valueOf(resourceAlerts.size()));

            reportStatusChart.setData(ViewFormatter.createReportStatusChartData(reports));
            taskDepartmentChart.getData().setAll(ViewFormatter.createTaskDepartmentSeries(tasks));
            resourceAvailabilityChart.getData().setAll(ViewFormatter.createResourceAvailabilitySeries(resources));
        } catch (IOException | ClassNotFoundException exception) {
            databaseStatusLabel.setText("Server error");
        }
    }

    public static void refreshReportData(
            DRSClientService clientService,
            TableView<DisasterReport> reportTable,
            ComboBox<DisasterReport> assessmentReportComboBox,
            ComboBox<DisasterReport> taskReportComboBox,
            ComboBox<DisasterReport> resourceReportComboBox) {
        try {
            var reports = FXCollections.observableArrayList(clientService.findAllReports());
            reportTable.setItems(reports);
            assessmentReportComboBox.setItems(reports);
            taskReportComboBox.setItems(reports);
            resourceReportComboBox.setItems(reports);
        } catch (IOException | ClassNotFoundException exception) {
            showRefreshError("Report Refresh Error", exception.getMessage());
        }
    }

    public static void refreshAssessmentData(
            DRSClientService clientService,
            TableView<AssessmentResult> assessmentTable,
            ComboBox<DisasterReport> assessmentReportComboBox) {
        try {
            assessmentTable.setItems(FXCollections.observableArrayList(clientService.findAllAssessments()));
            assessmentReportComboBox.setItems(FXCollections.observableArrayList(clientService.findAllReports()));
        } catch (IOException | ClassNotFoundException exception) {
            showRefreshError("Assessment Refresh Error", exception.getMessage());
        }
    }

    public static void refreshCoordinationData(
            DRSClientService clientService,
            ComboBox<DisasterReport> taskReportComboBox,
            ComboBox<Department> taskDepartmentComboBox,
            TableView<ResponseTask> taskTable) {
        try {
            taskReportComboBox.setItems(FXCollections.observableArrayList(clientService.findAllReports()));
            taskDepartmentComboBox.setItems(FXCollections.observableArrayList(clientService.findAllDepartments()));
            taskTable.setItems(FXCollections.observableArrayList(clientService.findAllTasks()));
        } catch (IOException | ClassNotFoundException exception) {
            showRefreshError("Coordination Refresh Error", exception.getMessage());
        }
    }

    public static void refreshDepartmentData(
            DRSClientService clientService,
            TableView<Department> departmentTable,
            TableView<ResponseTask> departmentTaskTable) {
        try {
            departmentTable.setItems(FXCollections.observableArrayList(clientService.findAllDepartments()));
            departmentTaskTable.setItems(FXCollections.observableArrayList(clientService.findAllTasks()));
        } catch (IOException | ClassNotFoundException exception) {
            showRefreshError("Department Refresh Error", exception.getMessage());
        }
    }

    public static void refreshResourceData(
            DRSClientService clientService,
            ComboBox<DisasterReport> resourceReportComboBox,
            ComboBox<Resource> resourceComboBox,
            TableView<Resource> resourceTable,
            TableView<ResourceAllocation> allocationTable,
            Label resourceAlertSummaryLabel,
            TableView<DepartmentResourceAlert> resourceAlertTable) {
        try {
            List<Resource> resources = clientService.findAllResources();
            List<DepartmentResourceAlert> resourceAlerts =
                    RESOURCE_ALERT_SERVICE.findCriticalAlerts(clientService.findAllDepartments(), resources);

            resourceReportComboBox.setItems(FXCollections.observableArrayList(clientService.findAllReports()));
            resourceComboBox.setItems(FXCollections.observableArrayList(resources));
            resourceTable.setItems(FXCollections.observableArrayList(resources));
            allocationTable.setItems(FXCollections.observableArrayList(clientService.findAllAllocations()));
            resourceAlertSummaryLabel.setText(buildResourceAlertSummary(resourceAlerts));
            resourceAlertTable.setItems(FXCollections.observableArrayList(resourceAlerts));
        } catch (IOException | ClassNotFoundException exception) {
            showRefreshError("Resource Refresh Error", exception.getMessage());
        }
    }

    private static String buildResourceAlertSummary(List<DepartmentResourceAlert> alerts) {
        if (alerts.isEmpty()) {
            return "All departments have sufficient matching resources.";
        }
        if (alerts.size() == 1) {
            return "1 department resource is critically low. Review responsiveness before assigning new tasks.";
        }
        return alerts.size() + " department resources are critically low. Review responsiveness before assigning new tasks.";
    }

    private static void showRefreshError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
