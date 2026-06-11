package com.sadman.drs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusValuesTest {

    @Test
    void reportStatusChoicesUseCompletedAsOnlyVisibleTerminalStatus() {
        assertTrue(StatusValues.REPORT_STATUSES.contains(StatusValues.COMPLETED));
        assertFalse(StatusValues.REPORT_STATUSES.contains(StatusValues.CLOSED));
    }

    @Test
    void legacyClosedStatusIsStillTreatedAsTerminal() {
        assertTrue(StatusValues.isTerminalReportStatus(StatusValues.COMPLETED));
        assertTrue(StatusValues.isTerminalReportStatus(StatusValues.CLOSED));
        assertFalse(StatusValues.isTerminalReportStatus(StatusValues.IN_PROGRESS));
    }
}
