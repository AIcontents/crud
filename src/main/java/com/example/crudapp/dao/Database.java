package com.example.crudapp.dao;

import org.sqlite.Function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

public class Database {
    private static final String URL = "jdbc:sqlite:crudapp.db";

    static {
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
        Connection conn = DriverManager.getConnection(URL);
        Function.create(conn, "REGEXP", new Function() {
            @Override
            protected void xFunc() throws SQLException {
                String expression = value_text(0);
                String value = value_text(1);
                if (value == null) {
                    value = "";
                }
                Pattern pattern = Pattern.compile(expression);
                result(pattern.matcher(value).find() ? 1 : 0);
            }
        });
        return conn;
    }
}
