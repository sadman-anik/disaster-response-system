package com.sadman.drs.service;

import java.util.List;

/**
 * Creative feature: recommends emergency resources based on disaster type and severity.
 */
public class ResourceRecommendationService {

    public List<String> recommendResourceList(String disasterType, String severity) {
        String type = normalize(disasterType);
        String level = normalize(severity);

        if (type.equals("fire")) {
            if (isHighRisk(level)) {
                return List.of("Fire Truck", "Ambulance", "Police Patrol Unit", "Evacuation Team", "Temporary Shelter Kit");
            }
            return List.of("Fire Truck", "Medical Team", "Police Patrol Unit");
        }

        if (type.equals("flood")) {
            if (isHighRisk(level)) {
                return List.of("Rescue Boat", "Medical Team", "Temporary Shelter Kit", "Water Supply Tanker", "Police Patrol Unit");
            }
            return List.of("Rescue Boat", "Medical Team", "Temporary Shelter Kit");
        }

        if (type.equals("earthquake")) {
            if (isHighRisk(level)) {
                return List.of("Search and Rescue Team", "Ambulance", "Medical Team", "Electricity Repair Team", "Debris Removal Truck");
            }
            return List.of("Search and Rescue Team", "Medical Team", "Debris Removal Truck");
        }

        if (type.equals("hurricane") || type.equals("storm")) {
            return List.of("Evacuation Team", "Temporary Shelter Kit", "Medical Team", "Debris Removal Truck", "Water Supply Tanker");
        }

        if (type.equals("chemical spill")) {
            return List.of("Police Patrol Unit", "Ambulance", "Medical Team", "Evacuation Team");
        }

        return List.of("Medical Team", "Police Patrol Unit", "Temporary Shelter Kit");
    }

    public String recommendResources(String disasterType, String severity) {
        return String.join(", ", recommendResourceList(disasterType, severity));
    }

    private boolean isHighRisk(String severity) {
        return severity.equals("high") || severity.equals("critical");
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
