package com.sadman.drs.controller.workflow;

import com.sadman.drs.client.bridge.DRSClientService;
import com.sadman.drs.controller.ui.AlertHelper;
import com.sadman.drs.controller.ui.ViewFormatter;
import com.sadman.drs.controller.validation.FormValueHelper;
import com.sadman.drs.model.Department;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.ResponseTask;
import com.sadman.drs.model.StatusValues;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Handles response-task creation and task status workflow for the main JavaFX view.
 */
public class TaskWorkflow {
    private final Supplier<DRSClientService> clientServiceSupplier;
    private final ComboBox<DisasterReport> taskReportComboBox;
    private final ComboBox<Department> taskDepartmentComboBox;
    private final ComboBox<String> activityTypeComboBox;
    private final ComboBox<String> taskPriorityComboBox;
    private final TextArea taskDescriptionArea;
    private final TextArea coordinationOutputArea;
    private final TableView<ResponseTask> taskTable;
    private final TableView<ResponseTask> departmentTaskTable;
    private final ComboBox<String> departmentTaskStatusComboBox;
    private final ComboBox<ResponseTask> reportTaskComboBox;
    private final ComboBox<String> reportTaskStatusComboBox;
    private final Supplier<DisasterReport> selectedReportSupplier;
    private final TextArea reportDetailsArea;
    private final Runnable refreshAllData;
    private final Runnable showCoordination;
    private final Consumer<DisasterReport> loadTasksForSelectedReport;

    public TaskWorkflow(Supplier<DRSClientService> clientServiceSupplier,
                        ComboBox<DisasterReport> taskReportComboBox,
                        ComboBox<Department> taskDepartmentComboBox,
                        ComboBox<String> activityTypeComboBox,
                        ComboBox<String> taskPriorityComboBox,
                        TextArea taskDescriptionArea,
                        TextArea coordinationOutputArea,
                        TableView<ResponseTask> taskTable,
                        TableView<ResponseTask> departmentTaskTable,
                        ComboBox<String> departmentTaskStatusComboBox,
                        ComboBox<ResponseTask> reportTaskComboBox,
                        ComboBox<String> reportTaskStatusComboBox,
                        Supplier<DisasterReport> selectedReportSupplier,
                        TextArea reportDetailsArea,
                        Runnable refreshAllData,
                        Runnable showCoordination,
                        Consumer<DisasterReport> loadTasksForSelectedReport) {
        this.clientServiceSupplier = clientServiceSupplier;
        this.taskReportComboBox = taskReportComboBox;
        this.taskDepartmentComboBox = taskDepartmentComboBox;
        this.activityTypeComboBox = activityTypeComboBox;
        this.taskPriorityComboBox = taskPriorityComboBox;
        this.taskDescriptionArea = taskDescriptionArea;
        this.coordinationOutputArea = coordinationOutputArea;
        this.taskTable = taskTable;
        this.departmentTaskTable = departmentTaskTable;
        this.departmentTaskStatusComboBox = departmentTaskStatusComboBox;
        this.reportTaskComboBox = reportTaskComboBox;
        this.reportTaskStatusComboBox = reportTaskStatusComboBox;
        this.selectedReportSupplier = selectedReportSupplier;
        this.reportDetailsArea = reportDetailsArea;
        this.refreshAllData = refreshAllData;
        this.showCoordination = showCoordination;
        this.loadTasksForSelectedReport = loadTasksForSelectedReport;
    }

    public void createResponseTask() {
        DisasterReport report = taskReportComboBox.getValue();
        Department department = taskDepartmentComboBox.getValue();
        String activityType = FormValueHelper.getValue(activityTypeComboBox);
        String priority = FormValueHelper.getValue(taskPriorityComboBox);
        String description = taskDescriptionArea.getText();

        if (report == null || department == null || FormValueHelper.isBlank(activityType)
                || FormValueHelper.isBlank(priority) || FormValueHelper.isBlank(description)) {
            AlertHelper.showWarning("Select report, department, activity, priority and enter task description.");
            return;
        }

        try {
            ResponseTask task = new ResponseTask(report.getReportId(), department.getDepartmentId(),
                    activityType, description.trim(), priority, StatusValues.PENDING);
            clientServiceSupplier.get().createResponseTask(task);
            coordinationOutputArea.setText("Response task created successfully for " + department.getDepartmentName()
                    + ".\nActivity: " + activityType + "\nPriority: " + priority);
            taskDescriptionArea.clear();
            refreshAllData.run();
            showCoordination.run();
        } catch (IOException | ClassNotFoundException | IllegalStateException exception) {
            AlertHelper.showError("Task Error", exception.getMessage());
        }
    }

    public void deleteSelectedResponseTask() {
        ResponseTask selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            AlertHelper.showWarning("Select a response task from the Response Task List first.");
            return;
        }
        if (!StatusValues.PENDING.equalsIgnoreCase(selectedTask.getStatus())) {
            AlertHelper.showWarning("Only pending response tasks can be deleted.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Response Task");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete task #" + selectedTask.getTaskId() + " (" + selectedTask.getActivityType()
                + ")? Any allocated resources for this task will be released.");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            clientServiceSupplier.get().deleteResponseTask(selectedTask.getTaskId());
            coordinationOutputArea.setText("Task #" + selectedTask.getTaskId()
                    + " deleted. Any allocated resources for that task were released.");
            refreshAllData.run();
            showCoordination.run();
        } catch (IOException | ClassNotFoundException | IllegalStateException exception) {
            AlertHelper.showError("Task Delete Error", exception.getMessage());
        }
    }

    public void updateSelectedDepartmentTaskStatus() {
        ResponseTask selectedTask = departmentTaskTable.getSelectionModel().getSelectedItem();
        String status = FormValueHelper.getValue(departmentTaskStatusComboBox);
        if (selectedTask == null) {
            AlertHelper.showWarning("Select a response task from the Departments page first.");
            return;
        }
        if (FormValueHelper.isBlank(status)) {
            AlertHelper.showWarning("Select the new task status.");
            return;
        }
        updateTaskStatus(selectedTask, status);
    }

    public void markDepartmentTaskPending() {
        updateSelectedDepartmentTask(StatusValues.PENDING);
    }

    public void markDepartmentTaskInProgress() {
        updateSelectedDepartmentTask(StatusValues.IN_PROGRESS);
    }

    public void completeDepartmentTask() {
        updateSelectedDepartmentTask(StatusValues.COMPLETED);
    }

    public void updateSelectedReportTaskStatus() {
        ResponseTask selectedTask = reportTaskComboBox.getValue();
        String status = FormValueHelper.getValue(reportTaskStatusComboBox);
        if (selectedTask == null) {
            AlertHelper.showWarning("Select a response task for the selected report first.");
            return;
        }
        if (FormValueHelper.isBlank(status)) {
            AlertHelper.showWarning("Select the new task status.");
            return;
        }
        updateTaskStatus(selectedTask, status);
    }

    public void markSelectedTaskPending() {
        updateSelectedCoordinationTask(StatusValues.PENDING);
    }

    public void markSelectedTaskInProgress() {
        updateSelectedCoordinationTask(StatusValues.IN_PROGRESS);
    }

    public void completeSelectedTask() {
        updateSelectedCoordinationTask(StatusValues.COMPLETED);
    }

    private void updateSelectedDepartmentTask(String status) {
        ResponseTask selectedTask = departmentTaskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            AlertHelper.showWarning("Select a response task from the Departments page first.");
            return;
        }
        updateTaskStatus(selectedTask, status);
    }

    private void updateSelectedCoordinationTask(String status) {
        ResponseTask selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            AlertHelper.showWarning("Select a task from the Coordination Task List first.");
            return;
        }
        updateTaskStatus(selectedTask, status);
    }

    private void updateTaskStatus(ResponseTask task, String status) {
        try {
            clientServiceSupplier.get().updateTaskStatus(task.getTaskId(), status);
            task.setStatus(status);

            if (taskTable != null) {
                taskTable.refresh();
            }
            if (departmentTaskTable != null) {
                departmentTaskTable.refresh();
            }

            DisasterReport selectedReport = selectedReportSupplier.get();
            if (selectedReport != null) {
                loadTasksForSelectedReport.accept(selectedReport);
                List<ResponseTask> tasks = clientServiceSupplier.get().findTasksByReportId(selectedReport.getReportId());
                reportDetailsArea.setText("Task #" + task.getTaskId() + " status updated to " + status
                        + ".\n\n" + ViewFormatter.buildReportDetails(selectedReport, tasks));
            }

            coordinationOutputArea.setText("Task #" + task.getTaskId() + " status updated to " + status + ".");
            refreshAllData.run();
        } catch (IOException | ClassNotFoundException | IllegalStateException exception) {
            AlertHelper.showError("Task Status Update Error", exception.getMessage());
        }
    }

}
