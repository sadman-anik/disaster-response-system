package com.sadman.drs.repository;

import com.sadman.drs.config.DatabaseConnection;
import com.sadman.drs.model.Resource;
import com.sadman.drs.model.ResourceAllocation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for resources and resource_allocations tables.
 */
public class ResourceRepository {

    public void seedDefaultResources() throws SQLException {
        if (!findAll().isEmpty()) {
            return;
        }
        save(new Resource(0, "Fire Truck", "Fire Response", 8));
        save(new Resource(0, "Ambulance", "Medical", 12));
        save(new Resource(0, "Police Patrol Unit", "Security", 10));
        save(new Resource(0, "Evacuation Team", "Evacuation", 6));
        save(new Resource(0, "Rescue Boat", "Flood Rescue", 5));
        save(new Resource(0, "Search and Rescue Team", "Rescue", 7));
        save(new Resource(0, "Temporary Shelter Kit", "Relief", 50));
        save(new Resource(0, "Medical Team", "Medical", 9));
        save(new Resource(0, "Electricity Repair Team", "Infrastructure", 4));
        save(new Resource(0, "Debris Removal Truck", "Waste Management", 6));
        save(new Resource(0, "Water Supply Tanker", "Water Supply", 5));
    }

    public Resource save(Resource resource) throws SQLException {
        String sql = "INSERT INTO resources (resource_name, category, quantity_available) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, resource.getResourceName());
            statement.setString(2, resource.getCategory());
            statement.setInt(3, resource.getQuantityAvailable());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    resource.setResourceId(keys.getInt(1));
                }
            }
        }
        return resource;
    }

    public List<Resource> findAll() throws SQLException {
        String sql = "SELECT * FROM resources ORDER BY resource_name";
        List<Resource> resources = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                resources.add(new Resource(
                        resultSet.getInt("resource_id"),
                        resultSet.getString("resource_name"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity_available")
                ));
            }
        }
        return resources;
    }

    public void allocateResource(int reportId, Resource resource, int quantity, String notes) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (quantity > resource.getQuantityAvailable()) {
            throw new IllegalArgumentException("Not enough resource quantity available.");
        }

        String allocationSql = """
                INSERT INTO resource_allocations (report_id, resource_id, quantity_allocated, notes)
                VALUES (?, ?, ?, ?)
                """;
        String updateSql = "UPDATE resources SET quantity_available = quantity_available - ? WHERE resource_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement allocationStatement = connection.prepareStatement(allocationSql);
                 PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                allocationStatement.setInt(1, reportId);
                allocationStatement.setInt(2, resource.getResourceId());
                allocationStatement.setInt(3, quantity);
                allocationStatement.setString(4, notes);
                allocationStatement.executeUpdate();

                updateStatement.setInt(1, quantity);
                updateStatement.setInt(2, resource.getResourceId());
                updateStatement.executeUpdate();
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<ResourceAllocation> findAllocations() throws SQLException {
        String sql = """
                SELECT ra.*, r.resource_name
                FROM resource_allocations ra
                JOIN resources r ON ra.resource_id = r.resource_id
                ORDER BY ra.allocation_id DESC
                """;
        List<ResourceAllocation> allocations = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                allocations.add(new ResourceAllocation(
                        resultSet.getInt("allocation_id"),
                        resultSet.getInt("report_id"),
                        resultSet.getInt("resource_id"),
                        resultSet.getString("resource_name"),
                        resultSet.getInt("quantity_allocated"),
                        resultSet.getString("notes"),
                        resultSet.getString("created_at")
                ));
            }
        }
        return allocations;
    }
}
