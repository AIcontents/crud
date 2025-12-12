package com.example.crudapp.dao;

import com.example.crudapp.model.Entity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EntityDAOImplTest {

    private EntityDAO entityDAO;

    @BeforeEach
    void setUp() {
        entityDAO = new EntityDAOImpl();
        // Ensure the table is clean before each test
        clearDatabase();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        clearDatabase();
    }

    private void clearDatabase() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM entities");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear database for test", e);
        }
    }

    @Test
    void testAddAndGetEntity() {
        UUID id = UUID.randomUUID();
        Entity newEntity = new Entity(id, "Test Entity", "Description", null, null);
        entityDAO.add(newEntity);

        Optional<Entity> retrievedEntity = entityDAO.get(id);

        assertTrue(retrievedEntity.isPresent());
        assertEquals("Test Entity", retrievedEntity.get().getName());
        assertNotNull(retrievedEntity.get().getCreatedAt());
        assertNotNull(retrievedEntity.get().getUpdatedAt());
    }

    @Test
    void testGetAllEntities() {
        Entity entity1 = new Entity(UUID.randomUUID(), "Entity 1", "Desc 1", null, null);
        Entity entity2 = new Entity(UUID.randomUUID(), "Entity 2", "Desc 2", null, null);
        entityDAO.add(entity1);
        entityDAO.add(entity2);

        List<Entity> allEntities = entityDAO.getAll();
        assertEquals(2, allEntities.size());
    }

    @Test
    void testUpdateEntity() {
        UUID id = UUID.randomUUID();
        Entity entity = new Entity(id, "Original Name", "Original Desc", null, null);
        entityDAO.add(entity);

        entity.setName("Updated Name");
        entityDAO.update(entity);

        Optional<Entity> updatedEntity = entityDAO.get(id);
        assertTrue(updatedEntity.isPresent());
        assertEquals("Updated Name", updatedEntity.get().getName());
        // Check that updatedAt is after createdAt
        assertTrue(updatedEntity.get().getUpdatedAt().isAfter(updatedEntity.get().getCreatedAt()));
    }

    @Test
    void testDeleteEntity() {
        UUID id = UUID.randomUUID();
        Entity entity = new Entity(id, "To Be Deleted", "Desc", null, null);
        entityDAO.add(entity);

        entityDAO.delete(id);

        Optional<Entity> deletedEntity = entityDAO.get(id);
        assertFalse(deletedEntity.isPresent());
    }

    @Test
    void testAddEntityWithEmptyNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Entity(UUID.randomUUID(), "", "Description", null, null));
    }

    @Test
    void testAddEntityWithTooLongNameThrowsException() {
        String longName = "a".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> new Entity(UUID.randomUUID(), longName, "Description", null, null));
    }

    @Test
    void testAddEntityWithTooShortNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Entity(UUID.randomUUID(), "aa", "Description", null, null));
    }

    @Test
    void testAddEntityWithTooLongDescriptionThrowsException() {
        String longDescription = "a".repeat(256);
        assertThrows(IllegalArgumentException.class, () -> new Entity(UUID.randomUUID(), "Valid Name", longDescription, null, null));
    }
}
