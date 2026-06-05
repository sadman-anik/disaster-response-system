package com.sadman.drs.server.service;

import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.AuditRecord;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Resource;
import com.sadman.drs.model.ResponseTask;
import com.sadman.drs.model.User;
import com.sadman.drs.server.repository.AuditRepository;

import java.sql.SQLException;

/**
 * Encapsulates audit record creation for important system changes.
 */
public class AuditService {
    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void logReportSubmitted(User user, DisasterReport report) throws SQLException {
        log(user,
                "Report",
                report.getReportId(),
                report.getReportTitle(),
                "Report Submitted",
                "Created report with status '" + report.getStatus() + "' and priority '" + report.getPriorityLevel() + "'.");
    }

    public void logAssessmentSaved(User user, AssessmentResult assessment, DisasterReport report, int generatedTaskCount)
            throws SQLException {
        log(user,
                "Assessment",
                assessment.getAssessmentId(),
                report.getReportTitle(),
                "Assessment Saved",
                "Assessed report " + report.getReportId() + " with priority '" + assessment.getPriorityLevel()
                        + "' and generated " + generatedTaskCount + " standard tasks.");
    }

    public void logTaskCreated(User user, ResponseTask task) throws SQLException {
        log(user,
                "Task",
                task.getTaskId(),
                task.getActivityType(),
                "Task Created",
                "Created task for report " + task.getReportId() + " and department " + task.getDepartmentName() + ".");
    }

    public void logResourceAllocated(User user, int reportId, Resource resource, int quantity) throws SQLException {
        log(user,
                "ResourceAllocation",
                reportId,
                resource.getResourceName(),
                "Resource Allocated",
                "Allocated " + quantity + " of " + resource.getResourceName() + " to report " + reportId + ".");
    }

    public void logReportStatusUpdated(User user, int reportId, String reportTitle, String previousStatus, String newStatus)
            throws SQLException {
        log(user,
                "Report",
                reportId,
                reportTitle,
                "Report Status Updated",
                "Status changed from '" + previousStatus + "' to '" + newStatus + "'.");
    }

    public void logTaskStatusUpdated(User user, int taskId, String taskLabel, String previousStatus, String newStatus)
            throws SQLException {
        log(user,
                "Task",
                taskId,
                taskLabel,
                "Task Status Updated",
                "Status changed from '" + previousStatus + "' to '" + newStatus + "'.");
    }

    private void log(User user, String entityType, int entityId, String entityLabel,
                     String actionType, String changeDetails) throws SQLException {
        if (user == null) {
            return;
        }
        AuditRecord record = new AuditRecord(entityType, entityId, entityLabel, actionType,
                user.getUsername(), changeDetails);
        auditRepository.save(record);
    }
}
