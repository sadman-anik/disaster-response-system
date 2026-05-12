package com.sadman.drs.repository;

import com.sadman.drs.config.DatabaseConnection;
import com.sadman.drs.model.AssessmentResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for disaster_assessments table.
 */
public class AssessmentRepository {

    public AssessmentResult save(AssessmentResult assessmentResult) throws SQLException {
        String sql = """
                INSERT INTO disaster_assessments
                (report_id, damage_level, people_affected, infrastructure_damage,
                 priority_score, priority_level, assessment_summary)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, assessmentResult.getReportId());
            statement.setString(2, assessmentResult.getDamageLevel());
            statement.setInt(3, assessmentResult.getPeopleAffected());
            statement.setBoolean(4, assessmentResult.isInfrastructureDamage());
            statement.setInt(5, assessmentResult.getPriorityScore());
            statement.setString(6, assessmentResult.getPriorityLevel());
            statement.setString(7, assessmentResult.getAssessmentSummary());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    assessmentResult.setAssessmentId(keys.getInt(1));
                }
            }
        }
        return assessmentResult;
    }

    public List<AssessmentResult> findAll() throws SQLException {
        String sql = """
                SELECT da.*, dr.report_title
                FROM disaster_assessments da
                JOIN disaster_reports dr ON da.report_id = dr.report_id
                ORDER BY da.assessment_id DESC
                """;
        List<AssessmentResult> assessments = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                assessments.add(new AssessmentResult(
                        resultSet.getInt("assessment_id"),
                        resultSet.getInt("report_id"),
                        resultSet.getString("report_title"),
                        resultSet.getString("damage_level"),
                        resultSet.getInt("people_affected"),
                        resultSet.getBoolean("infrastructure_damage"),
                        resultSet.getInt("priority_score"),
                        resultSet.getString("priority_level"),
                        resultSet.getString("assessment_summary"),
                        resultSet.getString("created_at")
                ));
            }
        }
        return assessments;
    }
}
