package com.example.crudapp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:crudapp.db";

    static {
        try {
            // The following line is not needed for modern JDBC drivers,
            // but it can be kept for compatibility.
            // Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS entities ("
                    + "id TEXT PRIMARY KEY, "
                    + "name TEXT NOT NULL, "
                    + "description TEXT, "
                    + "createdAt TEXT, "
                    + "updatedAt TEXT)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
