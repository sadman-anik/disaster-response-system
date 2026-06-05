package com.sadman.drs.protocol;

import java.io.Serializable;

public class UpdateTaskStatusRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int taskId;
    private final String status;

    public UpdateTaskStatusRequest(int taskId, String status) {
        this.taskId = taskId;
        this.status = status;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getStatus() {
        return status;
    }
}
