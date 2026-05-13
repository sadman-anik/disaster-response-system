package com.sadman.drs.service;

import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.DisasterReport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceLogicTest {

    private final DisasterAssessmentService assessmentService = new DisasterAssessmentService();
    private final EvacuationAdviceService evacuationAdviceService = new EvacuationAdviceService();
    private final ResourceRecommendationService resourceRecommendationService = new ResourceRecommendationService();
    private final ReportValidationService validationService = new ReportValidationService();

    @Test
    void fireHighShouldGenerateEvacuationAdvice() {
        String advice = evacuationAdviceService.generateAdvice("Fire", "High");

        assertNotNull(advice);
        assertFalse(advice.isBlank());

        String lowerAdvice = advice.toLowerCase();
        assertTrue(
                lowerAdvice.contains("evacuate")
                        || lowerAdvice.contains("smoke")
                        || lowerAdvice.contains("fire"),
                "Advice should contain fire-related safety instructions."
        );
    }

    @Test
    void floodMediumShouldGenerateRelevantAdvice() {
        String advice = evacuationAdviceService.generateAdvice("Flood", "Medium");

        assertNotNull(advice);
        assertFalse(advice.isBlank());

        String lowerAdvice = advice.toLowerCase();
        assertTrue(
                lowerAdvice.contains("higher")
                        || lowerAdvice.contains("water")
                        || lowerAdvice.contains("flood"),
                "Flood advice should mention higher ground, water, or flood safety."
        );
    }

    @Test
    void earthquakeCriticalShouldRecommendEmergencyResources() {
        String resources = resourceRecommendationService.recommendResources("Earthquake", "Critical");

        assertNotNull(resources);
        assertFalse(resources.isBlank());

        String lowerResources = resources.toLowerCase();
        assertTrue(
                lowerResources.contains("rescue")
                        || lowerResources.contains("hospital")
                        || lowerResources.contains("medical")
                        || lowerResources.contains("electricity"),
                "Critical earthquake should recommend rescue, hospital, medical, or infrastructure support."
        );
    }

    @Test
    void fireHighShouldRecommendFireOrEmergencyResources() {
        String resources = resourceRecommendationService.recommendResources("Fire", "High");

        assertNotNull(resources);
        assertFalse(resources.isBlank());

        String lowerResources = resources.toLowerCase();
        assertTrue(
                lowerResources.contains("fire")
                        || lowerResources.contains("ambulance")
                        || lowerResources.contains("police")
                        || lowerResources.contains("evacuation"),
                "High fire should recommend fire, ambulance, police, or evacuation resources."
        );
    }

    @Test
    void highSeverityShouldGenerateHighOrCriticalInitialPriority() {
        String priority = assessmentService.estimateInitialPriority("Fire", "High");

        assertNotNull(priority);
        assertFalse(priority.isBlank());

        assertTrue(
                priority.equalsIgnoreCase("High") || priority.equalsIgnoreCase("Critical"),
                "High severity fire should generate High or Critical priority."
        );
    }

    @Test
    void severeDamageWithManyPeopleShouldGenerateAssessmentResult() {
        DisasterReport report = new DisasterReport(
                "Parramatta Fire Emergency",
                "Fire",
                "High",
                "Parramatta",
                "Large fire reported in a residential building.",
                "Sadman",
                "0400000000",
                "Reported",
                "High",
                "Evacuate immediately and avoid smoke-affected areas.",
                "Fire truck, ambulance, police, evacuation team"
        );

        AssessmentResult result = assessmentService.assessDisaster(
                report,
                "Severe",
                100,
                true
        );

        assertNotNull(result);
        assertTrue(result.getPriorityScore() > 0);
        assertNotNull(result.getPriorityLevel());
        assertFalse(result.getPriorityLevel().isBlank());
        assertNotNull(result.getAssessmentSummary());
        assertFalse(result.getAssessmentSummary().isBlank());
    }

    @Test
    void validationShouldRejectMissingLocation() {
        String validationError = validationService.validateReport(
                "Flood Emergency",
                "Flood",
                "Medium",
                "",
                "Flood water entering houses.",
                "Sadman",
                "0400000000"
        );

        assertNotNull(validationError);
        assertFalse(validationError.isBlank());
        assertTrue(validationError.toLowerCase().contains("location"));
    }

    @Test
    void validationShouldAcceptCompleteReport() {
        String validationError = validationService.validateReport(
                "Flood Emergency",
                "Flood",
                "Medium",
                "Blacktown",
                "Flood water entering houses.",
                "Sadman",
                "0400000000"
        );

        assertNull(validationError);
    }
}