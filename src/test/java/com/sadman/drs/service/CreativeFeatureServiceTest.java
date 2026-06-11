package com.sadman.drs.service;

import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Department;
import com.sadman.drs.model.DepartmentResourceAlert;
import com.sadman.drs.model.Resource;
import com.sadman.drs.server.service.DisasterAssessmentService;
import com.sadman.drs.server.service.DepartmentResourceAlertService;
import com.sadman.drs.server.service.DuplicateReportService;
import com.sadman.drs.server.service.EvacuationAdviceService;
import com.sadman.drs.server.service.ResourceRecommendationService;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void departmentResourceAlertShouldFlagCriticallyLowMatchingResource() {
        DepartmentResourceAlertService service = new DepartmentResourceAlertService();
        List<Department> departments = List.of(
                new Department(1, "Hospital and Ambulance", "Medical support and patient transport", "000-MED", "Available"),
                new Department(2, "Water Supply", "Clean water and supply restoration", "131-WATER", "Available"));
        List<Resource> resources = List.of(
                new Resource(1, "Medical Team", "Medical", 2),
                new Resource(2, "Water Supply Tanker", "Water Supply", 7));

        List<DepartmentResourceAlert> alerts = service.findCriticalAlerts(departments, resources);

        assertEquals(1, alerts.size());
        assertEquals("Hospital and Ambulance", alerts.get(0).getDepartmentName());
        assertEquals("Medical Team", alerts.get(0).getResourceName());
        assertTrue(alerts.get(0).getAlertMessage().contains("may not be responsive"));
    }

    @Test
    void departmentResourceAlertShouldIncludeResourceAtThreshold() {
        DepartmentResourceAlertService service = new DepartmentResourceAlertService();
        List<Department> departments = List.of(
                new Department(1, "Fire and Emergency", "Fire control, rescue and evacuation", "000-FIRE", "Available"));
        List<Resource> resources = List.of(new Resource(1, "Fire Truck", "Fire Response", 3));

        List<DepartmentResourceAlert> alerts = service.findCriticalAlerts(departments, resources);

        assertEquals(1, alerts.size());
        assertEquals(3, alerts.get(0).getQuantityAvailable());
        assertEquals(DepartmentResourceAlertService.DEFAULT_CRITICAL_THRESHOLD, alerts.get(0).getCriticalThreshold());
    }

    @Test
    void departmentResourceAlertShouldIgnoreHealthyResources() {
        DepartmentResourceAlertService service = new DepartmentResourceAlertService();
        List<Department> departments = List.of(
                new Department(1, "Police", "Public safety and law enforcement", "000-POL", "Available"));
        List<Resource> resources = List.of(new Resource(1, "Police Patrol Unit", "Security", 4));

        List<DepartmentResourceAlert> alerts = service.findCriticalAlerts(departments, resources);

        assertTrue(alerts.isEmpty());
    }
}
