package com.example.crudapp.dao;

import com.example.crudapp.model.Entity;
import com.example.crudapp.model.ValidationException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityDAOImpl implements EntityDAO {

    @Override
    public void add(Entity entity) throws ValidationException {
        validateEntity(entity);
        String sql = "INSERT INTO entities (id, name, description, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (entity.getId() == null) {
                 entity.setId(UUID.randomUUID()); // Assign a new UUID if not present
            }
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            pstmt.setObject(1, entity.getId());
            pstmt.setString(2, entity.getName());
            pstmt.setString(3, entity.getDescription());
            pstmt.setTimestamp(4, Timestamp.valueOf(entity.getCreatedAt()));
            pstmt.setTimestamp(5, Timestamp.valueOf(entity.getUpdatedAt()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding entity", e);
        }
    }

    @Override
    public void update(Entity entity) throws ValidationException {
        validateEntity(entity);
        String sql = "UPDATE entities SET name = ?, description = ?, updatedAt = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            entity.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getDescription());
            pstmt.setTimestamp(3, Timestamp.valueOf(entity.getUpdatedAt()));
            pstmt.setObject(4, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating entity", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM entities WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting entity", e);
        }
    }

    @Override
    public Optional<Entity> get(UUID id) {
        String sql = "SELECT * FROM entities WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting entity by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Entity> getAll() {
        List<Entity> entities = new ArrayList<>();
        String sql = "SELECT * FROM entities";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                entities.add(mapRowToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all entities", e);
        }
        return entities;
    }

    private Entity mapRowToEntity(ResultSet rs) throws SQLException {
        return new Entity(
                (UUID) rs.getObject("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getTimestamp("createdAt").toLocalDateTime(),
                rs.getTimestamp("updatedAt").toLocalDateTime()
        );
    }

    private void validateEntity(Entity entity) throws ValidationException {
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new ValidationException("Entity name cannot be empty.");
        }
    }
}
