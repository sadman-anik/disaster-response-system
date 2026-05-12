package com.sadman.drs.repository;

import com.sadman.drs.config.DatabaseConnection;
import com.sadman.drs.model.ResponseTask;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for response_tasks table.
 */
public class ResponseTaskRepository {

    public ResponseTask save(ResponseTask task) throws SQLException {
        String sql = """
                INSERT INTO response_tasks
                (report_id, department_id, activity_type, task_description, priority_level, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, task.getReportId());
            statement.setInt(2, task.getDepartmentId());
            statement.setString(3, task.getActivityType());
            statement.setString(4, task.getTaskDescription());
            statement.setString(5, task.getPriorityLevel());
            statement.setString(6, task.getStatus());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    task.setTaskId(keys.getInt(1));
                }
            }
        }
        return task;
    }

    public boolean existsTask(int reportId, int departmentId, String activityType) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM response_tasks
                WHERE report_id = ?
                  AND department_id = ?
                  AND LOWER(activity_type) = LOWER(?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reportId);
            statement.setInt(2, departmentId);
            statement.setString(3, activityType.trim());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    public List<ResponseTask> findAll() throws SQLException {
        String sql = """
                SELECT rt.*, d.department_name, dr.report_title
                FROM response_tasks rt
                JOIN departments d ON rt.department_id = d.department_id
                JOIN disaster_reports dr ON rt.report_id = dr.report_id
                ORDER BY rt.task_id DESC
                """;
        List<ResponseTask> tasks = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                tasks.add(mapResultSet(resultSet));
            }
        }
        return tasks;
    }

    public List<ResponseTask> findByReportId(int reportId) throws SQLException {
        String sql = """
                SELECT rt.*, d.department_name, dr.report_title
                FROM response_tasks rt
                JOIN departments d ON rt.department_id = d.department_id
                JOIN disaster_reports dr ON rt.report_id = dr.report_id
                WHERE rt.report_id = ?
                ORDER BY rt.task_id DESC
                """;
        List<ResponseTask> tasks = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reportId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(mapResultSet(resultSet));
                }
            }
        }
        return tasks;
    }

    public void updateStatus(int taskId, String status) throws SQLException {
        String sql = "UPDATE response_tasks SET status = ? WHERE task_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, taskId);
            statement.executeUpdate();
        }
    }

    public long countOpenTasks() throws SQLException {
        String sql = "SELECT COUNT(*) FROM response_tasks WHERE status <> 'Completed'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getLong(1) : 0;
        }
    }

    private ResponseTask mapResultSet(ResultSet resultSet) throws SQLException {
        return new ResponseTask(
                resultSet.getInt("task_id"),
                resultSet.getInt("report_id"),
                resultSet.getString("report_title"),
                resultSet.getInt("department_id"),
                resultSet.getString("department_name"),
                resultSet.getString("activity_type"),
                resultSet.getString("task_description"),
                resultSet.getString("priority_level"),
                resultSet.getString("status"),
                resultSet.getString("created_at")
        );
    }
}
