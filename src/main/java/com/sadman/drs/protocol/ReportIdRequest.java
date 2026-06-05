package com.sadman.drs.protocol;

import java.io.Serializable;

public class ReportIdRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int reportId;

    public ReportIdRequest(int reportId) {
        this.reportId = reportId;
    }

    public int getReportId() {
        return reportId;
    }
}
