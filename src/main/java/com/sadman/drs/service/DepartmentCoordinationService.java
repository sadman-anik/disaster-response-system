package com.sadman.drs.service;

import com.sadman.drs.model.Department;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.ResponseTask;
import com.sadman.drs.repository.DepartmentRepository;
import com.sadman.drs.repository.ResponseTaskRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Coordinates departments and creates disaster response tasks.
 */
public class DepartmentCoordinationService {
    private final DepartmentRepository departmentRepository;
    private final ResponseTaskRepository responseTaskRepository;

    public DepartmentCoordinationService(DepartmentRepository departmentRepository,
                                         ResponseTaskRepository responseTaskRepository) {
        this.departmentRepository = departmentRepository;
        this.responseTaskRepository = responseTaskRepository;
    }

    public List<String> determineDepartments(String disasterType, String severity) {
        String type = normalize(disasterType);
        List<String> departments = new ArrayList<>();

        departments.add("Police");
        departments.add("Hospital and Ambulance");

        switch (type) {
            case "fire" -> {
                departments.add("Fire and Emergency");
                departments.add("Electricity Authority");
            }
            case "flood" -> {
                departments.add("Fire and Emergency");
                departments.add("Transportation Department");
                departments.add("Water Supply");
                departments.add("School Emergency Liaison");
            }
            case "earthquake" -> {
                departments.add("Fire and Emergency");
                departments.add("Electricity Authority");
                departments.add("Transportation Department");
                departments.add("Waste Management");
            }
            case "hurricane", "storm" -> {
                departments.add("Fire and Emergency");
                departments.add("Transportation Department");
                departments.add("Waste Management");
                departments.add("Water Supply");
                departments.add("School Emergency Liaison");
            }
            default -> departments.add("Fire and Emergency");
        }

        if (normalize(severity).equals("critical")) {
            departments.add("Waste Management");
        }

        return departments.stream().distinct().toList();
    }

    public List<String> getStandardActivities(String disasterType) {
        String type = normalize(disasterType);
        if (type.equals("hurricane") || type.equals("storm")) {
            return List.of("Warning/Evacuation", "Search and Rescue", "Immediate Assistance",
                    "Damage Assessment", "Continuing Assistance", "Infrastructure Restoration", "Debris Removal");
        }
        return List.of("Warning/Evacuation", "Search and Rescue", "Immediate Assistance",
                "Damage Assessment", "Continuing Assistance", "Infrastructure Restoration");
    }

    public List<ResponseTask> generateStandardTasks(DisasterReport report) throws SQLException {
        List<ResponseTask> createdTasks = new ArrayList<>();
        List<String> departmentNames = determineDepartments(report.getDisasterType(), report.getSeverity());
        List<String> activities = getStandardActivities(report.getDisasterType());

        for (int i = 0; i < departmentNames.size(); i++) {
            String departmentName = departmentNames.get(i);
            Optional<Department> department = departmentRepository.findByName(departmentName);
            if (department.isPresent()) {
                String activity = activities.get(i % activities.size());
                String taskDescription = createTaskDescription(report, activity, departmentName);
                int departmentId = department.get().getDepartmentId();
                if (!responseTaskRepository.existsTask(report.getReportId(), departmentId, activity)) {
                    ResponseTask task = new ResponseTask(report.getReportId(), departmentId,
                            activity, taskDescription, report.getPriorityLevel(), "Pending");
                    task.setDepartmentName(departmentName);
                    createdTasks.add(responseTaskRepository.save(task));
                }
            }
        }
        return createdTasks;
    }

    private String createTaskDescription(DisasterReport report, String activity, String departmentName) {
        return departmentName + " to handle " + activity.toLowerCase()
                + " for " + report.getDisasterType() + " at " + report.getLocation() + ".";
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
