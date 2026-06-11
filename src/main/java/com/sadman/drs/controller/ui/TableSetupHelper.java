package com.sadman.drs.controller.ui;

import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.Department;
import com.sadman.drs.model.DepartmentResourceAlert;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Resource;
import com.sadman.drs.model.ResourceAllocation;
import com.sadman.drs.model.ResponseTask;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.function.Consumer;

public class TableSetupHelper {

    public static void initializeTables(
            TableColumn<AssessmentResult, Integer> assessmentIdColumn,
            TableColumn<AssessmentResult, String> assessmentReportIdColumn,
            TableColumn<AssessmentResult, String> assessmentDamageColumn,
            TableColumn<AssessmentResult, Integer> assessmentPeopleColumn,
            TableColumn<AssessmentResult, Integer> assessmentScoreColumn,
            TableColumn<AssessmentResult, String> assessmentPriorityColumn,
            TableColumn<ResponseTask, Integer> taskIdColumn,
            TableColumn<ResponseTask, String> taskReportIdColumn,
            TableColumn<ResponseTask, String> taskDepartmentColumn,
            TableColumn<ResponseTask, String> taskActivityColumn,
            TableColumn<ResponseTask, String> taskPriorityColumn,
            TableColumn<ResponseTask, String> taskStatusColumn,
            TableColumn<Department, Integer> departmentIdColumn,
            TableColumn<Department, String> departmentNameColumn,
            TableColumn<Department, String> departmentServiceColumn,
            TableColumn<Department, String> departmentContactColumn,
            TableColumn<Department, String> departmentStatusColumn,
            TableView<ResponseTask> departmentTaskTable,
            ComboBox<String> departmentTaskStatusComboBox,
            TableColumn<ResponseTask, Integer> departmentTaskIdColumn,
            TableColumn<ResponseTask, String> departmentTaskReportColumn,
            TableColumn<ResponseTask, String> departmentTaskDepartmentColumn,
            TableColumn<ResponseTask, String> departmentTaskActivityColumn,
            TableColumn<ResponseTask, String> departmentTaskPriorityColumn,
            TableColumn<ResponseTask, String> departmentTaskStatusColumn,
            TableColumn<Resource, Integer> resourceIdColumn,
            TableColumn<Resource, String> resourceNameColumn,
            TableColumn<Resource, String> resourceCategoryColumn,
            TableColumn<Resource, Integer> resourceQuantityColumn,
            TableColumn<ResourceAllocation, Integer> allocationIdColumn,
            TableColumn<ResourceAllocation, Integer> allocationReportIdColumn,
            TableColumn<ResourceAllocation, String> allocationResourceColumn,
            TableColumn<ResourceAllocation, Integer> allocationQuantityColumn,
            TableColumn<DepartmentResourceAlert, String> dashboardAlertDepartmentColumn,
            TableColumn<DepartmentResourceAlert, String> dashboardAlertResourceColumn,
            TableColumn<DepartmentResourceAlert, Integer> dashboardAlertQuantityColumn,
            TableColumn<DepartmentResourceAlert, String> dashboardAlertMessageColumn,
            TableColumn<DepartmentResourceAlert, String> resourceAlertDepartmentColumn,
            TableColumn<DepartmentResourceAlert, String> resourceAlertServiceColumn,
            TableColumn<DepartmentResourceAlert, String> resourceAlertResourceColumn,
            TableColumn<DepartmentResourceAlert, Integer> resourceAlertQuantityColumn,
            TableColumn<DisasterReport, String> reportDisplayColumn,
            TableColumn<DisasterReport, String> reportTypeColumn,
            TableColumn<DisasterReport, String> reportSeverityColumn,
            TableColumn<DisasterReport, String> reportLocationColumn,
            TableColumn<DisasterReport, String> reportPriorityColumn,
            TableColumn<DisasterReport, String> reportStatusColumn,
            TableView<DisasterReport> reportTable,
            ComboBox<String> reportStatusComboBox,
            Consumer<DisasterReport> reportSelectionCallback) {

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

        dashboardAlertDepartmentColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        dashboardAlertResourceColumn.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
        dashboardAlertQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityAvailable"));
        dashboardAlertMessageColumn.setCellValueFactory(new PropertyValueFactory<>("alertMessage"));

        resourceAlertDepartmentColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        resourceAlertServiceColumn.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        resourceAlertResourceColumn.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
        resourceAlertQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityAvailable"));

        reportDisplayColumn.setCellValueFactory(new PropertyValueFactory<>("reportDisplayName"));
        reportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("disasterType"));
        reportSeverityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));
        reportLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        reportPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priorityLevel"));
        reportStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        reportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldReport, newReport) -> {
            reportSelectionCallback.accept(newReport);
            if (newReport != null) {
                reportStatusComboBox.setValue(newReport.getStatus());
            }
        });
    }
}
