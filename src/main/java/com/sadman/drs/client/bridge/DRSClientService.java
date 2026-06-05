package com.sadman.drs.client.bridge;

import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.Department;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Resource;
import com.sadman.drs.model.ResourceAllocation;
import com.sadman.drs.model.ResponseTask;
import com.sadman.drs.model.User;
import com.sadman.drs.model.User;
import com.sadman.drs.protocol.AllocateResourceRequest;
import com.sadman.drs.protocol.AssessmentRequest;
import com.sadman.drs.protocol.AssessmentResponse;
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

    public boolean pingServer() throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.PING, null));
        return response.isSuccess();
    }

    public User authenticate(String username, String password) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.AUTHENTICATE,
                new com.sadman.drs.protocol.AuthenticationRequest(username, password)));
        if (!response.isSuccess()) {
            return null;
        }
        return (User) response.getPayload();
    }

    public User registerUser(String username, String password, String role) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.REGISTER_USER,
                new com.sadman.drs.protocol.UserRegistrationRequest(username, password, role)));
        if (!response.isSuccess()) {
            return null;
        }
        return (User) response.getPayload();
    }

    public DisasterReport submitReport(DisasterReport report) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.SUBMIT_REPORT, report));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (DisasterReport) response.getPayload();
    }

    @SuppressWarnings("unchecked")
    public List<DisasterReport> findAllReports() throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.FETCH_REPORTS, null));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (List<DisasterReport>) response.getPayload();
    }

    @SuppressWarnings("unchecked")
    public List<DisasterReport> searchReports(String keyword) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.SEARCH_REPORTS, new SearchReportsRequest(keyword)));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (List<DisasterReport>) response.getPayload();
    }

    public boolean checkDuplicate(String disasterType, String location) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.CHECK_DUPLICATE, new CheckDuplicateRequest(disasterType, location)));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return Boolean.TRUE.equals(response.getPayload());
    }

    public AssessmentResponse saveAssessment(AssessmentRequest request) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.SAVE_ASSESSMENT, request));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (AssessmentResponse) response.getPayload();
    }

    @SuppressWarnings("unchecked")
    public List<AssessmentResult> findAllAssessments() throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.FETCH_ASSESSMENTS, null));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (List<AssessmentResult>) response.getPayload();
    }

    @SuppressWarnings("unchecked")
    public List<Department> findAllDepartments() throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.FETCH_DEPARTMENTS, null));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (List<Department>) response.getPayload();
    }

    @SuppressWarnings("unchecked")
    public List<ResponseTask> findAllTasks() throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.FETCH_TASKS, null));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (List<ResponseTask>) response.getPayload();
    }

    @SuppressWarnings("unchecked")
    public List<Resource> findAllResources() throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.FETCH_RESOURCES, null));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (List<Resource>) response.getPayload();
    }

    @SuppressWarnings("unchecked")
    public List<ResourceAllocation> findAllAllocations() throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.FETCH_ALLOCATIONS, null));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (List<ResourceAllocation>) response.getPayload();
    }

    public ResponseTask createResponseTask(ResponseTask task) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.CREATE_RESPONSE_TASK, task));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (ResponseTask) response.getPayload();
    }

    public void allocateResource(int reportId, Resource resource, int quantity, String notes) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.ALLOCATE_RESOURCE,
                new AllocateResourceRequest(reportId, resource, quantity, notes)));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
    }

    public void updateReportStatus(int reportId, String status) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.UPDATE_REPORT_STATUS,
                new UpdateReportStatusRequest(reportId, status)));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
    }

    public void updateTaskStatus(int taskId, String status) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.UPDATE_TASK_STATUS,
                new UpdateTaskStatusRequest(taskId, status)));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<ResponseTask> findTasksByReportId(int reportId) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.FETCH_TASKS_BY_REPORT,
                new ReportIdRequest(reportId)));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (List<ResponseTask>) response.getPayload();
    }

    public String recommendResources(DisasterReport report) throws IOException, ClassNotFoundException {
        ensureConnected();
        ServerResponse response = client.sendRequest(new ServerRequest(ServerAction.RECOMMEND_RESOURCES, report));
        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }
        return (String) response.getPayload();
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
