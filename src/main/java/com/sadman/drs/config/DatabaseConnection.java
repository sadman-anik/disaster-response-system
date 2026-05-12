package com.sadman.drs.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Handles MySQL connection and database/table initialization.
 */
public final class DatabaseConnection {

    private static final String PROPERTIES_FILE = "/database.properties";
    private static Properties properties;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        loadProperties();
        return DriverManager.getConnection(
                properties.getProperty("database.url"),
                properties.getProperty("database.username"),
                properties.getProperty("database.password")
        );
    }

    public static void initializeDatabase() throws SQLException {
        loadProperties();
        String databaseName = properties.getProperty("database.name");

        try (Connection serverConnection = DriverManager.getConnection(
                properties.getProperty("database.serverUrl"),
                properties.getProperty("database.username"),
                properties.getProperty("database.password"));
             Statement statement = serverConnection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);
        }

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
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

            ensureColumnExists(connection, "disaster_reports", "report_title",
                    "VARCHAR(150) NOT NULL DEFAULT 'Untitled Disaster Report'");
            statement.executeUpdate("""
                    UPDATE disaster_reports
                    SET report_title = CONCAT(disaster_type, ' at ', location)
                    WHERE report_title IS NULL OR report_title = '' OR report_title = 'Untitled Disaster Report'
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
                        resource_id INT NOT NULL,
                        quantity_allocated INT NOT NULL,
                        notes TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_allocation_report
                            FOREIGN KEY (report_id) REFERENCES disaster_reports(report_id)
                            ON DELETE CASCADE,
                        CONSTRAINT fk_allocation_resource
                            FOREIGN KEY (resource_id) REFERENCES resources(resource_id)
                            ON DELETE RESTRICT
                    )
                    """);
        }
    }

    private static void ensureColumnExists(Connection connection, String tableName, String columnName,
                                           String columnDefinition) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(connection.getCatalog(), null, tableName, columnName)) {
            if (!columns.next()) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN "
                            + columnName + " " + columnDefinition);
                }
            }
        }
    }

    private static void loadProperties() {
        if (properties != null) {
            return;
        }

        properties = new Properties();
        try (InputStream inputStream = DatabaseConnection.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("database.properties file was not found in resources.");
            }
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load database.properties", exception);
        }
    }
}
