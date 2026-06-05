package com.sadman.drs.protocol;

import java.io.Serializable;

public class UpdateReportStatusRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int reportId;
    private final String status;

    public UpdateReportStatusRequest(int reportId, String status) {
        this.reportId = reportId;
        this.status = status;
    }

    public int getReportId() {
        return reportId;
    }

    public String getStatus() {
        return status;
    }
}
