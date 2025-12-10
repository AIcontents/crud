package com.example.crudapp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    // Added DB_CLOSE_DELAY=-1 to prevent the in-memory database from being deleted
    // as long as the virtual machine is alive.
    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS entities ("
                    + "id UUID PRIMARY KEY, "
                    + "name VARCHAR(255) NOT NULL, "
                    + "description VARCHAR(1024), "
                    + "createdAt TIMESTAMP, "
                    + "updatedAt TIMESTAMP)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            // This is a critical failure, so we throw a RuntimeException
            // to stop the application from starting if the DB can't be initialized.
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
