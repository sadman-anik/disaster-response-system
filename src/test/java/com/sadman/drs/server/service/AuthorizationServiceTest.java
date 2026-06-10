package com.sadman.drs.server.service;

import com.sadman.drs.model.User;
import com.sadman.drs.model.UserRole;
import com.sadman.drs.protocol.ServerAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorizationServiceTest {
    private final AuthorizationService authorizationService = new AuthorizationService();

    @Test
    void publicActionsDoNotRequireAuthenticatedUser() {
        assertFalse(authorizationService.requiresAuthentication(ServerAction.PING));
        assertFalse(authorizationService.requiresAuthentication(ServerAction.AUTHENTICATE));
        assertFalse(authorizationService.requiresAuthentication(ServerAction.REGISTER_USER));
        assertTrue(authorizationService.isAuthorized(null, ServerAction.PING));
        assertTrue(authorizationService.isAuthorized(null, ServerAction.AUTHENTICATE));
    }

    @Test
    void reporterCanSubmitReportsButCannotAllocateResources() {
        User reporter = userWithRole(UserRole.REPORTER);

        assertTrue(authorizationService.isAuthorized(reporter, ServerAction.SUBMIT_REPORT));
        assertTrue(authorizationService.isAuthorized(reporter, ServerAction.CHECK_DUPLICATE));
        assertFalse(authorizationService.isAuthorized(reporter, ServerAction.ALLOCATE_RESOURCE));
        assertFalse(authorizationService.isAuthorized(reporter, ServerAction.FETCH_AUDIT_EVENTS));
    }

    @Test
    void assessmentOfficerCanAssessAndCreateTasks() {
        User assessmentOfficer = userWithRole(UserRole.ASSESSMENT_OFFICER);

        assertTrue(authorizationService.isAuthorized(assessmentOfficer, ServerAction.SAVE_ASSESSMENT));
        assertTrue(authorizationService.isAuthorized(assessmentOfficer, ServerAction.CREATE_RESPONSE_TASK));
        assertTrue(authorizationService.isAuthorized(assessmentOfficer, ServerAction.UPDATE_TASK_STATUS));
        assertFalse(authorizationService.isAuthorized(assessmentOfficer, ServerAction.ALLOCATE_RESOURCE));
    }

    @Test
    void resourceOfficerCanAllocateResourcesButCannotUpdateReportStatus() {
        User resourceOfficer = userWithRole(UserRole.RESOURCE_OFFICER);

        assertTrue(authorizationService.isAuthorized(resourceOfficer, ServerAction.ALLOCATE_RESOURCE));
        assertTrue(authorizationService.isAuthorized(resourceOfficer, ServerAction.RECOMMEND_RESOURCES));
        assertFalse(authorizationService.isAuthorized(resourceOfficer, ServerAction.UPDATE_REPORT_STATUS));
    }

    @Test
    void auditorCanReadAuditEventsButCannotModifyData() {
        User auditor = userWithRole(UserRole.AUDITOR);

        assertTrue(authorizationService.isAuthorized(auditor, ServerAction.FETCH_AUDIT_EVENTS));
        assertTrue(authorizationService.isAuthorized(auditor, ServerAction.SEARCH_AUDIT_EVENTS));
        assertFalse(authorizationService.isAuthorized(auditor, ServerAction.SUBMIT_REPORT));
        assertFalse(authorizationService.isAuthorized(auditor, ServerAction.UPDATE_TASK_STATUS));
    }

    @Test
    void adminCanPerformEveryProtocolAction() {
        User admin = userWithRole(UserRole.ADMIN);

        for (ServerAction action : ServerAction.values()) {
            assertTrue(authorizationService.isAuthorized(admin, action), "Admin should be authorized for " + action);
        }
    }

    private User userWithRole(UserRole role) {
        return new User(1, role.name().toLowerCase(), role.name(), "hash");
    }
}
