package com.sadman.drs.controller.security;

import com.sadman.drs.model.UserRole;

/**
 * Defines role-specific view permissions for the JavaFX client.
 */
public class RolePermissionService {

    public boolean canReport(String role) {
        return hasAnyRole(role, UserRole.ADMIN, UserRole.REPORTER);
    }

    public boolean canAssess(String role) {
        return hasAnyRole(role, UserRole.ADMIN, UserRole.ASSESSMENT_OFFICER);
    }

    public boolean canCoordinate(String role) {
        return hasAnyRole(role, UserRole.ADMIN, UserRole.ASSESSMENT_OFFICER);
    }

    public boolean canUpdateDepartmentTasks(String role) {
        return hasAnyRole(role, UserRole.ADMIN, UserRole.DEPARTMENT_OFFICER);
    }

    public boolean canManageResources(String role) {
        return hasAnyRole(role, UserRole.ADMIN, UserRole.RESOURCE_OFFICER);
    }

    public boolean canViewAudit(String role) {
        return hasAnyRole(role, UserRole.ADMIN, UserRole.AUDITOR);
    }

    public boolean canUpdateReportStatus(String role) {
        return hasAnyRole(role, UserRole.ADMIN);
    }

    private boolean hasAnyRole(String role, UserRole... allowedRoles) {
        if (role == null) {
            return false;
        }
        for (UserRole allowedRole : allowedRoles) {
            if (allowedRole.name().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
