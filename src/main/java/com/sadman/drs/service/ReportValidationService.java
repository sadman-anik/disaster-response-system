package com.sadman.drs.service;

/**
 * Performs user input validation for the disaster report form.
 */
public class ReportValidationService {

    public String validateReport(String reportTitle, String disasterType, String severity, String location,
                                 String description, String reportedBy, String contactNumber) {
        if (isBlank(reportTitle)) {
            return "Report title is required.";
        }
        if (isBlank(disasterType)) {
            return "Disaster type is required.";
        }
        if (isBlank(severity)) {
            return "Severity is required.";
        }
        if (isBlank(location)) {
            return "Location is required.";
        }
        if (isBlank(description)) {
            return "Description is required.";
        }
        if (isBlank(reportedBy)) {
            return "Reporter name is required.";
        }
        if (isBlank(contactNumber)) {
            return "Contact number is required.";
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
