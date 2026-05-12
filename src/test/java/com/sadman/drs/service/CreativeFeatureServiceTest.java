package com.sadman.drs.service;

import com.sadman.drs.model.DisasterReport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreativeFeatureServiceTest {

    @Test
    void fireHighSeverityShouldGenerateEvacuationAdvice() {
        EvacuationAdviceService service = new EvacuationAdviceService();
        String advice = service.generateAdvice("Fire", "High");
        assertTrue(advice.toLowerCase().contains("evacuate"));
        assertTrue(advice.toLowerCase().contains("smoke"));
    }

    @Test
    void floodMediumShouldRecommendRescueBoat() {
        ResourceRecommendationService service = new ResourceRecommendationService();
        String resources = service.recommendResources("Flood", "Medium");
        assertTrue(resources.contains("Rescue Boat"));
        assertTrue(resources.contains("Medical Team"));
    }

    @Test
    void duplicateDetectionShouldMatchSameTypeAndLocationIgnoringCase() {
        DuplicateReportService service = new DuplicateReportService(null);
        assertTrue(service.isSimilar("Fire", "Parramatta", "fire", "parramatta"));
        assertFalse(service.isSimilar("Fire", "Parramatta", "Flood", "Parramatta"));
    }

    @Test
    void criticalDisasterShouldHaveHighPriorityScore() {
        DisasterAssessmentService service = new DisasterAssessmentService();
        int score = service.calculatePriorityScore("Critical", "Severe", 120, true);
        assertEquals("Critical", service.convertScoreToPriority(score));
    }

    @Test
    void assessmentShouldCreateSummary() {
        DisasterReport report = new DisasterReport(1, "Earthquake", "High", "Sydney CBD",
                "Building damage", "Sadman", "0400000000", "Reported", "High",
                "Advice", "Resources", "2026-05-12");

        DisasterAssessmentService service = new DisasterAssessmentService();
        var result = service.assessDisaster(report, "Major", 40, true);

        assertEquals(1, result.getReportId());
        assertNotNull(result.getAssessmentSummary());
        assertTrue(result.getPriorityScore() > 0);
    }
}
