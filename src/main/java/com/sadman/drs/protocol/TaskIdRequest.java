package com.sadman.drs.protocol;

import java.io.Serializable;

/**
 * Request payload for actions that operate on one response task.
 */
public class TaskIdRequest implements Serializable {
    private final int taskId;

    public TaskIdRequest(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }
}
