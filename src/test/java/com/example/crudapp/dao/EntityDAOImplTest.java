
package com.example.crudapp.dao;

import com.example.crudapp.model.Entity;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityDAOImplTest {

    private Connection connection;
    private final EntityDAO entityDAO = new EntityDAOImpl();

    @BeforeAll
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Database.setConnection(connection);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE entity (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50), description VARCHAR(255))");
        }
    }

    @AfterAll
    void tearDown() throws SQLException {
        connection.close();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE entity");
        }
    }

    @Test
    void testAddAndGetEntity() {
        Entity entity = new Entity(0, "Test Name", "Test Description");
        entityDAO.add(entity);
        assertNotEquals(0, entity.getId());

        Entity retrievedEntity = entityDAO.get(entity.getId());
        assertNotNull(retrievedEntity);
        assertEquals("Test Name", retrievedEntity.getName());
    }

    @Test
    void testGetAll() {
        entityDAO.add(new Entity(0, "Name1", "Desc1"));
        entityDAO.add(new Entity(0, "Name2", "Desc2"));

        List<Entity> entities = entityDAO.getAll(1, 10, null);
        assertEquals(2, entities.size());
    }

    @Test
    void testUpdate() {
        Entity entity = new Entity(0, "Original Name", "Original Desc");
        entityDAO.add(entity);

        entity.setName("Updated Name");
        entityDAO.update(entity);

        Entity updatedEntity = entityDAO.get(entity.getId());
        assertEquals("Updated Name", updatedEntity.getName());
    }

    @Test
    void testDelete() {
        Entity entity = new Entity(0, "To Be Deleted", "Delete me");
        entityDAO.add(entity);

        entityDAO.delete(entity.getId());
        assertNull(entityDAO.get(entity.getId()));
    }
}

