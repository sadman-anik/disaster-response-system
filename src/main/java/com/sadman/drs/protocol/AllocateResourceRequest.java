package com.sadman.drs.protocol;

import com.sadman.drs.model.Resource;

import java.io.Serializable;

public class AllocateResourceRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int reportId;
    private final int taskId;
    private final Resource resource;
    private final int quantity;
    private final String notes;

    public AllocateResourceRequest(int reportId, int taskId, Resource resource, int quantity, String notes) {
        this.reportId = reportId;
        this.taskId = taskId;
        this.resource = resource;
        this.quantity = quantity;
        this.notes = notes;
    }

    public int getReportId() {
        return reportId;
    }

    public int getTaskId() {
        return taskId;
    }

    public Resource getResource() {
        return resource;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getNotes() {
        return notes;
    }
}
