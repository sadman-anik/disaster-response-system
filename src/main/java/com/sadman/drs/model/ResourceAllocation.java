package com.sadman.drs.model;

import java.io.Serializable;

/**
 * Model class for emergency resources allocated to response tasks.
 */
public class ResourceAllocation implements Serializable {
    private int allocationId;
    private int reportId;
    private int taskId;
    private String taskActivityType;
    private int resourceId;
    private String resourceName;
    private int quantityAllocated;
    private String notes;
    private String createdAt;

    public ResourceAllocation() {
    }

    public ResourceAllocation(int allocationId, int reportId, int resourceId, String resourceName,
                              int quantityAllocated, String notes, String createdAt) {
        this(allocationId, reportId, 0, null, resourceId, resourceName, quantityAllocated, notes, createdAt);
    }

    public ResourceAllocation(int allocationId, int reportId, int taskId, String taskActivityType,
                              int resourceId, String resourceName, int quantityAllocated, String notes, String createdAt) {
        this.allocationId = allocationId;
        this.reportId = reportId;
        this.taskId = taskId;
        this.taskActivityType = taskActivityType;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.quantityAllocated = quantityAllocated;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getAllocationId() { return allocationId; }
    public void setAllocationId(int allocationId) { this.allocationId = allocationId; }
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public String getTaskActivityType() { return taskActivityType; }
    public void setTaskActivityType(String taskActivityType) { this.taskActivityType = taskActivityType; }
    public int getResourceId() { return resourceId; }
    public void setResourceId(int resourceId) { this.resourceId = resourceId; }
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    public int getQuantityAllocated() { return quantityAllocated; }
    public void setQuantityAllocated(int quantityAllocated) { this.quantityAllocated = quantityAllocated; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Allocation #" + allocationId + " | " + resourceName + " x " + quantityAllocated;
    }
}
