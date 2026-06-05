package com.sadman.drs.controller.validation;

/**
 * Performs user input validation for the disaster report form.
 */
public class ReportValidationService {

    public String validateReport(String reportTitle, String disasterType, String severity, String location,
                                 String description, String reportedBy, String contactNumber) {
        if (FormValueHelper.isBlank(reportTitle)) {
            return "Report title is required.";
        }
        if (FormValueHelper.isBlank(disasterType)) {
            return "Disaster type is required.";
        }
        if (FormValueHelper.isBlank(severity)) {
            return "Severity is required.";
        }
        if (FormValueHelper.isBlank(location)) {
            return "Location is required.";
        }
        if (FormValueHelper.isBlank(description)) {
            return "Description is required.";
        }
        if (FormValueHelper.isBlank(reportedBy)) {
            return "Reporter name is required.";
        }
        if (FormValueHelper.isBlank(contactNumber)) {
            return "Contact number is required.";
        }
        return null;
    }
}
