package com.sadman.drs.controller.workflow;

import com.sadman.drs.model.AuditRecord;
import com.sadman.drs.model.DisasterReport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkflowFilterTest {

    @Test
    void reportStatusFilterShouldOnlyReturnSelectedStatus() {
        List<DisasterReport> reports = List.of(
                report("Fire in Parramatta", "Fire", "High", "Parramatta", "In Progress"),
                report("Flood in Blacktown", "Flood", "Medium", "Blacktown", "Completed"),
                report("Storm in Penrith", "Storm", "Low", "Penrith", "In Progress")
        );

        List<DisasterReport> filteredReports = ReportWorkflow.applyReportFilters(
                reports,
                "All Severities",
                "In Progress"
        );

        assertEquals(2, filteredReports.size());
        assertEquals("Fire in Parramatta", filteredReports.get(0).getReportTitle());
        assertEquals("Storm in Penrith", filteredReports.get(1).getReportTitle());
    }

    @Test
    void reportStatusFilterShouldCombineWithSeverityFilter() {
        List<DisasterReport> reports = List.of(
                report("High Fire", "Fire", "High", "Parramatta", "Completed"),
                report("Medium Flood", "Flood", "Medium", "Blacktown", "Completed"),
                report("High Storm", "Storm", "High", "Penrith", "In Progress")
        );

        List<DisasterReport> filteredReports = ReportWorkflow.applyReportFilters(
                reports,
                "High",
                "Completed"
        );

        assertEquals(1, filteredReports.size());
        assertEquals("High Fire", filteredReports.get(0).getReportTitle());
    }

    @Test
    void auditActionFilterShouldOnlyReturnSelectedAction() {
        List<AuditRecord> records = List.of(
                audit("Task Created", "Created evacuation task."),
                audit("Report Status Updated", "Status changed to Completed."),
                audit("Task Created", "Created medical task.")
        );

        List<AuditRecord> filteredRecords = AuditWorkflow.applyActionFilter(records, "Task Created");

        assertEquals(2, filteredRecords.size());
        assertEquals("Created evacuation task.", filteredRecords.get(0).getChangeDetails());
        assertEquals("Created medical task.", filteredRecords.get(1).getChangeDetails());
    }

    @Test
    void auditActionFilterShouldReturnAllRecordsForAllActionsOption() {
        List<AuditRecord> records = List.of(
                audit("Task Created", "Created evacuation task."),
                audit("Resource Allocated", "Allocated medical team."),
                audit("Task Deleted", "Deleted duplicate task.")
        );

        List<AuditRecord> filteredRecords = AuditWorkflow.applyActionFilter(records, "All Audit Actions");

        assertEquals(3, filteredRecords.size());
    }

    private DisasterReport report(String title, String disasterType, String severity, String location, String status) {
        return new DisasterReport(
                title,
                disasterType,
                severity,
                location,
                "Description",
                "Reporter",
                "0400000000",
                status,
                "High",
                "Advice",
                "Resources"
        );
    }

    private AuditRecord audit(String actionType, String changeDetails) {
        return new AuditRecord("Task", 1, "Task", actionType, "admin", changeDetails);
    }
}
