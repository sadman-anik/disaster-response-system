package com.sadman.drs.model;

/**
 * Model class for assessment, damage level, priority score and priority result.
 */
public class AssessmentResult {
    private int assessmentId;
    private int reportId;
    private String reportTitle;
    private String damageLevel;
    private int peopleAffected;
    private boolean infrastructureDamage;
    private int priorityScore;
    private String priorityLevel;
    private String assessmentSummary;
    private String createdAt;

    public AssessmentResult() {
    }

    public AssessmentResult(int assessmentId, int reportId, String reportTitle, String damageLevel, int peopleAffected,
                            boolean infrastructureDamage, int priorityScore, String priorityLevel,
                            String assessmentSummary, String createdAt) {
        this.assessmentId = assessmentId;
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.damageLevel = damageLevel;
        this.peopleAffected = peopleAffected;
        this.infrastructureDamage = infrastructureDamage;
        this.priorityScore = priorityScore;
        this.priorityLevel = priorityLevel;
        this.assessmentSummary = assessmentSummary;
        this.createdAt = createdAt;
    }

    /**
     * Constructor used when creating a new assessment before it has been saved.
     * The report title is loaded later when assessment history is displayed.
     */
    public AssessmentResult(int reportId, String damageLevel, int peopleAffected,
                            boolean infrastructureDamage, int priorityScore,
                            String priorityLevel, String assessmentSummary) {
        this(0, reportId, null, damageLevel, peopleAffected, infrastructureDamage,
                priorityScore, priorityLevel, assessmentSummary, null);
    }

    /**
     * Backward compatible constructor for older code that only has the report id.
     */
    public AssessmentResult(int assessmentId, int reportId, String damageLevel, int peopleAffected,
                            boolean infrastructureDamage, int priorityScore, String priorityLevel,
                            String assessmentSummary, String createdAt) {
        this(assessmentId, reportId, null, damageLevel, peopleAffected, infrastructureDamage,
                priorityScore, priorityLevel, assessmentSummary, createdAt);
    }

    public int getAssessmentId() { return assessmentId; }
    public void setAssessmentId(int assessmentId) { this.assessmentId = assessmentId; }
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }

    /**
     * Used by the JavaFX assessment history table to show ID + Title instead of ID only.
     */
    public String getReportDisplayName() {
        String title = (reportTitle == null || reportTitle.trim().isEmpty())
                ? "Untitled Disaster Report"
                : reportTitle.trim();
        if (reportId <= 0) {
            return title;
        }
        return "#" + reportId + " - " + title;
    }

    public String getDamageLevel() { return damageLevel; }
    public void setDamageLevel(String damageLevel) { this.damageLevel = damageLevel; }
    public int getPeopleAffected() { return peopleAffected; }
    public void setPeopleAffected(int peopleAffected) { this.peopleAffected = peopleAffected; }
    public boolean isInfrastructureDamage() { return infrastructureDamage; }
    public void setInfrastructureDamage(boolean infrastructureDamage) { this.infrastructureDamage = infrastructureDamage; }
    public int getPriorityScore() { return priorityScore; }
    public void setPriorityScore(int priorityScore) { this.priorityScore = priorityScore; }
    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }
    public String getAssessmentSummary() { return assessmentSummary; }
    public void setAssessmentSummary(String assessmentSummary) { this.assessmentSummary = assessmentSummary; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Assessment #" + assessmentId + " for " + getReportDisplayName()
                + " | Priority: " + priorityLevel + " | Score: " + priorityScore;
    }
}
