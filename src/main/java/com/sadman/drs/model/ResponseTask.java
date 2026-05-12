package com.sadman.drs.model;

/**
 * Model class for response activities such as evacuation, search and rescue, assistance, and restoration.
 */
public class ResponseTask {
    private int taskId;
    private int reportId;
    private String reportTitle;
    private int departmentId;
    private String departmentName;
    private String activityType;
    private String taskDescription;
    private String priorityLevel;
    private String status;
    private String createdAt;

    public ResponseTask() {
    }

    public ResponseTask(int taskId, int reportId, String reportTitle, int departmentId, String departmentName,
                        String activityType, String taskDescription, String priorityLevel,
                        String status, String createdAt) {
        this.taskId = taskId;
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.activityType = activityType;
        this.taskDescription = taskDescription;
        this.priorityLevel = priorityLevel;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Backward compatible constructor used by existing task creation code.
     */
    public ResponseTask(int taskId, int reportId, int departmentId, String departmentName,
                        String activityType, String taskDescription, String priorityLevel,
                        String status, String createdAt) {
        this(taskId, reportId, null, departmentId, departmentName, activityType, taskDescription,
                priorityLevel, status, createdAt);
    }

    public ResponseTask(int reportId, int departmentId, String activityType,
                        String taskDescription, String priorityLevel, String status) {
        this(0, reportId, null, departmentId, null, activityType, taskDescription, priorityLevel, status, null);
    }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }

    /**
     * Used in task tables and combo boxes to show report ID with report title.
     */
    public String getReportDisplayName() {
        String title = (reportTitle == null || reportTitle.trim().isEmpty())
                ? "Untitled Disaster Report"
                : reportTitle.trim();
        if (reportId <= 0) {
            return title;
        }
        return "#" + reportId + " - " + title;
    }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Task #" + taskId + " | " + getReportDisplayName() + " | " + activityType + " | " + status;
    }
}
