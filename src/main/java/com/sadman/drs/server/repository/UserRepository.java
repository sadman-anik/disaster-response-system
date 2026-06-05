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
                    return new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("role"),
                            resultSet.getString("password_hash")
                    );
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
        if (!"VIEWER".equals(role) && !"RESPONDER".equals(role)) {
            throw new IllegalArgumentException("Role must be VIEWER or RESPONDER.");
        }
        return save(new User(username, role, hashPassword(password)));
    }

    public void createDefaultUsers() throws SQLException {
        if (findByUsername("admin") != null) {
            return;
        }

        save(new User("admin", "ADMIN", hashPassword("Admin@123")));
        save(new User("responder", "RESPONDER", hashPassword("Responder@123")));
        save(new User("viewer", "VIEWER", hashPassword("Viewer@123")));
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
