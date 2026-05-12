package com.sadman.drs.service;

import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.DisasterReport;

/**
 * Provides quick assessment and priority calculation for reported disasters.
 */
public class DisasterAssessmentService {

    public String estimateInitialPriority(String disasterType, String severity) {
        String type = normalize(disasterType);
        String level = normalize(severity);

        if (level.equals("critical")) {
            return "Critical";
        }
        if (level.equals("high")) {
            return type.equals("fire") || type.equals("earthquake") || type.equals("flood") ? "High" : "Medium";
        }
        if (level.equals("medium")) {
            return "Medium";
        }
        return "Low";
    }

    public AssessmentResult assessDisaster(DisasterReport report, String damageLevel,
                                           int peopleAffected, boolean infrastructureDamage) {
        int score = calculatePriorityScore(report.getSeverity(), damageLevel, peopleAffected, infrastructureDamage);
        String priorityLevel = convertScoreToPriority(score);
        String summary = buildAssessmentSummary(report, damageLevel, peopleAffected, infrastructureDamage, score, priorityLevel);

        return new AssessmentResult(report.getReportId(), damageLevel, peopleAffected,
                infrastructureDamage, score, priorityLevel, summary);
    }

    public int calculatePriorityScore(String severity, String damageLevel, int peopleAffected,
                                      boolean infrastructureDamage) {
        int score = 0;
        score += switch (normalize(severity)) {
            case "critical" -> 45;
            case "high" -> 35;
            case "medium" -> 22;
            default -> 10;
        };
        score += switch (normalize(damageLevel)) {
            case "severe" -> 35;
            case "major" -> 28;
            case "moderate" -> 18;
            default -> 8;
        };
        if (peopleAffected >= 100) {
            score += 20;
        } else if (peopleAffected >= 30) {
            score += 12;
        } else if (peopleAffected > 0) {
            score += 5;
        }
        if (infrastructureDamage) {
            score += 15;
        }
        return Math.min(score, 100);
    }

    public String convertScoreToPriority(int score) {
        if (score >= 80) {
            return "Critical";
        }
        if (score >= 60) {
            return "High";
        }
        if (score >= 35) {
            return "Medium";
        }
        return "Low";
    }

    private String buildAssessmentSummary(DisasterReport report, String damageLevel,
                                          int peopleAffected, boolean infrastructureDamage,
                                          int score, String priorityLevel) {
        return "Report #" + report.getReportId() + " assessed as " + priorityLevel
                + " priority with score " + score + ". Damage level: " + damageLevel
                + ". People affected: " + peopleAffected
                + ". Infrastructure damage: " + (infrastructureDamage ? "Yes" : "No") + ".";
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
