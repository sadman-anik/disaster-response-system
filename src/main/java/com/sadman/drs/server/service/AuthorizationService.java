package com.sadman.drs.server.service;

import com.sadman.drs.model.User;
import com.sadman.drs.model.UserRole;
import com.sadman.drs.protocol.ServerAction;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Centralizes role-based access rules for server request processing.
 */
public class AuthorizationService {
    private static final Set<ServerAction> PUBLIC_ACTIONS = EnumSet.of(
            ServerAction.AUTHENTICATE,
            ServerAction.REGISTER_USER
    );

    private static final Map<UserRole, Set<ServerAction>> ROLE_PERMISSIONS = new EnumMap<>(UserRole.class);

    static {
        ROLE_PERMISSIONS.put(UserRole.ADMIN, EnumSet.allOf(ServerAction.class));

        ROLE_PERMISSIONS.put(UserRole.REPORTER, EnumSet.of(
                ServerAction.PING,
                ServerAction.AUTHENTICATE,
                ServerAction.FETCH_REPORTS,
                ServerAction.SEARCH_REPORTS,
                ServerAction.CHECK_DUPLICATE,
                ServerAction.SUBMIT_REPORT,
                ServerAction.FETCH_TASKS,
                ServerAction.FETCH_RESOURCES,
                ServerAction.FETCH_ALLOCATIONS
        ));

        ROLE_PERMISSIONS.put(UserRole.ASSESSMENT_OFFICER, EnumSet.of(
                ServerAction.PING,
                ServerAction.AUTHENTICATE,
                ServerAction.FETCH_REPORTS,
                ServerAction.SEARCH_REPORTS,
                ServerAction.CHECK_DUPLICATE,
                ServerAction.SAVE_ASSESSMENT,
                ServerAction.FETCH_ASSESSMENTS,
                ServerAction.FETCH_DEPARTMENTS,
                ServerAction.FETCH_TASKS,
                ServerAction.FETCH_RESOURCES,
                ServerAction.FETCH_ALLOCATIONS,
                ServerAction.CREATE_RESPONSE_TASK,
                ServerAction.UPDATE_TASK_STATUS,
                ServerAction.FETCH_TASKS_BY_REPORT,
                ServerAction.RECOMMEND_RESOURCES
        ));

        ROLE_PERMISSIONS.put(UserRole.RESOURCE_OFFICER, EnumSet.of(
                ServerAction.PING,
                ServerAction.AUTHENTICATE,
                ServerAction.FETCH_REPORTS,
                ServerAction.SEARCH_REPORTS,
                ServerAction.FETCH_ASSESSMENTS,
                ServerAction.FETCH_TASKS,
                ServerAction.FETCH_RESOURCES,
                ServerAction.FETCH_ALLOCATIONS,
                ServerAction.ALLOCATE_RESOURCE,
                ServerAction.FETCH_TASKS_BY_REPORT,
                ServerAction.RECOMMEND_RESOURCES
        ));

        ROLE_PERMISSIONS.put(UserRole.DEPARTMENT_OFFICER, EnumSet.of(
                ServerAction.PING,
                ServerAction.AUTHENTICATE,
                ServerAction.FETCH_REPORTS,
                ServerAction.SEARCH_REPORTS,
                ServerAction.FETCH_DEPARTMENTS,
                ServerAction.FETCH_TASKS,
                ServerAction.FETCH_RESOURCES,
                ServerAction.FETCH_ALLOCATIONS,
                ServerAction.UPDATE_TASK_STATUS,
                ServerAction.FETCH_TASKS_BY_REPORT
        ));

        ROLE_PERMISSIONS.put(UserRole.AUDITOR, EnumSet.of(
                ServerAction.PING,
                ServerAction.AUTHENTICATE,
                ServerAction.FETCH_REPORTS,
                ServerAction.SEARCH_REPORTS,
                ServerAction.FETCH_ASSESSMENTS,
                ServerAction.FETCH_DEPARTMENTS,
                ServerAction.FETCH_TASKS,
                ServerAction.FETCH_RESOURCES,
                ServerAction.FETCH_ALLOCATIONS,
                ServerAction.FETCH_TASKS_BY_REPORT,
                ServerAction.RECOMMEND_RESOURCES,
                ServerAction.FETCH_AUDIT_EVENTS,
                ServerAction.SEARCH_AUDIT_EVENTS
        ));
    }

    public boolean requiresAuthentication(ServerAction action) {
        return !PUBLIC_ACTIONS.contains(action);
    }

    public boolean isAuthenticated(User currentUser) {
        return currentUser != null;
    }

    public boolean isAuthorized(User currentUser, ServerAction action) {
        if (!requiresAuthentication(action)) {
            return true;
        }
        if (currentUser == null || currentUser.getRole() == null) {
            return false;
        }
        if ("VIEWER".equals(currentUser.getRole())) {
            return isLegacyViewerAuthorized(action);
        }
        try {
            UserRole role = UserRole.valueOf(currentUser.getRole());
            Set<ServerAction> permissions = ROLE_PERMISSIONS.get(role);
            return permissions != null && permissions.contains(action);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private boolean isLegacyViewerAuthorized(ServerAction action) {
        return switch (action) {
            case PING,
                 AUTHENTICATE,
                 FETCH_REPORTS,
                 SEARCH_REPORTS,
                 FETCH_ASSESSMENTS,
                 FETCH_DEPARTMENTS,
                 FETCH_TASKS,
                 FETCH_RESOURCES,
                 FETCH_ALLOCATIONS,
                 FETCH_TASKS_BY_REPORT,
                 RECOMMEND_RESOURCES -> true;
            default -> false;
        };
    }
}
