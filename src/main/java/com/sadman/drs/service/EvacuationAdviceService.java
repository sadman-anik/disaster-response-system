package com.sadman.drs.service;

/**
 * Creative feature: generates public safety and evacuation advice based on disaster type and severity.
 */
public class EvacuationAdviceService {

    public String generateAdvice(String disasterType, String severity) {
        String type = normalize(disasterType);
        String level = normalize(severity);

        if (type.equals("fire") && isHighRisk(level)) {
            return "Evacuate immediately, avoid smoke-affected areas, and wait for fire and emergency instructions.";
        }
        if (type.equals("flood")) {
            if (isHighRisk(level)) {
                return "Move to higher ground immediately, avoid floodwater, and do not drive through water-covered roads.";
            }
            return "Move valuable items higher, monitor warnings, and avoid walking or driving through floodwater.";
        }
        if (type.equals("earthquake") && isHighRisk(level)) {
            return "Stay away from damaged buildings, check for injuries, and wait for rescue or safety instructions.";
        }
        if (type.equals("hurricane") || type.equals("storm")) {
            return "Stay indoors away from windows, prepare emergency supplies, and follow evacuation orders if issued.";
        }
        if (type.equals("chemical spill")) {
            return "Move away from the affected area, avoid direct contact, and wait for hazmat or emergency instructions.";
        }
        return "Follow official emergency instructions, stay alert, and move to a safe location if conditions worsen.";
    }

    private boolean isHighRisk(String severity) {
        return severity.equals("high") || severity.equals("critical");
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
