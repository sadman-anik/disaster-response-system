package com.sadman.drs.model;

/**
 * Model class for external departments/organisations involved in disaster response.
 */
public class Department {
    private int departmentId;
    private String departmentName;
    private String serviceType;
    private String contactNumber;
    private String availabilityStatus;

    public Department() {
    }

    public Department(int departmentId, String departmentName, String serviceType,
                      String contactNumber, String availabilityStatus) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.serviceType = serviceType;
        this.contactNumber = contactNumber;
        this.availabilityStatus = availabilityStatus;
    }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    @Override
    public String toString() {
        return departmentName + " (" + serviceType + ")";
    }
}
