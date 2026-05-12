package com.sadman.drs.model;

/**
 * Model class for available emergency resources.
 */
public class Resource {
    private int resourceId;
    private String resourceName;
    private String category;
    private int quantityAvailable;

    public Resource() {
    }

    public Resource(int resourceId, String resourceName, String category, int quantityAvailable) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.category = category;
        this.quantityAvailable = quantityAvailable;
    }

    public int getResourceId() { return resourceId; }
    public void setResourceId(int resourceId) { this.resourceId = resourceId; }
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(int quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    @Override
    public String toString() {
        return resourceName + " (Available: " + quantityAvailable + ")";
    }
}
