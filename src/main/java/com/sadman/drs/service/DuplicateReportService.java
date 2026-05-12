package com.sadman.drs.service;

import com.sadman.drs.repository.DisasterReportRepository;

import java.sql.SQLException;

/**
 * Creative feature: checks whether a similar open disaster report already exists.
 */
public class DuplicateReportService {
    private final DisasterReportRepository disasterReportRepository;

    public DuplicateReportService(DisasterReportRepository disasterReportRepository) {
        this.disasterReportRepository = disasterReportRepository;
    }

    public boolean isDuplicate(String disasterType, String location) throws SQLException {
        return disasterReportRepository.existsSimilarOpenReport(disasterType, location);
    }

    public boolean isSimilar(String existingType, String existingLocation, String newType, String newLocation) {
        return normalize(existingType).equals(normalize(newType))
                && normalize(existingLocation).equals(normalize(newLocation));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
