package com.sadman.drs.client.bridge;

import com.sadman.drs.model.AuditRecord;
import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.Department;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Resource;
import com.sadman.drs.model.ResourceAllocation;
import com.sadman.drs.model.ResponseTask;
import com.sadman.drs.model.User;
import com.sadman.drs.protocol.AllocateResourceRequest;
import com.sadman.drs.protocol.AssessmentRequest;
import com.sadman.drs.protocol.AssessmentResponse;
import com.sadman.drs.protocol.AuditSearchRequest;
import com.sadman.drs.protocol.AuthenticationRequest;
import com.sadman.drs.protocol.CheckDuplicateRequest;
import com.sadman.drs.protocol.ReportIdRequest;
import com.sadman.drs.protocol.SearchReportsRequest;
import com.sadman.drs.protocol.ServerAction;
import com.sadman.drs.protocol.ServerRequest;
import com.sadman.drs.protocol.ServerResponse;
import com.sadman.drs.protocol.UpdateReportStatusRequest;
import com.sadman.drs.protocol.UpdateTaskStatusRequest;
import com.sadman.drs.protocol.UserRegistrationRequest;

import java.io.IOException;
import java.util.List;

/**
 * Client-facing wrapper for sending protocol requests to the DRS server.
 */
public class DRSClientService implements AutoCloseable {

    private final DRSClient client = new DRSClient();
    private boolean connected = false;

    public void connect() throws IOException {
        client.connect();
        connected = true;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean pingServer() throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.PING, null));
        return response.isSuccess();
    }

    public User authenticate(String username, String password) throws IOException, ClassNotFoundException {
        return sendNullable(ServerAction.AUTHENTICATE, new AuthenticationRequest(username, password), User.class);
    }

    public User registerUser(String username, String password, String role) throws IOException, ClassNotFoundException {
        return sendNullable(ServerAction.REGISTER_USER, new UserRegistrationRequest(username, password, role), User.class);
    }

    public DisasterReport submitReport(DisasterReport report) throws IOException, ClassNotFoundException {
        return sendPayload(ServerAction.SUBMIT_REPORT, report, DisasterReport.class);
    }

    @SuppressWarnings("unchecked")
    public List<DisasterReport> findAllReports() throws IOException, ClassNotFoundException {
        return sendList(ServerAction.FETCH_REPORTS, null);
    }

    @SuppressWarnings("unchecked")
    public List<DisasterReport> searchReports(String keyword) throws IOException, ClassNotFoundException {
        return sendList(ServerAction.SEARCH_REPORTS, new SearchReportsRequest(keyword));
    }

    public boolean checkDuplicate(String disasterType, String location) throws IOException, ClassNotFoundException {
        ServerResponse response = send(ServerAction.CHECK_DUPLICATE, new CheckDuplicateRequest(disasterType, location));
        return Boolean.TRUE.equals(response.getPayload());
    }

    public AssessmentResponse saveAssessment(AssessmentRequest request) throws IOException, ClassNotFoundException {
        return sendPayload(ServerAction.SAVE_ASSESSMENT, request, AssessmentResponse.class);
    }

    @SuppressWarnings("unchecked")
    public List<AssessmentResult> findAllAssessments() throws IOException, ClassNotFoundException {
        return sendList(ServerAction.FETCH_ASSESSMENTS, null);
    }

    @SuppressWarnings("unchecked")
    public List<Department> findAllDepartments() throws IOException, ClassNotFoundException {
        return sendList(ServerAction.FETCH_DEPARTMENTS, null);
    }

    @SuppressWarnings("unchecked")
    public List<ResponseTask> findAllTasks() throws IOException, ClassNotFoundException {
        return sendList(ServerAction.FETCH_TASKS, null);
    }

    @SuppressWarnings("unchecked")
    public List<Resource> findAllResources() throws IOException, ClassNotFoundException {
        return sendList(ServerAction.FETCH_RESOURCES, null);
    }

    @SuppressWarnings("unchecked")
    public List<ResourceAllocation> findAllAllocations() throws IOException, ClassNotFoundException {
        return sendList(ServerAction.FETCH_ALLOCATIONS, null);
    }

    public ResponseTask createResponseTask(ResponseTask task) throws IOException, ClassNotFoundException {
        return sendPayload(ServerAction.CREATE_RESPONSE_TASK, task, ResponseTask.class);
    }

    public void allocateResource(int reportId, int taskId, Resource resource, int quantity, String notes)
            throws IOException, ClassNotFoundException {
        send(ServerAction.ALLOCATE_RESOURCE, new AllocateResourceRequest(reportId, taskId, resource, quantity, notes));
    }

    public void updateReportStatus(int reportId, String status) throws IOException, ClassNotFoundException {
        send(ServerAction.UPDATE_REPORT_STATUS, new UpdateReportStatusRequest(reportId, status));
    }

    public void updateTaskStatus(int taskId, String status) throws IOException, ClassNotFoundException {
        send(ServerAction.UPDATE_TASK_STATUS, new UpdateTaskStatusRequest(taskId, status));
    }

    @SuppressWarnings("unchecked")
    public List<ResponseTask> findTasksByReportId(int reportId) throws IOException, ClassNotFoundException {
        return sendList(ServerAction.FETCH_TASKS_BY_REPORT, new ReportIdRequest(reportId));
    }

    public String recommendResources(DisasterReport report) throws IOException, ClassNotFoundException {
        return sendPayload(ServerAction.RECOMMEND_RESOURCES, report, String.class);
    }

    @SuppressWarnings("unchecked")
    public List<AuditRecord> findAllAuditEvents() throws IOException, ClassNotFoundException {
        return sendList(ServerAction.FETCH_AUDIT_EVENTS, null);
    }

    @SuppressWarnings("unchecked")
    public List<AuditRecord> searchAuditEvents(String keyword) throws IOException, ClassNotFoundException {
        return sendList(ServerAction.SEARCH_AUDIT_EVENTS, new AuditSearchRequest(keyword));
    }

    private ServerResponse send(ServerAction action, Object payload) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(action, payload));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return response;
    }

    private <T> T sendPayload(ServerAction action, Object payload, Class<T> payloadType)
            throws IOException, ClassNotFoundException {
        return payloadType.cast(send(action, payload).getPayload());
    }

    private <T> T sendNullable(ServerAction action, Object payload, Class<T> payloadType)
            throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(action, payload));
        if (!response.isSuccess()) {
            return null;
        }
        return payloadType.cast(response.getPayload());
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> sendList(ServerAction action, Object payload) throws IOException, ClassNotFoundException {
        return (List<T>) send(action, payload).getPayload();
    }

    private void ensureConnected() {
        if (!connected) {
            throw new IllegalStateException("Not connected to the DRS server.");
        }
    }

    @Override
    public void close() {
        connected = false;
        client.close();
    }
}
