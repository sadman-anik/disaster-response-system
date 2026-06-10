# DRS-Enhanced Part 1 Group Report Draft

> Convert this draft into a `.doc` or `.docx` file for submission. Replace all `TODO` items with your team details, screenshots, diagram images, and final GitHub URL.

## 1. Cover Page

**Project title:** DRS-Enhanced - Disaster Response System  
**Unit:** COIT20258 Software Engineering  
**Assessment:** Assignment 2, Part 1 - Group Report and Software  
**Team name:** TODO  
**GitHub repository:** TODO: paste repository URL  

| Student name | Student ID | Role / contribution |
| --- | --- | --- |
| TODO | TODO | TODO |
| TODO | TODO | TODO |
| TODO | TODO | TODO |

## 2. Requirement Specification

### 2.1 Functional Requirements

| ID | Requirement |
| --- | --- |
| FR1 | The system shall allow users to register and login using role-based accounts. |
| FR2 | The system shall allow authorised users to submit disaster reports with title, type, severity, location, reporter details, contact number, and description. |
| FR3 | The system shall validate report input and reject incomplete report data. |
| FR4 | The system shall detect possible duplicate disaster reports using disaster type and location. |
| FR5 | The system shall generate initial priority, evacuation advice, and recommended resources for a submitted report. |
| FR6 | The system shall allow assessment officers to assess disaster damage, people affected, and infrastructure damage. |
| FR7 | The system shall calculate priority score and priority level from assessment data. |
| FR8 | The system shall automatically create standard response tasks after an assessment is saved. |
| FR9 | The system shall allow department officers to update assigned response task status. |
| FR10 | The system shall allow resource officers to recommend and allocate emergency resources. |
| FR11 | The system shall update resource availability after resource allocation. |
| FR12 | The system shall allow authorised users to search and view disaster reports. |
| FR13 | The system shall allow administrators to update report workflow status. |
| FR14 | The system shall provide dashboard charts for reports, tasks, and available resources. |
| FR15 | The system shall maintain audit records for important actions such as report submission, assessment, resource allocation, and task updates. |

### 2.2 Non-Functional Requirements

| ID | Requirement |
| --- | --- |
| NFR1 | The system shall use JavaFX to provide an intuitive graphical user interface. |
| NFR2 | The system shall use MVC-style separation between UI controllers, domain models, services, repositories, and protocol classes. |
| NFR3 | The system shall persist data in a MySQL database. |
| NFR4 | The system shall use role-based access control to restrict functions by user type. |
| NFR5 | Client/server request and response objects shall be encrypted using AES-based sealed objects. |
| NFR6 | The project shall compile with JDK 17 and Maven. |
| NFR7 | The application shall run in NetBeans using the `com.sadman.drs.DRSLauncher` main class. |

### 2.3 System Requirements

| Component | Requirement |
| --- | --- |
| Operating system | Windows, macOS, or Linux with JDK 17 support |
| IDE | NetBeans or another Java IDE with Maven support |
| Java | JDK 17 |
| Build tool | Maven |
| GUI framework | JavaFX |
| Database | MySQL Server |
| Database configuration | `src/main/resources/database.properties` |
| Run entry point | `com.sadman.drs.DRSLauncher` |

### 2.4 User Requirements

| User role | Main needs |
| --- | --- |
| Admin | View dashboard, search reports, update report status, and access all modules. |
| Reporter | Submit disaster reports, check duplicates, and view report status. |
| Assessment Officer | Assess disaster reports, generate priority score, and create response tasks. |
| Resource Officer | View resources, recommend resources, and allocate resources to reports. |
| Department Officer | View assigned response tasks and update task progress. |
| Auditor | View audit logs and system activity records. |

**Screenshots to insert:**

- Login page with server connection indicator.
- Registration page with password requirement validation.
- Dashboard page.
- Report submission page.
- Assessment page.
- Resource allocation page.
- Audit log page.

### 2.5 Additional Domain-Specific Features

| Feature | Description |
| --- | --- |
| Duplicate Disaster Report Detection | The system checks whether an active disaster report already exists with similar disaster type and location. This helps prevent repeated reports for the same incident. |
| Emergency Resource Recommendation | The system recommends emergency resources based on disaster type and severity, such as fire trucks for fire incidents or rescue boats for floods. |
| Evacuation Advice Generator | The system generates context-specific evacuation and safety advice based on disaster type and severity. |

## 3. Design Specifications

### 3.1 System Architecture

Diagram image for report: `docs/diagrams/system-architecture.png`

The application uses a layered MVC-style architecture:

| Layer | Main packages/classes | Responsibility |
| --- | --- | --- |
| Presentation layer | `com.sadman.drs.controller`, `src/main/resources/*.fxml`, `style.css` | JavaFX screens, user input, UI navigation, and UI feedback. |
| Client communication layer | `com.sadman.drs.client.bridge` | Sends encrypted requests from JavaFX client to the server. |
| Protocol layer | `com.sadman.drs.protocol` | Defines serializable request, response, and command objects. |
| Server layer | `com.sadman.drs.server`, `DRSServerRequestProcessor`, `ClientHandler` | Receives requests, applies authorization, and routes actions to services/repositories. |
| Service layer | `com.sadman.drs.server.service` | Business rules such as assessment, duplicate checking, evacuation advice, resource recommendation, authorization, and audit logic. |
| Repository layer | `com.sadman.drs.server.repository` | Database CRUD operations. |
| Persistence layer | MySQL, `database-schema.sql` | Stores reports, assessments, tasks, departments, resources, allocations, users, and audit events. |

### 3.2 Use Case Diagram

Diagram image for report: `docs/diagrams/use-case-diagram.png`

Insert the PNG image into the final Word report. The diagram covers the six application roles: Admin, Reporter, Assessment Officer, Resource Officer, Department Officer, and Auditor. It also shows the key DRS-Enhanced use cases: login/registration, report submission, duplicate checking, disaster assessment, evacuation advice generation, response task creation, task status updates, resource recommendation, resource allocation, report status updates, dashboard viewing, and audit log viewing.

### 3.3 Class Diagram

Diagram image for report: `docs/diagrams/class-diagram.png`

Insert the PNG image into the final Word report. The class diagram is intentionally high level so it clearly communicates the MVC/client-server structure without becoming unreadable. It includes launcher, JavaFX controllers, client bridge classes, protocol objects, server request handling, business services, repositories, and model classes.

### 3.4 Sequence Diagrams

Diagram images for report:

1. `docs/diagrams/sequence-login.png`
2. `docs/diagrams/sequence-submit-report.png`
3. `docs/diagrams/sequence-assessment-auto-task.png`
4. `docs/diagrams/sequence-resource-allocation.png`

Insert these PNG images into the final Word report. Together they show the main client-server flows: login, disaster report submission with duplicate checking, disaster assessment with automatic response task creation, and resource recommendation/allocation.

### 3.5 ERD

Diagram image for report: `docs/diagrams/erd.png`

Insert the PNG image into the final Word report. The ERD is based on `database-schema.sql` and includes the main database entities, primary keys, foreign keys, and relationships.

Database entities from `database-schema.sql`:

- `users`
- `disaster_reports`
- `disaster_assessments`
- `departments`
- `response_tasks`
- `resources`
- `resource_allocations`
- `audit_events`

Important relationships:

- One disaster report can have many assessments.
- One disaster report can have many response tasks.
- One department can be assigned many response tasks.
- One disaster report can have many resource allocations.
- One resource can appear in many resource allocations.
- Audit events record important changes made by users.

## 4. Test Plan

| Test ID | Feature | Input data | Expected result | Actual result |
| --- | --- | --- | --- | --- |
| T1 | Login validation | Empty username/password | Red error message shown | Pass |
| T2 | User registration password validation | Password without uppercase/special/minimum length | Requirement labels remain red and Register disabled | Pass |
| T3 | Valid user registration | Valid username, password `Test@123`, role Reporter | Success popup shown and user moved to login page | Pass |
| T4 | Submit disaster report validation | Missing location | Validation rejects report and shows location error | Pass, covered by `ServiceLogicTest.validationShouldRejectMissingLocation` |
| T5 | Submit complete disaster report | Fire, High, Parramatta, valid description/reporter/contact | Report saved and shown in report table | Pass |
| T6 | Duplicate detection | Existing Fire report in Parramatta, new Fire report in Parramatta | Duplicate warning shown and submit disabled | Pass, covered by `CreativeFeatureServiceTest.duplicateDetectionShouldMatchSameTypeAndLocationIgnoringCase` |
| T7 | Evacuation advice | Fire, High | Advice includes evacuation/fire/smoke safety text | Pass, covered by `ServiceLogicTest.fireHighShouldGenerateEvacuationAdvice` |
| T8 | Resource recommendation | Flood, Medium | Rescue Boat and Medical Team recommended | Pass, covered by `CreativeFeatureServiceTest.floodMediumShouldRecommendRescueBoat` |
| T9 | Disaster assessment | Severe damage, 100 people affected, infrastructure damage true | Priority score generated and summary created | Pass, covered by `ServiceLogicTest.severeDamageWithManyPeopleShouldGenerateAssessmentResult` |
| T10 | Authorization | Reporter tries resource allocation | Access denied | Pass, covered by `AuthorizationServiceTest.reporterCanSubmitReportsButCannotAllocateResources` |
| T11 | Secure transport utility | Plain message encrypted/decrypted | Encrypted text differs from original and decrypts correctly | Pass, covered by `SecurityUtilityTest` |
| T12 | Dashboard chart data | Seeded reports/resources/tasks | Dashboard displays totals and graphs | Pass |

## 5. Evidence of Automated Testing

Insert screenshots of automated tests here.

Recommended screenshots:

1. NetBeans or terminal running:

```bash
mvn test
```

2. Test result showing:

```text
Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

3. Screenshots of these test files:

- `src/test/java/com/sadman/drs/service/ServiceLogicTest.java`
- `src/test/java/com/sadman/drs/service/CreativeFeatureServiceTest.java`
- `src/test/java/com/sadman/drs/server/service/AuthorizationServiceTest.java`
- `src/test/java/com/sadman/drs/security/SecurityUtilityTest.java`

## 6. Software Prototype Submission Checklist

| Requirement | Evidence / location |
| --- | --- |
| Complete Java source code | `src/main/java` |
| JavaFX FXML and CSS resources | `src/main/resources/login.fxml`, `main.fxml`, `style.css` |
| Maven build file and libraries | `pom.xml` |
| MySQL database script | `database-schema.sql` creates tables and populates default users, departments, and resources. |
| Database connection settings | `src/main/resources/database.properties` |
| NetBeans run support | `nbactions.xml`, `com.sadman.drs.DRSLauncher` |
| Single launcher for server and client | `src/main/java/com/sadman/drs/DRSLauncher.java` |
| JUnit automated tests | `src/test/java` |
| GitHub repository | TODO: paste repository URL |

**Database setup note:** The application automatically creates and seeds the MySQL database at startup through `DatabaseInitializer` and `DatabaseSeeder`. The separate `database-schema.sql` script is included for the assignment requirement and for manual marking/setup. It creates the same schema and populates default users, departments, and resources. Using both methods is safe because existing default data is skipped.

## 7. How to Run in NetBeans

1. Open NetBeans.
2. Select **File > Open Project**.
3. Choose the project folder containing `pom.xml`.
4. Ensure MySQL Server is running.
5. Confirm `src/main/resources/database.properties` contains the correct MySQL username and password.
6. Run the project using the main class:

```text
com.sadman.drs.DRSLauncher
```

This launcher starts both:

- `DRSServer` on `localhost:9090`
- JavaFX client login screen

Alternative Maven command:

```bash
mvn clean javafx:run
```

## 8. GitHub and Version Control Evidence

Insert GitHub repository URL here:

```text
TODO: https://github.com/...
```

Include screenshots/evidence of:

- Commit history.
- Team member contributions.
- Branch or task distribution if applicable.
- Final pushed repository state.
