package com.example.crudapp.controller;

import com.example.crudapp.model.Entity;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntityDialogControllerTest extends JavaFxTestBase {

    @Mock
    private BiConsumer<String, String> alertDisplayer;

    @Captor
    private ArgumentCaptor<String> titleCaptor;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    private EntityDialogController controller;

    @BeforeEach
    public void setUp() {
        controller = new EntityDialogController();
        controller.nameField = new TextField();
        controller.descriptionArea = new TextArea();
        controller.setAlertDisplayer(alertDisplayer);
    }

    @Test
    public void testSetEntity_withNullEntity() {
        controller.setEntity(null);
        assertNotNull(controller.getEntity());
        assertEquals("", controller.getEntity().getName());
        assertTrue(controller.nameField.getText().isEmpty());
        assertTrue(controller.descriptionArea.getText().isEmpty());
    }

    @Test
    public void testSetEntity_withExistingEntity() {
        Entity entity = new Entity(UUID.randomUUID(), "Test", "Desc", null, null);
        controller.setEntity(entity);
        assertEquals("Test", controller.nameField.getText());
        assertEquals("Desc", controller.descriptionArea.getText());
    }

    @Test
    public void testProcessResult_validName() {
        controller.setEntity(null);
        controller.nameField.setText("Valid Name");
        controller.descriptionArea.setText("Some description");
        boolean result = controller.processResult();
        assertTrue(result);
        assertEquals("Valid Name", controller.getEntity().getName());
        assertEquals("Some description", controller.getEntity().getDescription());
        verify(alertDisplayer, never()).accept(anyString(), anyString());
    }

    @Test
    public void testProcessResult_nameTooShort() {
        controller.setEntity(null);
        controller.nameField.setText("a");
        boolean result = controller.processResult();
        assertFalse(result);
        verify(alertDisplayer).accept(titleCaptor.capture(), messageCaptor.capture());
        assertEquals("Validation Error", titleCaptor.getValue());
        assertEquals("Name must be between 3 and 50 characters.", messageCaptor.getValue());
    }

    @Test
    public void testProcessResult_nameTooLong() {
        controller.setEntity(null);
        controller.nameField.setText("a".repeat(51));
        boolean result = controller.processResult();
        assertFalse(result);
        verify(alertDisplayer).accept(anyString(), eq("Name must be between 3 and 50 characters."));
    }
    
    @Test
    public void testProcessResult_nameIsNull() {
        controller.setEntity(null);
        controller.nameField.setText(null);
        boolean result = controller.processResult();
        assertFalse(result);
        verify(alertDisplayer).accept(anyString(), eq("Name must be between 3 and 50 characters."));
    }
}
