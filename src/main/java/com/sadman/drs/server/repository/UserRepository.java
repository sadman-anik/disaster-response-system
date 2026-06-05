package com.sadman.drs.server.repository;

import com.sadman.drs.model.User;
import com.sadman.drs.server.config.DatabaseConnection;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

/**
 * Repository used for authenticating users and seeding initial credentials.
 */
public class UserRepository {

    public UserRepository() {
    }

    public User findByUsername(String username) throws SQLException {
        String query = "SELECT user_id, username, role, password_hash FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSet(resultSet);
                }
            }
        }
        return null;
    }

    public User authenticate(String username, String password) throws SQLException {
        User storedUser = findByUsername(username);
        if (storedUser == null) {
            return null;
        }
        String hashed = hashPassword(password);
        if (storedUser.getPasswordHash().equals(hashed)) {
            return storedUser;
        }
        return null;
    }

    public User registerUser(String username, String password, String role) throws SQLException {
        if (findByUsername(username) != null) {
            return null;
        }
        if (!"REPORTER".equals(role)
                && !"ASSESSMENT_OFFICER".equals(role)
                && !"RESOURCE_OFFICER".equals(role)
                && !"DEPARTMENT_OFFICER".equals(role)
                && !"AUDITOR".equals(role)) {
            throw new IllegalArgumentException("Role must be REPORTER, ASSESSMENT_OFFICER, RESOURCE_OFFICER, DEPARTMENT_OFFICER, or AUDITOR.");
        }
        return save(new User(username, role, hashPassword(password)));
    }

    public void createDefaultUsers() throws SQLException {
        createDefaultUserIfMissing("admin", "ADMIN", "Admin@123");
        createDefaultUserIfMissing("reporter", "REPORTER", "Reporter@123");
        createDefaultUserIfMissing("assessment_officer", "ASSESSMENT_OFFICER", "Assessment@123");
        createDefaultUserIfMissing("resource_officer", "RESOURCE_OFFICER", "Resource@123");
        createDefaultUserIfMissing("department_officer", "DEPARTMENT_OFFICER", "Department@123");
        createDefaultUserIfMissing("auditor", "AUDITOR", "Auditor@123");
    }

    private void createDefaultUserIfMissing(String username, String role, String password) throws SQLException {
        if (findByUsername(username) == null) {
            save(new User(username, role, hashPassword(password)));
        }
    }

    private User save(User user) throws SQLException {
        String insertSql = "INSERT INTO users (username, role, password_hash) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getRole());
            statement.setString(3, user.getPasswordHash());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
            }
            return user;
        }
    }

    private User mapResultSet(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("user_id"),
                resultSet.getString("username"),
                resultSet.getString("role"),
                resultSet.getString("password_hash")
        );
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Unable to hash password", exception);
        }
    }
}
