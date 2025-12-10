package com.example.crudapp.dao;

import com.example.crudapp.model.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntityDAOImpl implements EntityDAO {

    @Override
    public void add(Entity entity) {
        String sql = "INSERT INTO entity (name, description) VALUES (?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDescription());
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Entity entity) {
        String sql = "UPDATE entity SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDescription());
            ps.setInt(3, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM entity WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Entity get(int id) {
        String sql = "SELECT * FROM entity WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Entity(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Entity> getAll(int page, int pageSize, String filter) {
        List<Entity> entities = new ArrayList<>();
        String sql = "SELECT * FROM entity";
        if (filter != null && !filter.isEmpty()) {
            sql += " WHERE name LIKE ?";
        }
        sql += " LIMIT ? OFFSET ?";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            int paramIndex = 1;
            if (filter != null && !filter.isEmpty()) {
                ps.setString(paramIndex++, "%" + filter + "%");
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, (page - 1) * pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entities.add(new Entity(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entities;
    }
}
