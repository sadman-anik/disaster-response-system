package com.sadman.drs.model;

import java.util.List;

/**
 * Shared display values for report status, task status, and priority fields.
 */
public final class StatusValues {
    public static final String REPORTED = "Reported";
    public static final String ASSESSED = "Assessed";
    public static final String IN_PROGRESS = "In Progress";
    public static final String COMPLETED = "Completed";
    public static final String CLOSED = "Closed";
    public static final String PENDING = "Pending";

    public static final String LOW = "Low";
    public static final String MEDIUM = "Medium";
    public static final String HIGH = "High";
    public static final String CRITICAL = "Critical";

    public static final List<String> PRIORITIES = List.of(LOW, MEDIUM, HIGH, CRITICAL);
    public static final List<String> REPORT_STATUSES = List.of(REPORTED, ASSESSED, IN_PROGRESS, COMPLETED);
    public static final List<String> TASK_STATUSES = List.of(PENDING, IN_PROGRESS, COMPLETED);

    public static boolean isTerminalReportStatus(String status) {
        return COMPLETED.equalsIgnoreCase(status) || CLOSED.equalsIgnoreCase(status);
    }

    private StatusValues() {
    }
}
