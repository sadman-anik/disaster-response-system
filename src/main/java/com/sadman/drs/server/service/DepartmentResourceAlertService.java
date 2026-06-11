package com.sadman.drs.server.service;

import com.sadman.drs.model.Department;
import com.sadman.drs.model.DepartmentResourceAlert;
import com.sadman.drs.model.Resource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Detects departments that may struggle to respond because related resources are critically low.
 */
public class DepartmentResourceAlertService {
    public static final int DEFAULT_CRITICAL_THRESHOLD = 3;

    private static final Map<String, Set<String>> SERVICE_RESOURCE_KEYWORDS = Map.ofEntries(
            Map.entry("fire", Set.of("fire", "rescue", "evacuation")),
            Map.entry("medical", Set.of("medical", "ambulance")),
            Map.entry("patient", Set.of("medical", "ambulance")),
            Map.entry("transport", Set.of("transport", "road")),
            Map.entry("road", Set.of("transport", "road")),
            Map.entry("power", Set.of("electricity", "power", "repair")),
            Map.entry("electric", Set.of("electricity", "power", "repair")),
            Map.entry("waste", Set.of("waste", "debris")),
            Map.entry("debris", Set.of("waste", "debris")),
            Map.entry("water", Set.of("water")),
            Map.entry("shelter", Set.of("shelter", "relief")),
            Map.entry("school", Set.of("shelter", "relief")),
            Map.entry("safety", Set.of("security", "police", "patrol")),
            Map.entry("law", Set.of("security", "police", "patrol")),
            Map.entry("rescue", Set.of("rescue", "evacuation"))
    );

    public List<DepartmentResourceAlert> findCriticalAlerts(List<Department> departments, List<Resource> resources) {
        return findCriticalAlerts(departments, resources, DEFAULT_CRITICAL_THRESHOLD);
    }

    public List<DepartmentResourceAlert> findCriticalAlerts(
            List<Department> departments,
            List<Resource> resources,
            int criticalThreshold) {

        if (departments == null || resources == null || criticalThreshold < 0) {
            return List.of();
        }

        List<DepartmentResourceAlert> alerts = new ArrayList<>();
        for (Department department : departments) {
            Set<String> departmentKeywords = getDepartmentResourceKeywords(department);
            if (departmentKeywords.isEmpty()) {
                continue;
            }

            resources.stream()
                    .filter(resource -> resource.getQuantityAvailable() <= criticalThreshold)
                    .filter(resource -> matchesResourceKeywords(resource, departmentKeywords))
                    .forEach(resource -> alerts.add(new DepartmentResourceAlert(
                            department.getDepartmentName(),
                            department.getServiceType(),
                            resource.getResourceName(),
                            resource.getCategory(),
                            resource.getQuantityAvailable(),
                            criticalThreshold)));
        }

        return alerts.stream()
                .sorted(Comparator.comparingInt(DepartmentResourceAlert::getQuantityAvailable)
                        .thenComparing(DepartmentResourceAlert::getDepartmentName)
                        .thenComparing(DepartmentResourceAlert::getResourceName))
                .toList();
    }

    private Set<String> getDepartmentResourceKeywords(Department department) {
        String searchableText = normalize(department.getDepartmentName()) + " " + normalize(department.getServiceType());
        return SERVICE_RESOURCE_KEYWORDS.entrySet().stream()
                .filter(entry -> searchableText.contains(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toSet());
    }

    private boolean matchesResourceKeywords(Resource resource, Set<String> departmentKeywords) {
        String searchableText = normalize(resource.getResourceName()) + " " + normalize(resource.getCategory());
        return departmentKeywords.stream().anyMatch(searchableText::contains);
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
