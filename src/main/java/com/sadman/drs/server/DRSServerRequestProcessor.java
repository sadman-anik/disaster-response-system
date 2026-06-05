package com.sadman.drs.server;

import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.Department;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Resource;
import com.sadman.drs.model.ResourceAllocation;
import com.sadman.drs.model.ResponseTask;
import com.sadman.drs.model.User;
import com.sadman.drs.protocol.AllocateResourceRequest;
import com.sadman.drs.protocol.AuthenticationRequest;
import com.sadman.drs.protocol.AssessmentRequest;
import com.sadman.drs.protocol.AssessmentResponse;
import com.sadman.drs.protocol.CheckDuplicateRequest;
import com.sadman.drs.protocol.ReportIdRequest;
import com.sadman.drs.protocol.SearchReportsRequest;
import com.sadman.drs.protocol.ServerAction;
import com.sadman.drs.protocol.ServerRequest;
import com.sadman.drs.protocol.ServerResponse;
import com.sadman.drs.model.AuditRecord;
import com.sadman.drs.protocol.AuditSearchRequest;
import com.sadman.drs.protocol.UpdateReportStatusRequest;
import com.sadman.drs.protocol.UpdateTaskStatusRequest;
import com.sadman.drs.protocol.UserRegistrationRequest;
import com.sadman.drs.server.repository.AuditRepository;
import com.sadman.drs.server.repository.AssessmentRepository;
import com.sadman.drs.server.repository.DepartmentRepository;
import com.sadman.drs.server.repository.DisasterReportRepository;
import com.sadman.drs.server.repository.ResourceRepository;
import com.sadman.drs.server.repository.ResponseTaskRepository;
import com.sadman.drs.server.repository.UserRepository;
import com.sadman.drs.server.service.DepartmentCoordinationService;
import com.sadman.drs.server.service.DisasterAssessmentService;
import com.sadman.drs.server.service.DuplicateReportService;
import com.sadman.drs.server.service.EvacuationAdviceService;
import com.sadman.drs.server.service.ResourceRecommendationService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DRSServerRequestProcessor {
    private final DisasterReportRepository disasterReportRepository = new DisasterReportRepository();
    private final AssessmentRepository assessmentRepository = new AssessmentRepository();
    private final DepartmentRepository departmentRepository = new DepartmentRepository();
    private final ResponseTaskRepository responseTaskRepository = new ResponseTaskRepository();
    private final ResourceRepository resourceRepository = new ResourceRepository();
    private final UserRepository userRepository = new UserRepository();
    private final AuditRepository auditRepository = new AuditRepository();

    private final DisasterAssessmentService assessmentService = new DisasterAssessmentService();
    private final EvacuationAdviceService evacuationAdviceService = new EvacuationAdviceService();
    private final ResourceRecommendationService resourceRecommendationService = new ResourceRecommendationService();
    private final DuplicateReportService duplicateReportService = new DuplicateReportService(disasterReportRepository);
    private final DepartmentCoordinationService departmentCoordinationService =
            new DepartmentCoordinationService(departmentRepository, responseTaskRepository);

    private boolean isAuthenticated(User currentUser) {
        return currentUser != null;
    }

    private boolean isAuthorized(User currentUser, ServerAction action) {
        if (currentUser == null) {
            return false;
        }
        String role = currentUser.getRole();
        if ("ADMIN".equals(role)) {
            return true;
        }
        if ("REPORTER".equals(role)) {
            switch (action) {
                case PING:
                case AUTHENTICATE:
                case FETCH_REPORTS:
                case SEARCH_REPORTS:
                case CHECK_DUPLICATE:
                case SUBMIT_REPORT:
                case FETCH_TASKS:
                case FETCH_RESOURCES:
                case FETCH_ALLOCATIONS:
                    return true;
                default:
                    return false;
            }
        }
        if ("ASSESSMENT_OFFICER".equals(role)) {
            switch (action) {
                case PING:
                case AUTHENTICATE:
                case FETCH_REPORTS:
                case SEARCH_REPORTS:
                case CHECK_DUPLICATE:
                case SAVE_ASSESSMENT:
                case FETCH_ASSESSMENTS:
                case FETCH_DEPARTMENTS:
                case FETCH_TASKS:
                case FETCH_RESOURCES:
                case FETCH_ALLOCATIONS:
                case CREATE_RESPONSE_TASK:
                case UPDATE_TASK_STATUS:
                case FETCH_TASKS_BY_REPORT:
                case RECOMMEND_RESOURCES:
                    return true;
                default:
                    return false;
            }
        }
        if ("RESOURCE_OFFICER".equals(role)) {
            switch (action) {
                case PING:
                case AUTHENTICATE:
                case FETCH_REPORTS:
                case SEARCH_REPORTS:
                case FETCH_ASSESSMENTS:
                case FETCH_TASKS:
                case FETCH_RESOURCES:
                case FETCH_ALLOCATIONS:
                case ALLOCATE_RESOURCE:
                case FETCH_TASKS_BY_REPORT:
                case RECOMMEND_RESOURCES:
                    return true;
                default:
                    return false;
            }
        }
        if ("DEPARTMENT_OFFICER".equals(role)) {
            switch (action) {
                case PING:
                case AUTHENTICATE:
                case FETCH_REPORTS:
                case SEARCH_REPORTS:
                case FETCH_DEPARTMENTS:
                case FETCH_TASKS:
                case FETCH_RESOURCES:
                case FETCH_ALLOCATIONS:
                case UPDATE_TASK_STATUS:
                case FETCH_TASKS_BY_REPORT:
                    return true;
                default:
                    return false;
            }
        }
        if ("AUDITOR".equals(role)) {
            switch (action) {
                case PING:
                case AUTHENTICATE:
                case FETCH_REPORTS:
                case SEARCH_REPORTS:
                case FETCH_ASSESSMENTS:
                case FETCH_DEPARTMENTS:
                case FETCH_TASKS:
                case FETCH_RESOURCES:
                case FETCH_ALLOCATIONS:
                case FETCH_TASKS_BY_REPORT:
                case RECOMMEND_RESOURCES:
                case FETCH_AUDIT_EVENTS:
                case SEARCH_AUDIT_EVENTS:
                    return true;
                default:
                    return false;
            }
        }
        if ("VIEWER".equals(role)) {
            switch (action) {
                case PING:
                case AUTHENTICATE:
                case FETCH_REPORTS:
                case SEARCH_REPORTS:
                case FETCH_ASSESSMENTS:
                case FETCH_DEPARTMENTS:
                case FETCH_TASKS:
                case FETCH_RESOURCES:
                case FETCH_ALLOCATIONS:
                case FETCH_TASKS_BY_REPORT:
                case RECOMMEND_RESOURCES:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private ServerResponse handleAuthentication(Object payload) throws SQLException {
        if (!(payload instanceof AuthenticationRequest request)) {
            return ServerResponse.failure("Invalid authentication payload.");
        }
        User user = userRepository.authenticate(request.getUsername(), request.getPassword());
        if (user == null) {
            return ServerResponse.failure("Username or password is invalid.");
        }
        return ServerResponse.success("Authentication successful", user);
    }

    private ServerResponse handleUserRegistration(Object payload) throws SQLException {
        if (!(payload instanceof UserRegistrationRequest request)) {
            return ServerResponse.failure("Invalid registration payload.");
        }
        try {
            User newUser = userRepository.registerUser(request.getUsername(), request.getPassword(), request.getRole());
            if (newUser == null) {
                return ServerResponse.failure("Username already exists.");
            }
            return ServerResponse.success("Registration successful", newUser);
        } catch (IllegalArgumentException exception) {
            return ServerResponse.failure(exception.getMessage());
        }
    }

    public ServerResponse processRequest(ServerRequest request, User currentUser) {
        if (request == null || request.getAction() == null) {
            return ServerResponse.failure("Invalid request received.");
        }

        try {
            if (request.getAction() != ServerAction.AUTHENTICATE && request.getAction() != ServerAction.REGISTER_USER && !isAuthenticated(currentUser)) {
                return ServerResponse.failure("Authentication required. Please login first.");
            }

            if (request.getAction() != ServerAction.AUTHENTICATE && request.getAction() != ServerAction.REGISTER_USER && !isAuthorized(currentUser, request.getAction())) {
                return ServerResponse.failure("Access denied. Your role does not permit this action.");
            }

            return switch (request.getAction()) {
                case PING -> ServerResponse.success("PONG", null);
                case AUTHENTICATE -> handleAuthentication(request.getPayload());
                case REGISTER_USER -> handleUserRegistration(request.getPayload());
                case SUBMIT_REPORT -> handleSubmitReport(request.getPayload(), currentUser);
                case FETCH_REPORTS -> ServerResponse.success("Reports fetched", disasterReportRepository.findAll());
                case SEARCH_REPORTS -> handleSearchReports(request.getPayload());
                case CHECK_DUPLICATE -> handleDuplicateCheck(request.getPayload());
                case SAVE_ASSESSMENT -> handleSaveAssessment(request.getPayload(), currentUser);
                case FETCH_ASSESSMENTS -> ServerResponse.success("Assessments fetched", assessmentRepository.findAll());
                case FETCH_DEPARTMENTS -> ServerResponse.success("Departments fetched", departmentRepository.findAll());
                case FETCH_TASKS -> ServerResponse.success("Tasks fetched", responseTaskRepository.findAll());
                case FETCH_RESOURCES -> ServerResponse.success("Resources fetched", resourceRepository.findAll());
                case FETCH_ALLOCATIONS -> ServerResponse.success("Allocations fetched", resourceRepository.findAllocations());
                case CREATE_RESPONSE_TASK -> handleCreateResponseTask(request.getPayload(), currentUser);
                case ALLOCATE_RESOURCE -> handleAllocateResource(request.getPayload(), currentUser);
                case UPDATE_REPORT_STATUS -> handleUpdateReportStatus(request.getPayload(), currentUser);
                case UPDATE_TASK_STATUS -> handleUpdateTaskStatus(request.getPayload(), currentUser);
                case FETCH_TASKS_BY_REPORT -> handleFetchTasksByReport(request.getPayload());
                case RECOMMEND_RESOURCES -> handleRecommendResources(request.getPayload());
                case FETCH_AUDIT_EVENTS -> ServerResponse.success("Audit events fetched", auditRepository.findAll());
                case SEARCH_AUDIT_EVENTS -> handleSearchAuditEvents(request.getPayload());
                default -> ServerResponse.failure("Action not implemented yet: " + request.getAction());
            };
        } catch (SQLException | IllegalArgumentException exception) {
            return ServerResponse.failure(exception.getMessage());
        }
    }

    private ServerResponse handleSubmitReport(Object payload, User currentUser) throws SQLException {
        if (!(payload instanceof DisasterReport report)) {
            return ServerResponse.failure("Invalid submit report payload.");
        }

        if (report.getStatus() == null || report.getStatus().trim().isEmpty()) {
            report.setStatus("Reported");
        }
        report.setPriorityLevel(assessmentService.estimateInitialPriority(report.getDisasterType(), report.getSeverity()));
        report.setEvacuationAdvice(evacuationAdviceService.generateAdvice(report.getDisasterType(), report.getSeverity()));
        report.setRecommendedResources(resourceRecommendationService.recommendResources(report.getDisasterType(), report.getSeverity()));

        DisasterReport savedReport = disasterReportRepository.save(report);
        logAuditEvent(currentUser,
                "Report",
                savedReport.getReportId(),
                savedReport.getReportTitle(),
                "Report Submitted",
                "Created report with status '" + savedReport.getStatus() + "' and priority '" + savedReport.getPriorityLevel() + "'.");
        return ServerResponse.success("Report submitted", savedReport);
    }

    private ServerResponse handleSearchReports(Object payload) throws SQLException {
        if (!(payload instanceof SearchReportsRequest request)) {
            return ServerResponse.failure("Invalid search payload.");
        }
        String keyword = request.getKeyword();
        if (keyword == null || keyword.trim().isEmpty()) {
            return ServerResponse.success("Reports fetched", disasterReportRepository.findAll());
        }
        return ServerResponse.success("Search results", disasterReportRepository.search(keyword.trim()));
    }

    private ServerResponse handleDuplicateCheck(Object payload) throws SQLException {
        if (!(payload instanceof CheckDuplicateRequest request)) {
            return ServerResponse.failure("Invalid duplicate check payload.");
        }
        boolean duplicate = duplicateReportService.isDuplicate(request.getDisasterType(), request.getLocation());
        return ServerResponse.success("Duplicate check complete", duplicate);
    }

    private ServerResponse handleSaveAssessment(Object payload, User currentUser) throws SQLException {
        if (!(payload instanceof AssessmentRequest request)) {
            return ServerResponse.failure("Invalid assessment payload.");
        }

        DisasterReport report = request.getReport();
        AssessmentResult assessment = assessmentService.assessDisaster(report, request.getDamageLevel(),
                request.getPeopleAffected(), request.isInfrastructureDamage());

        assessmentRepository.save(assessment);
        disasterReportRepository.updateStatusAndPriority(report.getReportId(), "Assessed", assessment.getPriorityLevel());
        List<ResponseTask> generatedTasks = departmentCoordinationService.generateStandardTasks(report);

        logAuditEvent(currentUser,
                "Assessment",
                assessment.getAssessmentId(),
                report.getReportTitle(),
                "Assessment Saved",
                "Assessed report " + report.getReportId() + " with priority '" + assessment.getPriorityLevel()
                        + "' and generated " + generatedTasks.size() + " standard tasks.");
        return ServerResponse.success("Assessment saved", new AssessmentResponse(assessment, generatedTasks));
    }

    private ServerResponse handleCreateResponseTask(Object payload, User currentUser) throws SQLException {
        if (!(payload instanceof ResponseTask task)) {
            return ServerResponse.failure("Invalid task payload.");
        }
        ResponseTask savedTask = responseTaskRepository.save(task);
        logAuditEvent(currentUser,
                "Task",
                savedTask.getTaskId(),
                savedTask.getActivityType(),
                "Task Created",
                "Created task for report " + savedTask.getReportId() + " and department " + savedTask.getDepartmentName() + ".");
        return ServerResponse.success("Task created", savedTask);
    }

    private ServerResponse handleAllocateResource(Object payload, User currentUser) throws SQLException {
        if (!(payload instanceof AllocateResourceRequest request)) {
            return ServerResponse.failure("Invalid allocation payload.");
        }
        resourceRepository.allocateResource(request.getReportId(), request.getResource(), request.getQuantity(), request.getNotes());
        logAuditEvent(currentUser,
                "ResourceAllocation",
                request.getReportId(),
                request.getResource().getResourceName(),
                "Resource Allocated",
                "Allocated " + request.getQuantity() + " of " + request.getResource().getResourceName() + " to report " + request.getReportId() + ".");
        return ServerResponse.success("Resource allocated", null);
    }

    private ServerResponse handleUpdateReportStatus(Object payload, User currentUser) throws SQLException {
        if (!(payload instanceof UpdateReportStatusRequest request)) {
            return ServerResponse.failure("Invalid report status payload.");
        }
        DisasterReport existingReport = disasterReportRepository.findById(request.getReportId());
        String previousStatus = existingReport == null ? "Unknown" : existingReport.getStatus();
        disasterReportRepository.updateStatus(request.getReportId(), request.getStatus());
        logAuditEvent(currentUser,
                "Report",
                request.getReportId(),
                existingReport == null ? "Report #" + request.getReportId() : existingReport.getReportTitle(),
                "Report Status Updated",
                "Status changed from '" + previousStatus + "' to '" + request.getStatus() + "'.");
        return ServerResponse.success("Report status updated", null);
    }

    private ServerResponse handleUpdateTaskStatus(Object payload, User currentUser) throws SQLException {
        if (!(payload instanceof UpdateTaskStatusRequest request)) {
            return ServerResponse.failure("Invalid task status payload.");
        }
        ResponseTask existingTask = responseTaskRepository.findById(request.getTaskId());
        String previousStatus = existingTask == null ? "Unknown" : existingTask.getStatus();
        responseTaskRepository.updateStatus(request.getTaskId(), request.getStatus());
        logAuditEvent(currentUser,
                "Task",
                request.getTaskId(),
                existingTask == null ? "Task #" + request.getTaskId() : existingTask.getActivityType(),
                "Task Status Updated",
                "Status changed from '" + previousStatus + "' to '" + request.getStatus() + "'.");
        return ServerResponse.success("Task status updated", null);
    }

    private ServerResponse handleSearchAuditEvents(Object payload) throws SQLException {
        if (!(payload instanceof AuditSearchRequest request)) {
            return ServerResponse.failure("Invalid audit search payload.");
        }
        String keyword = request.getKeyword();
        if (keyword == null || keyword.trim().isEmpty()) {
            return ServerResponse.success("Audit events fetched", auditRepository.findAll());
        }
        return ServerResponse.success("Audit search results", auditRepository.search(keyword.trim()));
    }

    private void logAuditEvent(User currentUser, String entityType, int entityId, String entityLabel,
                               String actionType, String changeDetails) throws SQLException {
        if (currentUser == null) {
            return;
        }
        AuditRecord record = new AuditRecord(entityType, entityId, entityLabel, actionType,
                currentUser.getUsername(), changeDetails);
        auditRepository.save(record);
    }

    private ServerResponse handleFetchTasksByReport(Object payload) throws SQLException {
        if (!(payload instanceof ReportIdRequest request)) {
            return ServerResponse.failure("Invalid report id payload.");
        }
        return ServerResponse.success("Tasks fetched", responseTaskRepository.findByReportId(request.getReportId()));
    }

    private ServerResponse handleRecommendResources(Object payload) {
        if (!(payload instanceof DisasterReport report)) {
            return ServerResponse.failure("Invalid recommendation payload.");
        }
        String recommendation = resourceRecommendationService.recommendResources(report.getDisasterType(), report.getSeverity());
        return ServerResponse.success("Resources recommended", recommendation);
    }
}
