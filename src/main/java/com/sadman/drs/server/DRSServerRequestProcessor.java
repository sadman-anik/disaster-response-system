package com.sadman.drs.server;

import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.Department;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Resource;
import com.sadman.drs.model.ResourceAllocation;
import com.sadman.drs.model.ResponseTask;
import com.sadman.drs.model.StatusValues;
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
import com.sadman.drs.server.service.AuditService;
import com.sadman.drs.server.service.AuthorizationService;
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

    private final AuthorizationService authorizationService = new AuthorizationService();
    private final AuditService auditService = new AuditService(auditRepository);
    private final DisasterAssessmentService assessmentService = new DisasterAssessmentService();
    private final EvacuationAdviceService evacuationAdviceService = new EvacuationAdviceService();
    private final ResourceRecommendationService resourceRecommendationService = new ResourceRecommendationService();
    private final DuplicateReportService duplicateReportService = new DuplicateReportService(disasterReportRepository);
    private final DepartmentCoordinationService departmentCoordinationService =
            new DepartmentCoordinationService(departmentRepository, responseTaskRepository);

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
            if (authorizationService.requiresAuthentication(request.getAction())
                    && !authorizationService.isAuthenticated(currentUser)) {
                return ServerResponse.failure("Authentication required. Please login first.");
            }

            if (!authorizationService.isAuthorized(currentUser, request.getAction())) {
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
            report.setStatus(StatusValues.REPORTED);
        }
        report.setPriorityLevel(assessmentService.estimateInitialPriority(report.getDisasterType(), report.getSeverity()));
        report.setEvacuationAdvice(evacuationAdviceService.generateAdvice(report.getDisasterType(), report.getSeverity()));
        report.setRecommendedResources(resourceRecommendationService.recommendResources(report.getDisasterType(), report.getSeverity()));

        DisasterReport savedReport = disasterReportRepository.save(report);
        auditService.logReportSubmitted(currentUser, savedReport);
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
        if (report == null) {
            return ServerResponse.failure("Report not found.");
        }
        DisasterReport existingReport = disasterReportRepository.findById(report.getReportId());
        if (existingReport != null && StatusValues.isTerminalReportStatus(existingReport.getStatus())) {
            return ServerResponse.failure("Completed reports cannot be assessed again.");
        }
        AssessmentResult assessment = assessmentService.assessDisaster(report, request.getDamageLevel(),
                request.getPeopleAffected(), request.isInfrastructureDamage());

        assessmentRepository.save(assessment);
        disasterReportRepository.updateStatusAndPriority(report.getReportId(), StatusValues.ASSESSED, assessment.getPriorityLevel());
        List<ResponseTask> generatedTasks = departmentCoordinationService.generateStandardTasks(report);

        auditService.logAssessmentSaved(currentUser, assessment, report, generatedTasks.size());
        return ServerResponse.success("Assessment saved", new AssessmentResponse(assessment, generatedTasks));
    }

    private ServerResponse handleCreateResponseTask(Object payload, User currentUser) throws SQLException {
        if (!(payload instanceof ResponseTask task)) {
            return ServerResponse.failure("Invalid task payload.");
        }
        DisasterReport report = disasterReportRepository.findById(task.getReportId());
        if (report != null && StatusValues.isTerminalReportStatus(report.getStatus())) {
            return ServerResponse.failure("Cannot create tasks for a completed report.");
        }
        ResponseTask savedTask = responseTaskRepository.save(task);
        auditService.logTaskCreated(currentUser, savedTask);
        return ServerResponse.success("Task created", savedTask);
    }

    private ServerResponse handleAllocateResource(Object payload, User currentUser) throws SQLException {
        if (!(payload instanceof AllocateResourceRequest request)) {
            return ServerResponse.failure("Invalid allocation payload.");
        }
        DisasterReport report = disasterReportRepository.findById(request.getReportId());
        if (report == null) {
            return ServerResponse.failure("Report not found.");
        }
        if (StatusValues.isTerminalReportStatus(report.getStatus())) {
            return ServerResponse.failure("Cannot allocate resources to a completed report.");
        }
        resourceRepository.allocateResource(request.getReportId(), request.getResource(), request.getQuantity(), request.getNotes());
        auditService.logResourceAllocated(currentUser, request.getReportId(), request.getResource(), request.getQuantity());
        return ServerResponse.success("Resource allocated", null);
    }

    private ServerResponse handleUpdateReportStatus(Object payload, User currentUser) throws SQLException {
        if (!(payload instanceof UpdateReportStatusRequest request)) {
            return ServerResponse.failure("Invalid report status payload.");
        }
        DisasterReport existingReport = disasterReportRepository.findById(request.getReportId());
        if (existingReport == null) {
            return ServerResponse.failure("Report not found.");
        }
        String previousStatus = existingReport.getStatus();
        if (StatusValues.isTerminalReportStatus(previousStatus)) {
            return ServerResponse.failure("Completed reports cannot be updated.");
        }

        String requestedStatus = normalizeReportStatus(request.getStatus());
        if (StatusValues.isTerminalReportStatus(requestedStatus)) {
            List<String> incompleteStatuses = responseTaskRepository.findIncompleteStatusesByReportId(request.getReportId());
            if (!incompleteStatuses.isEmpty()) {
                return ServerResponse.failure("Report cannot be completed because related tasks are still in "
                        + String.join(", ", incompleteStatuses) + ".");
            }
            resourceRepository.releaseAllocationsForReport(request.getReportId());
        }

        disasterReportRepository.updateStatus(request.getReportId(), requestedStatus);
        auditService.logReportStatusUpdated(currentUser,
                request.getReportId(),
                existingReport.getReportTitle(),
                previousStatus,
                requestedStatus);
        return ServerResponse.success("Report status updated", null);
    }

    private ServerResponse handleUpdateTaskStatus(Object payload, User currentUser) throws SQLException {
        if (!(payload instanceof UpdateTaskStatusRequest request)) {
            return ServerResponse.failure("Invalid task status payload.");
        }
        ResponseTask existingTask = responseTaskRepository.findById(request.getTaskId());
        if (existingTask == null) {
            return ServerResponse.failure("Task not found.");
        }
        DisasterReport report = disasterReportRepository.findById(existingTask.getReportId());
        if (report != null && StatusValues.isTerminalReportStatus(report.getStatus())) {
            return ServerResponse.failure("Cannot update tasks for a completed report.");
        }
        String previousStatus = existingTask.getStatus();
        responseTaskRepository.updateStatus(request.getTaskId(), request.getStatus());
        auditService.logTaskStatusUpdated(currentUser,
                request.getTaskId(),
                existingTask.getActivityType(),
                previousStatus,
                request.getStatus());
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

    private ServerResponse handleFetchTasksByReport(Object payload) throws SQLException {
        if (!(payload instanceof ReportIdRequest request)) {
            return ServerResponse.failure("Invalid report id payload.");
        }
        return ServerResponse.success("Tasks fetched", responseTaskRepository.findByReportId(request.getReportId()));
    }

    private ServerResponse handleRecommendResources(Object payload) throws SQLException {
        if (!(payload instanceof DisasterReport report)) {
            return ServerResponse.failure("Invalid recommendation payload.");
        }
        DisasterReport existingReport = disasterReportRepository.findById(report.getReportId());
        if (existingReport != null && StatusValues.isTerminalReportStatus(existingReport.getStatus())) {
            return ServerResponse.failure("Cannot recommend resources for a completed report.");
        }
        String recommendation = resourceRecommendationService.recommendResources(report.getDisasterType(), report.getSeverity());
        return ServerResponse.success("Resources recommended", recommendation);
    }

    private String normalizeReportStatus(String status) {
        if (StatusValues.CLOSED.equalsIgnoreCase(status)) {
            return StatusValues.COMPLETED;
        }
        return status == null ? "" : status.trim();
    }
}
