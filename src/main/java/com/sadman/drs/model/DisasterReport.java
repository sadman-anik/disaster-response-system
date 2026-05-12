package com.sadman.drs.model;

/**
 * Model class that stores the main disaster report details.
 */
public class DisasterReport {
    private int reportId;
    private String reportTitle;
    private String disasterType;
    private String severity;
    private String location;
    private String description;
    private String reportedBy;
    private String contactNumber;
    private String status;
    private String priorityLevel;
    private String evacuationAdvice;
    private String recommendedResources;
    private String createdAt;

    public DisasterReport() {
    }

    public DisasterReport(int reportId, String reportTitle, String disasterType, String severity, String location,
                          String description, String reportedBy, String contactNumber,
                          String status, String priorityLevel, String evacuationAdvice,
                          String recommendedResources, String createdAt) {
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.disasterType = disasterType;
        this.severity = severity;
        this.location = location;
        this.description = description;
        this.reportedBy = reportedBy;
        this.contactNumber = contactNumber;
        this.status = status;
        this.priorityLevel = priorityLevel;
        this.evacuationAdvice = evacuationAdvice;
        this.recommendedResources = recommendedResources;
        this.createdAt = createdAt;
    }

    /**
     * Backward compatible constructor used by older unit tests or older code.
     * It creates a meaningful default title from disaster type and location.
     */
    public DisasterReport(int reportId, String disasterType, String severity, String location,
                          String description, String reportedBy, String contactNumber,
                          String status, String priorityLevel, String evacuationAdvice,
                          String recommendedResources, String createdAt) {
        this(reportId, generateDefaultTitle(disasterType, location), disasterType, severity, location,
                description, reportedBy, contactNumber, status, priorityLevel, evacuationAdvice,
                recommendedResources, createdAt);
    }

    public DisasterReport(String reportTitle, String disasterType, String severity, String location,
                          String description, String reportedBy, String contactNumber,
                          String status, String priorityLevel, String evacuationAdvice,
                          String recommendedResources) {
        this(0, reportTitle, disasterType, severity, location, description, reportedBy, contactNumber,
                status, priorityLevel, evacuationAdvice, recommendedResources, null);
    }

    /**
     * Backward compatible constructor used by older code.
     */
    public DisasterReport(String disasterType, String severity, String location,
                          String description, String reportedBy, String contactNumber,
                          String status, String priorityLevel, String evacuationAdvice,
                          String recommendedResources) {
        this(generateDefaultTitle(disasterType, location), disasterType, severity, location, description,
                reportedBy, contactNumber, status, priorityLevel, evacuationAdvice, recommendedResources);
    }

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
    public String getDisasterType() { return disasterType; }
    public void setDisasterType(String disasterType) { this.disasterType = disasterType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }
    public String getEvacuationAdvice() { return evacuationAdvice; }
    public void setEvacuationAdvice(String evacuationAdvice) { this.evacuationAdvice = evacuationAdvice; }
    public String getRecommendedResources() { return recommendedResources; }
    public void setRecommendedResources(String recommendedResources) { this.recommendedResources = recommendedResources; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /**
     * Used by JavaFX table columns and combo boxes to show Report ID + Title together.
     */
    public String getReportDisplayName() {
        String title = (reportTitle == null || reportTitle.trim().isEmpty())
                ? generateDefaultTitle(disasterType, location)
                : reportTitle.trim();
        if (reportId <= 0) {
            return title;
        }
        return "#" + reportId + " - " + title;
    }

    private static String generateDefaultTitle(String disasterType, String location) {
        String type = disasterType == null || disasterType.trim().isEmpty() ? "Disaster" : disasterType.trim();
        String place = location == null || location.trim().isEmpty() ? "Unknown location" : location.trim();
        return type + " at " + place;
    }

    @Override
    public String toString() {
        return getReportDisplayName() + " | " + disasterType + " | " + location + " | " + severity;
    }
}
