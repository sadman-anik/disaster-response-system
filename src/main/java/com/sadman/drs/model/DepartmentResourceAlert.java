package com.sadman.drs.model;

import java.io.Serializable;

/**
 * Warns administrators when a department depends on a critically low resource.
 */
public class DepartmentResourceAlert implements Serializable {
    private String departmentName;
    private String serviceType;
    private String resourceName;
    private String resourceCategory;
    private int quantityAvailable;
    private int criticalThreshold;

    public DepartmentResourceAlert() {
    }

    public DepartmentResourceAlert(String departmentName, String serviceType, String resourceName,
                                   String resourceCategory, int quantityAvailable, int criticalThreshold) {
        this.departmentName = departmentName;
        this.serviceType = serviceType;
        this.resourceName = resourceName;
        this.resourceCategory = resourceCategory;
        this.quantityAvailable = quantityAvailable;
        this.criticalThreshold = criticalThreshold;
    }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    public String getResourceCategory() { return resourceCategory; }
    public void setResourceCategory(String resourceCategory) { this.resourceCategory = resourceCategory; }
    public int getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(int quantityAvailable) { this.quantityAvailable = quantityAvailable; }
    public int getCriticalThreshold() { return criticalThreshold; }
    public void setCriticalThreshold(int criticalThreshold) { this.criticalThreshold = criticalThreshold; }

    public String getAlertLevel() {
        return "Critical";
    }

    public String getAlertMessage() {
        return departmentName + " may not be responsive: " + resourceName
                + " is at " + quantityAvailable + " units (threshold " + criticalThreshold + ").";
    }
}
