package com.example.crudapp.controller;

import com.example.crudapp.model.Entity;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntityDialogControllerTest {

    @InjectMocks
    private EntityDialogController controller;

    private TextField nameField;
    private TextArea descriptionArea;

    @BeforeEach
    public void setUp() {
        nameField = new TextField();
        descriptionArea = new TextArea();
        controller = new EntityDialogController();
        setFields(controller, nameField, descriptionArea);
    }

    private void setFields(EntityDialogController controller, TextField nameField, TextArea descriptionArea) {
        try {
            java.lang.reflect.Field nameFieldVar = EntityDialogController.class.getDeclaredField("nameField");
            nameFieldVar.setAccessible(true);
            nameFieldVar.set(controller, nameField);

            java.lang.reflect.Field descriptionAreaVar = EntityDialogController.class.getDeclaredField("descriptionArea");
            descriptionAreaVar.setAccessible(true);
            descriptionAreaVar.set(controller, descriptionArea);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSetEntity_withNullEntity() {
        controller.setEntity(null);
        assertNotNull(controller.getEntity());
        assertEquals("", controller.getEntity().getName());
    }

    @Test
    public void testSetEntity_withExistingEntity() {
        Entity entity = new Entity(null, "Test", "Desc", null, null);
        controller.setEntity(entity);
        assertEquals("Test", nameField.getText());
        assertEquals("Desc", descriptionArea.getText());
    }

    @Test
    public void testProcessResult_validName() {
        controller.setEntity(null);
        nameField.setText("Valid Name");
        descriptionArea.setText("Some description");
        assertTrue(controller.processResult());
        assertEquals("Valid Name", controller.getEntity().getName());
        assertEquals("Some description", controller.getEntity().getDescription());
    }

    @Test
    public void testProcessResult_nameTooShort() {
        controller.setEntity(null);
        nameField.setText("a");
        assertFalse(controller.processResult());
    }

    @Test
    public void testProcessResult_nameTooLong() {
        controller.setEntity(null);
        nameField.setText("a".repeat(51));
        assertFalse(controller.processResult());
    }
}
