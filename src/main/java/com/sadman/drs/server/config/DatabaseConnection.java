package com.sadman.drs.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
        new DatabaseInitializer().initialize();
        new DatabaseSeeder().seedDefaults();
    }

    static String getProperty(String key) {
        loadProperties();
        return properties.getProperty(key);
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
