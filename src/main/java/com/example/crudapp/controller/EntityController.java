
package com.example.crudapp.controller;

import com.example.crudapp.dao.EntityDAO;
import com.example.crudapp.dao.EntityDAOImpl;
import com.example.crudapp.model.Entity;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class EntityController {

    @FXML
    private TableView<Entity> entityTable;
    @FXML
    private TableColumn<Entity, String> idColumn;
    @FXML
    private TableColumn<Entity, String> nameColumn;
    @FXML
    private TableColumn<Entity, String> descriptionColumn;
    @FXML
    private TextField filterField;
    @FXML
    private Label pageLabel;

    private final EntityDAO entityDAO = new EntityDAOImpl();
    private final ObservableList<Entity> entityList = FXCollections.observableArrayList();
    private int currentPage = 1;
    private static final int PAGE_SIZE = 10;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        filterField.textProperty().addListener((observable, oldValue, newValue) -> filterEntities(newValue));

        loadEntities();
    }

    private void loadEntities() {
        List<Entity> entities = entityDAO.getAll(currentPage, PAGE_SIZE, filterField.getText());
        entityList.setAll(entities);
        entityTable.setItems(entityList);
        pageLabel.setText("Page " + currentPage);
    }

    private void filterEntities(String filter) {
        currentPage = 1;
        loadEntities();
    }

    @FXML
    private void handleNewEntity() {
        // TODO: Implement new entity dialog
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadEntities();
        }
    }

    @FXML
    private void handleNextPage() {
        currentPage++;
        loadEntities();
    }
}
