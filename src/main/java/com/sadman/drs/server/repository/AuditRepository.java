package com.sadman.drs.server.repository;

import com.sadman.drs.model.AuditRecord;
import com.sadman.drs.server.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for audit_events table.
 */
public class AuditRepository {

    public AuditRecord save(AuditRecord record) throws SQLException {
        String sql = """
                INSERT INTO audit_events
                (entity_type, entity_id, entity_label, action_type, username, change_details)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, record.getEntityType());
            statement.setInt(2, record.getEntityId());
            statement.setString(3, record.getEntityLabel());
            statement.setString(4, record.getActionType());
            statement.setString(5, record.getUsername());
            statement.setString(6, record.getChangeDetails());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    record.setAuditId(keys.getInt(1));
                }
            }
        }
        return record;
    }

    public List<AuditRecord> findAll() throws SQLException {
        String sql = "SELECT * FROM audit_events ORDER BY created_at DESC";
        List<AuditRecord> events = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                events.add(mapResultSet(resultSet));
            }
        }
        return events;
    }

    public List<AuditRecord> search(String keyword) throws SQLException {
        String sql = """
                SELECT * FROM audit_events
                WHERE entity_type LIKE ?
                  OR entity_label LIKE ?
                  OR action_type LIKE ?
                  OR username LIKE ?
                  OR change_details LIKE ?
                ORDER BY created_at DESC
                """;
        List<AuditRecord> events = new ArrayList<>();
        String searchText = "%" + keyword + "%";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                statement.setString(i, searchText);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    events.add(mapResultSet(resultSet));
                }
            }
        }
        return events;
    }

    private AuditRecord mapResultSet(ResultSet resultSet) throws SQLException {
        return new AuditRecord(
                resultSet.getInt("audit_id"),
                resultSet.getString("entity_type"),
                resultSet.getInt("entity_id"),
                resultSet.getString("entity_label"),
                resultSet.getString("action_type"),
                resultSet.getString("username"),
                resultSet.getString("change_details"),
                resultSet.getString("created_at")
        );
    }
}
