package com.sadman.drs.repository;

import com.sadman.drs.config.DatabaseConnection;
import com.sadman.drs.model.DisasterReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for disaster_reports table.
 */
public class DisasterReportRepository {

    public DisasterReport save(DisasterReport report) throws SQLException {
        String sql = """
                INSERT INTO disaster_reports
                (report_title, disaster_type, severity, location, description, reported_by, contact_number,
                 status, priority_level, evacuation_advice, recommended_resources)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, report.getReportTitle());
            statement.setString(2, report.getDisasterType());
            statement.setString(3, report.getSeverity());
            statement.setString(4, report.getLocation());
            statement.setString(5, report.getDescription());
            statement.setString(6, report.getReportedBy());
            statement.setString(7, report.getContactNumber());
            statement.setString(8, report.getStatus());
            statement.setString(9, report.getPriorityLevel());
            statement.setString(10, report.getEvacuationAdvice());
            statement.setString(11, report.getRecommendedResources());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    report.setReportId(keys.getInt(1));
                }
            }
        }
        return report;
    }

    public List<DisasterReport> findAll() throws SQLException {
        String sql = "SELECT * FROM disaster_reports ORDER BY report_id DESC";
        List<DisasterReport> reports = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                reports.add(mapResultSet(resultSet));
            }
        }
        return reports;
    }

    public List<DisasterReport> search(String keyword) throws SQLException {
        String sql = """
                SELECT * FROM disaster_reports
                WHERE report_title LIKE ? OR disaster_type LIKE ? OR severity LIKE ? OR location LIKE ? OR status LIKE ? OR priority_level LIKE ?
                ORDER BY report_id DESC
                """;
        List<DisasterReport> reports = new ArrayList<>();
        String searchText = "%" + keyword + "%";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 6; i++) {
                statement.setString(i, searchText);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reports.add(mapResultSet(resultSet));
                }
            }
        }
        return reports;
    }

    public boolean existsSimilarOpenReport(String disasterType, String location) throws SQLException {
        String sql = """
                SELECT COUNT(*) 
                FROM disaster_reports
                WHERE TRIM(LOWER(disaster_type)) = TRIM(LOWER(?))
                AND TRIM(LOWER(location)) = TRIM(LOWER(?))
                AND TRIM(LOWER(status)) NOT IN ('closed')
                """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, disasterType);
            statement.setString(2, location);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    public void updateStatusAndPriority(int reportId, String status, String priorityLevel) throws SQLException {
        String sql = "UPDATE disaster_reports SET status = ?, priority_level = ? WHERE report_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setString(2, priorityLevel);
            statement.setInt(3, reportId);
            statement.executeUpdate();
        }
    }

    public void updateStatus(int reportId, String status) throws SQLException {
        String sql = "UPDATE disaster_reports SET status = ? WHERE report_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, reportId);
            statement.executeUpdate();
        }
    }

    public long countAll() throws SQLException {
        return countByWhereClause(null);
    }

    public long countByPriority(String priorityLevel) throws SQLException {
        String sql = "SELECT COUNT(*) FROM disaster_reports WHERE priority_level = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, priorityLevel);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getLong(1) : 0;
            }
        }
    }

    private long countByWhereClause(String whereClause) throws SQLException {
        String sql = "SELECT COUNT(*) FROM disaster_reports" + (whereClause == null ? "" : " WHERE " + whereClause);
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getLong(1) : 0;
        }
    }

    private DisasterReport mapResultSet(ResultSet resultSet) throws SQLException {
        return new DisasterReport(
                resultSet.getInt("report_id"),
                resultSet.getString("report_title"),
                resultSet.getString("disaster_type"),
                resultSet.getString("severity"),
                resultSet.getString("location"),
                resultSet.getString("description"),
                resultSet.getString("reported_by"),
                resultSet.getString("contact_number"),
                resultSet.getString("status"),
                resultSet.getString("priority_level"),
                resultSet.getString("evacuation_advice"),
                resultSet.getString("recommended_resources"),
                resultSet.getString("created_at")
        );
    }
}
