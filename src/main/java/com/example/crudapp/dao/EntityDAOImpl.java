package com.example.crudapp.dao;

import com.example.crudapp.model.Entity;
import com.example.crudapp.model.ValidationException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityDAOImpl implements EntityDAO {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void add(Entity entity) throws ValidationException {
        validateEntity(entity);
        String sql = "INSERT INTO entities (id, name, description, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, entity.getId().toString());
            pstmt.setString(2, entity.getName());
            pstmt.setString(3, entity.getDescription());
            pstmt.setString(4, entity.getCreatedAt().format(formatter));
            pstmt.setString(5, entity.getUpdatedAt().format(formatter));
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
            pstmt.setString(3, entity.getUpdatedAt().format(formatter));
            pstmt.setString(4, entity.getId().toString());
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
            pstmt.setString(1, id.toString());
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
            pstmt.setString(1, id.toString());
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

    @Override
    public List<Entity> search(String searchTerm, String sortBy, boolean sortAsc, String filterBy, int page, int pageSize) {
        List<Entity> entities = new ArrayList<>();
        String sortDirection = sortAsc ? "ASC" : "DESC";
        String orderBy;
        switch (sortBy) {
            case "Date":
                orderBy = "createdAt";
                break;
            case "Name (A-Z)":
            case "Name (Z-A)":
                orderBy = "name COLLATE NOCASE";
                break;
            default:
                orderBy = "name COLLATE NOCASE";
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM entities WHERE 1=1 ");

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR description LIKE ?)");
        }

        if ("Letters Only".equals(filterBy)) {
            sql.append(" AND name GLOB '[A-Za-z]*'");
        }

        sql.append(" ORDER BY ").append(orderBy).append(" ").append(sortDirection).append(" LIMIT ? OFFSET ?");

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.trim() + "%";
                pstmt.setString(paramIndex++, searchPattern);
                pstmt.setString(paramIndex++, searchPattern);
            }
            pstmt.setInt(paramIndex++, pageSize);
            pstmt.setInt(paramIndex, (page - 1) * pageSize);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                entities.add(mapRowToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching entities", e);
        }
        return entities;
    }


    @Override
    public int getCount(String searchTerm, String filterBy) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM entities WHERE 1=1 ");

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR description LIKE ?)");
        }

        if ("Letters Only".equals(filterBy)) {
            sql.append(" AND name GLOB '[A-Za-z]*'");
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.trim() + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting entities", e);
        }
        return 0;
    }

    private Entity mapRowToEntity(ResultSet rs) throws SQLException {
        return new Entity(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getString("description"),
                LocalDateTime.parse(rs.getString("createdAt"), formatter),
                LocalDateTime.parse(rs.getString("updatedAt"), formatter)
        );
    }

    private void validateEntity(Entity entity) throws ValidationException {
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new ValidationException("Entity name cannot be empty.");
        }
    }
}
