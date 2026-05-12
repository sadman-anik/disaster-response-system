package com.sadman.drs.repository;

import com.sadman.drs.config.DatabaseConnection;
import com.sadman.drs.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for departments table.
 */
public class DepartmentRepository {

    public void seedDefaultDepartments() throws SQLException {
        if (!findAll().isEmpty()) {
            return;
        }

        save(new Department(0, "Fire and Emergency", "Fire control, rescue and evacuation", "000-FIRE", "Available"));
        save(new Department(0, "Hospital and Ambulance", "Medical support and patient transport", "000-MED", "Available"));
        save(new Department(0, "Police", "Public safety and law enforcement", "000-POL", "Available"));
        save(new Department(0, "Electricity Authority", "Power isolation and repair", "131-ELEC", "Available"));
        save(new Department(0, "Transportation Department", "Road access and transport coordination", "131-ROAD", "Available"));
        save(new Department(0, "Waste Management", "Debris and waste removal", "131-WASTE", "Available"));
        save(new Department(0, "Water Supply", "Clean water and supply restoration", "131-WATER", "Available"));
        save(new Department(0, "School Emergency Liaison", "School safety and temporary shelter support", "131-SCHOOL", "Available"));
    }

    public Department save(Department department) throws SQLException {
        String sql = """
                INSERT INTO departments (department_name, service_type, contact_number, availability_status)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, department.getDepartmentName());
            statement.setString(2, department.getServiceType());
            statement.setString(3, department.getContactNumber());
            statement.setString(4, department.getAvailabilityStatus());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    department.setDepartmentId(keys.getInt(1));
                }
            }
        }
        return department;
    }

    public List<Department> findAll() throws SQLException {
        String sql = "SELECT * FROM departments ORDER BY department_name";
        List<Department> departments = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                departments.add(mapResultSet(resultSet));
            }
        }
        return departments;
    }

    public Optional<Department> findByName(String departmentName) throws SQLException {
        String sql = "SELECT * FROM departments WHERE department_name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, departmentName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSet(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    private Department mapResultSet(ResultSet resultSet) throws SQLException {
        return new Department(
                resultSet.getInt("department_id"),
                resultSet.getString("department_name"),
                resultSet.getString("service_type"),
                resultSet.getString("contact_number"),
                resultSet.getString("availability_status")
        );
    }
}
