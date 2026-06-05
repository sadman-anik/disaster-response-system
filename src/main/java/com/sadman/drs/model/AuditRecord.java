package com.sadman.drs.model;

import java.io.Serializable;

/**
 * Represents an audit record for important system changes.
 */
public class AuditRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int auditId;
    private String entityType;
    private int entityId;
    private String entityLabel;
    private String actionType;
    private String username;
    private String changeDetails;
    private String createdAt;

    public AuditRecord() {
    }

    public AuditRecord(String entityType, int entityId, String entityLabel,
                       String actionType, String username, String changeDetails) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityLabel = entityLabel;
        this.actionType = actionType;
        this.username = username;
        this.changeDetails = changeDetails;
    }

    public AuditRecord(int auditId, String entityType, int entityId, String entityLabel,
                       String actionType, String username, String changeDetails, String createdAt) {
        this.auditId = auditId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityLabel = entityLabel;
        this.actionType = actionType;
        this.username = username;
        this.changeDetails = changeDetails;
        this.createdAt = createdAt;
    }

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getEntityLabel() {
        return entityLabel;
    }

    public void setEntityLabel(String entityLabel) {
        this.entityLabel = entityLabel;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChangeDetails() {
        return changeDetails;
    }

    public void setChangeDetails(String changeDetails) {
        this.changeDetails = changeDetails;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
