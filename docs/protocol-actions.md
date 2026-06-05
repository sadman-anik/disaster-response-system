# DRS Client-Server Protocol Actions

The DRS client and server communicate through a simple serializable command protocol:

```text
Client -> ServerRequest(ServerAction, payload)
Server -> ServerResponse(success, message, payload)
```

The protocol package is the contract between JavaFX client code and the multi-threaded server. The client does not call server repositories directly.

| ServerAction | Request Payload | Response Payload | Main Allowed Roles |
|---|---|---|---|
| `PING` | `null` | `null` | Authenticated users |
| `AUTHENTICATE` | `AuthenticationRequest` | `User` | Public |
| `REGISTER_USER` | `UserRegistrationRequest` | `User` | Public registration for non-admin roles |
| `SUBMIT_REPORT` | `DisasterReport` | `DisasterReport` | `ADMIN`, `REPORTER` |
| `FETCH_REPORTS` | `null` | `List<DisasterReport>` | All authenticated roles |
| `SEARCH_REPORTS` | `SearchReportsRequest` | `List<DisasterReport>` | All authenticated roles |
| `CHECK_DUPLICATE` | `CheckDuplicateRequest` | `Boolean` | `ADMIN`, `REPORTER`, `ASSESSMENT_OFFICER` |
| `SAVE_ASSESSMENT` | `AssessmentRequest` | `AssessmentResponse` | `ADMIN`, `ASSESSMENT_OFFICER` |
| `FETCH_ASSESSMENTS` | `null` | `List<AssessmentResult>` | `ADMIN`, `ASSESSMENT_OFFICER`, `RESOURCE_OFFICER`, `AUDITOR` |
| `FETCH_DEPARTMENTS` | `null` | `List<Department>` | `ADMIN`, `ASSESSMENT_OFFICER`, `DEPARTMENT_OFFICER`, `AUDITOR` |
| `FETCH_TASKS` | `null` | `List<ResponseTask>` | All authenticated roles |
| `FETCH_RESOURCES` | `null` | `List<Resource>` | All authenticated roles |
| `FETCH_ALLOCATIONS` | `null` | `List<ResourceAllocation>` | All authenticated roles |
| `CREATE_RESPONSE_TASK` | `ResponseTask` | `ResponseTask` | `ADMIN`, `ASSESSMENT_OFFICER` |
| `ALLOCATE_RESOURCE` | `AllocateResourceRequest` | `null` | `ADMIN`, `RESOURCE_OFFICER` |
| `UPDATE_REPORT_STATUS` | `UpdateReportStatusRequest` | `null` | `ADMIN` |
| `UPDATE_TASK_STATUS` | `UpdateTaskStatusRequest` | `null` | `ADMIN`, `ASSESSMENT_OFFICER`, `DEPARTMENT_OFFICER` |
| `FETCH_TASKS_BY_REPORT` | `ReportIdRequest` | `List<ResponseTask>` | All authenticated roles |
| `RECOMMEND_RESOURCES` | `DisasterReport` | `String` | `ADMIN`, `ASSESSMENT_OFFICER`, `RESOURCE_OFFICER`, `AUDITOR` |
| `FETCH_AUDIT_EVENTS` | `null` | `List<AuditRecord>` | `ADMIN`, `AUDITOR` |
| `SEARCH_AUDIT_EVENTS` | `AuditSearchRequest` | `List<AuditRecord>` | `ADMIN`, `AUDITOR` |

Authorization is enforced on the server by `AuthorizationService`; client-side role-specific views only improve usability and are not treated as the security boundary.
