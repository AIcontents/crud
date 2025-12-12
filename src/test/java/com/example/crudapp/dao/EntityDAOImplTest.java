package com.example.crudapp.dao;

import com.example.crudapp.model.Entity;
import com.example.crudapp.model.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EntityDAOImplTest {

    private EntityDAO entityDAO;

    @BeforeEach
    void setUp() {
        entityDAO = new EntityDAOImpl();
        clearDatabase();
    }

    @AfterEach
    void tearDown() {
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

    private Entity createAndAddEntity(String name, String description) {
        Entity entity = new Entity(UUID.randomUUID(), name, description, null, null);
        entityDAO.add(entity);
        try {
            // Sleep to ensure createdAt and updatedAt can be differentiated
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return entityDAO.get(entity.getId()).orElseThrow();
    }

    @Test
    void testAddAndGetEntity() {
        UUID id = UUID.randomUUID();
        Entity newEntity = new Entity(id, "Test Entity", "Description", null, null);
        entityDAO.add(newEntity);

        Optional<Entity> retrievedEntityOpt = entityDAO.get(id);

        assertTrue(retrievedEntityOpt.isPresent());
        Entity retrievedEntity = retrievedEntityOpt.get();
        assertEquals("Test Entity", retrievedEntity.getName());
        assertEquals("Description", retrievedEntity.getDescription());
        assertNotNull(retrievedEntity.getCreatedAt());
        assertNotNull(retrievedEntity.getUpdatedAt());
        assertEquals(retrievedEntity.getCreatedAt(), retrievedEntity.getUpdatedAt());
    }

    @Test
    void testGetAllEntities() {
        createAndAddEntity("Entity 1", "Desc 1");
        createAndAddEntity("Entity 2", "Desc 2");
        assertEquals(2, entityDAO.getAll().size());
    }

    @Test
    void testUpdateEntity() {
        Entity entity = createAndAddEntity("Original Name", "Original Desc");
        entity.setName("Updated Name");
        entity.setDescription("Updated Desc");
        entityDAO.update(entity);

        Optional<Entity> updatedEntityOpt = entityDAO.get(entity.getId());
        assertTrue(updatedEntityOpt.isPresent());
        Entity updatedEntity = updatedEntityOpt.get();
        assertEquals("Updated Name", updatedEntity.getName());
        assertEquals("Updated Desc", updatedEntity.getDescription());
        assertTrue(updatedEntity.getUpdatedAt().isAfter(updatedEntity.getCreatedAt()));
    }

    @Test
    void testDeleteEntity() {
        Entity entity = createAndAddEntity("To Be Deleted", "Desc");
        entityDAO.delete(entity.getId());
        assertFalse(entityDAO.get(entity.getId()).isPresent());
    }

    @Test
    void testGetNonExistentEntity() {
        assertFalse(entityDAO.get(UUID.randomUUID()).isPresent());
    }

    // Validation Tests
    @Test
    void testAddEntityWithEmptyNameThrowsException() {
        Entity entity = new Entity(UUID.randomUUID(), "", "Description", null, null);
        assertThrows(ValidationException.class, () -> entityDAO.add(entity));
    }

    @Test
    void testAddEntityWithNullNameThrowsException() {
        Entity entity = new Entity(UUID.randomUUID(), null, "Description", null, null);
        assertThrows(ValidationException.class, () -> entityDAO.add(entity));
    }

    @Test
    void testAddEntityWithTooLongNameThrowsException() {
        String longName = "a".repeat(51);
        Entity entity = new Entity(UUID.randomUUID(), longName, "Description", null, null);
        assertThrows(ValidationException.class, () -> entityDAO.add(entity));
    }

    @Test
    void testAddEntityWithTooShortNameThrowsException() {
        Entity entity = new Entity(UUID.randomUUID(), "aa", "Description", null, null);
        assertThrows(ValidationException.class, () -> entityDAO.add(entity));
    }

    @Test
    void testAddEntityWithTooLongDescriptionThrowsException() {
        String longDescription = "a".repeat(257);
        Entity entity = new Entity(UUID.randomUUID(), "Valid Name", longDescription, null, null);
        assertThrows(ValidationException.class, () -> entityDAO.add(entity));
    }

    @Test
    void testSearchAndGetCount() {
        createAndAddEntity("Apple", "A sweet red fruit");
        createAndAddEntity("Banana", "A sweet yellow fruit");
        createAndAddEntity("Carrot", "A crunchy orange vegetable");
        createAndAddEntity("Apple Pie", "A sweet dessert");

        // Test getCount
        assertEquals(4, entityDAO.getCount(null, null));
        assertEquals(3, entityDAO.getCount("sweet", null));
        assertEquals(1, entityDAO.getCount("yellow", null));
        assertEquals(1, entityDAO.getCount("vegetable", null));
        assertEquals(2, entityDAO.getCount("Apple", null));
        assertEquals(1, entityDAO.getCount("Pie", null));

        // Test search with sorting
        List<Entity> results = entityDAO.search("sweet", "name", true, null, 0, 10);
        assertEquals(3, results.size());
        assertEquals("Apple", results.get(0).getName());
        assertEquals("Apple Pie", results.get(1).getName());
        assertEquals("Banana", results.get(2).getName());

        results = entityDAO.search("sweet", "name", false, null, 0, 10);
        assertEquals(3, results.size());
        assertEquals("Banana", results.get(0).getName());

        // Test search with pagination
        results = entityDAO.search("sweet", "name", true, null, 0, 2);
        assertEquals(2, results.size());
        assertEquals("Apple", results.get(0).getName());

        results = entityDAO.search("sweet", "name", true, null, 1, 2);
        assertEquals(1, results.size());
        assertEquals("Banana", results.get(0).getName());
    }
    
    @Test
    void testSearchWithDate() {
        LocalDateTime before = LocalDateTime.now();
        createAndAddEntity("Past", "Created before the time point");
        try { Thread.sleep(20); } catch (InterruptedException e) {}
        LocalDateTime timePoint = LocalDateTime.now();
        try { Thread.sleep(20); } catch (InterruptedException e) {}
        createAndAddEntity("Future", "Created after the time point");
        
        // Test date range
        assertEquals(2, entityDAO.getCount(null, null, before, null));
        assertEquals(1, entityDAO.getCount(null, null, timePoint, null));
        assertEquals(1, entityDAO.getCount(null, null, null, timePoint));
    }


    @Test
    void testModelMethods() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Entity entity = new Entity(id, "Test Name", "Test Description", now, now);

        entity.setId(id);
        assertEquals(id, entity.getId());

        entity.setName("New Name");
        assertEquals("New Name", entity.getName());

        entity.setDescription("New Description");
        assertEquals("New Description", entity.getDescription());

        LocalDateTime later = now.plusDays(1);
        entity.setCreatedAt(later);
        assertEquals(later, entity.getCreatedAt());

        entity.setUpdatedAt(later);
        assertEquals(later, entity.getUpdatedAt());

        // The toString method should return the name
        assertEquals("New Name", entity.toString());
    }
}