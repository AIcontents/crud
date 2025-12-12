package com.example.crudapp.controller;

import com.example.crudapp.dao.EntityDAO;
import com.example.crudapp.model.Entity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(MockitoExtension.class)
public class MainControllerTest {

    @Mock
    private EntityDAO entityDAO;

    @Mock
    private Supplier<Dialog<ButtonType>> dialogSupplier;

    @InjectMocks
    private MainController controller;

    private ListView<Entity> entityListView;

    @Start
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crudapp/MainView.fxml"));
        loader.setControllerFactory(param -> controller);
        AnchorPane root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
        entityListView = (ListView<Entity>) root.lookup("#entityListView");
    }

    @Test
    public void testInitialize() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity(UUID.randomUUID(), "Test Entity", "Description", null, null));
        when(entityDAO.search(anyString(), anyString(), anyBoolean(), anyString(), anyInt(), anyInt())).thenReturn(entities);

        controller.initialize();

        verify(entityDAO).search(anyString(), anyString(), anyBoolean(), anyString(), anyInt(), anyInt());
        ObservableList<Entity> items = entityListView.getItems();
        assert items.size() == 1;
        assert items.get(0).getName().equals("Test Entity");
    }

    @Test
    public void testHandleDeleteEntity() {
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
    }

}
