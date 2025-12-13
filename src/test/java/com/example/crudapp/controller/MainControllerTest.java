package com.example.crudapp.controller;

import com.example.crudapp.dao.EntityDAO;
import com.example.crudapp.model.Entity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MainControllerTest extends JavaFxTestBase {

    @Mock
    private EntityDAO entityDAO;

    @Mock
    private Supplier<Dialog<ButtonType>> dialogSupplier;

    @InjectMocks
    private MainController controller;

    private ListView<Entity> entityListView;
    private ObservableList<Entity> entityObservableList;

    @BeforeEach
    public void setUp() {
        entityObservableList = FXCollections.observableArrayList();
        entityListView = new ListView<>(entityObservableList);
        controller.entityListView = entityListView;
    }

    @Test
    public void testInitialize() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity(UUID.randomUUID(), "Test Entity", "Description", null, null));
        when(entityDAO.search(anyString(), anyString(), anyBoolean(), anyString(), anyInt(), anyInt())).thenReturn(entities);

        controller.initialize();

        verify(entityDAO).search(anyString(), anyString(), anyBoolean(), anyString(), anyInt(), anyInt());
        assertEquals(1, entityListView.getItems().size());
        assertEquals("Test Entity", entityListView.getItems().get(0).getName());
    }

    @Test
    public void testHandleDeleteEntity_WhenConfirmed() {
        Entity entity = new Entity(UUID.randomUUID(), "Test Entity", "Description", null, null);
        entityListView.getItems().add(entity);
        entityListView.getSelectionModel().select(0);

        Dialog<ButtonType> dialog = mock(Dialog.class);
        when(dialog.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
        when(dialogSupplier.get()).thenReturn(dialog);
        controller.setDialogSupplier(dialogSupplier);

        controller.handleDeleteEntity();

        verify(entityDAO).delete(entity.getId());
        verify(entityDAO, times(2)).search(anyString(), anyString(), anyBoolean(), anyString(), anyInt(), anyInt());
        assertTrue(entityListView.getItems().isEmpty());
    }
    
    @Test
    public void testHandleDeleteEntity_WhenCancelled() {
        Entity entity = new Entity(UUID.randomUUID(), "Test Entity", "Description", null, null);
        entityListView.getItems().add(entity);
        entityListView.getSelectionModel().select(0);

        Dialog<ButtonType> dialog = mock(Dialog.class);
        when(dialog.showAndWait()).thenReturn(Optional.of(ButtonType.CANCEL));
        when(dialogSupplier.get()).thenReturn(dialog);
        controller.setDialogSupplier(dialogSupplier);

        controller.handleDeleteEntity();

        verify(entityDAO, never()).delete(any(UUID.class));
        assertEquals(1, entityListView.getItems().size());
    }
}
