
package com.example.crudapp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static Connection connection;

    public static void init() {
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:crudapp");
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS entity (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50), description VARCHAR(255))");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        Database.connection = connection;
    }
}
