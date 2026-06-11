package com.sadman.drs.server.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates the MySQL schema used by the DRS server.
 */
public class DatabaseInitializer {

    public void initialize() throws SQLException {
        createDatabaseIfMissing();
        createTablesIfMissing();
    }

    private void createDatabaseIfMissing() throws SQLException {
        String databaseName = DatabaseConnection.getProperty("database.name");
        try (Connection serverConnection = DriverManager.getConnection(
                DatabaseConnection.getProperty("database.serverUrl"),
                DatabaseConnection.getProperty("database.username"),
                DatabaseConnection.getProperty("database.password"));
             Statement statement = serverConnection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);
        }
    }

    private void createTablesIfMissing() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS disaster_reports (
                        report_id INT AUTO_INCREMENT PRIMARY KEY,
                        report_title VARCHAR(150) NOT NULL DEFAULT 'Untitled Disaster Report',
                        disaster_type VARCHAR(60) NOT NULL,
                        severity VARCHAR(40) NOT NULL,
                        location VARCHAR(150) NOT NULL,
                        description TEXT NOT NULL,
                        reported_by VARCHAR(100) NOT NULL,
                        contact_number VARCHAR(40) NOT NULL,
                        status VARCHAR(40) NOT NULL,
                        priority_level VARCHAR(40) NOT NULL,
                        evacuation_advice TEXT,
                        recommended_resources TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS disaster_assessments (
                        assessment_id INT AUTO_INCREMENT PRIMARY KEY,
                        report_id INT NOT NULL,
                        damage_level VARCHAR(40) NOT NULL,
                        people_affected INT NOT NULL,
                        infrastructure_damage BOOLEAN NOT NULL,
                        priority_score INT NOT NULL,
                        priority_level VARCHAR(40) NOT NULL,
                        assessment_summary TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_assessment_report
                            FOREIGN KEY (report_id) REFERENCES disaster_reports(report_id)
                            ON DELETE CASCADE
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS departments (
                        department_id INT AUTO_INCREMENT PRIMARY KEY,
                        department_name VARCHAR(120) NOT NULL UNIQUE,
                        service_type VARCHAR(120) NOT NULL,
                        contact_number VARCHAR(40) NOT NULL,
                        availability_status VARCHAR(40) NOT NULL
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS response_tasks (
                        task_id INT AUTO_INCREMENT PRIMARY KEY,
                        report_id INT NOT NULL,
                        department_id INT NOT NULL,
                        activity_type VARCHAR(80) NOT NULL,
                        task_description TEXT NOT NULL,
                        priority_level VARCHAR(40) NOT NULL,
                        status VARCHAR(40) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_task_report
                            FOREIGN KEY (report_id) REFERENCES disaster_reports(report_id)
                            ON DELETE CASCADE,
                        CONSTRAINT fk_task_department
                            FOREIGN KEY (department_id) REFERENCES departments(department_id)
                            ON DELETE RESTRICT
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS resources (
                        resource_id INT AUTO_INCREMENT PRIMARY KEY,
                        resource_name VARCHAR(120) NOT NULL UNIQUE,
                        category VARCHAR(80) NOT NULL,
                        quantity_available INT NOT NULL
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS resource_allocations (
                        allocation_id INT AUTO_INCREMENT PRIMARY KEY,
                        report_id INT NOT NULL,
                        task_id INT NOT NULL,
                        resource_id INT NOT NULL,
                        quantity_allocated INT NOT NULL,
                        notes TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_allocation_report
                            FOREIGN KEY (report_id) REFERENCES disaster_reports(report_id)
                            ON DELETE CASCADE,
                        CONSTRAINT fk_allocation_task
                            FOREIGN KEY (task_id) REFERENCES response_tasks(task_id)
                            ON DELETE CASCADE,
                        CONSTRAINT fk_allocation_resource
                            FOREIGN KEY (resource_id) REFERENCES resources(resource_id)
                            ON DELETE RESTRICT
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        user_id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(80) NOT NULL UNIQUE,
                        role VARCHAR(40) NOT NULL,
                        password_hash VARCHAR(255) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS audit_events (
                        audit_id INT AUTO_INCREMENT PRIMARY KEY,
                        entity_type VARCHAR(60) NOT NULL,
                        entity_id INT NOT NULL,
                        entity_label VARCHAR(180) NOT NULL,
                        action_type VARCHAR(80) NOT NULL,
                        username VARCHAR(80) NOT NULL,
                        change_details TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
        }
    }
}
