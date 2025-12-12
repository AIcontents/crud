package com.example.crudapp.controller;

import com.example.crudapp.dao.EntityDAO;
import com.example.crudapp.dao.EntityDAOImpl;
import com.example.crudapp.model.Entity;
import com.example.crudapp.model.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MainController {

    private static final int PAGE_SIZE = 10;

    @FXML
    private ListView<Entity> entityListView;
    @FXML
    private VBox entityDetailsVBox;
    @FXML
    private Label nameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label createdAtLabel;
    @FXML
    private Label updatedAtLabel;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;
    @FXML
    private ComboBox<String> sortComboBox;

    private final EntityDAO entityDAO = new EntityDAOImpl();
    private final ObservableList<Entity> entityList = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        entityListView.setItems(entityList);
        VBox.setVgrow(entityListView, javafx.scene.layout.Priority.ALWAYS);

        entityListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showEntityDetails(newValue));

        entityListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleEditEntity();
            }
        });

        sortComboBox.setItems(FXCollections.observableArrayList("Date", "Name (A-Z)", "Name (Z-A)"));
        sortComboBox.getSelectionModel().selectFirst();
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateView());

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> updateView());

        updateView();
    }

    private void updateView() {
        String searchTerm = searchField.getText();
        String sortBy = sortComboBox.getValue();
        boolean sortAsc = !"Name (Z-A)".equals(sortBy);

        int currentPage = pagination.getCurrentPageIndex() + 1;

        int totalItems = entityDAO.getCount(searchTerm);
        int pageCount = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (pageCount == 0) {
            pageCount = 1;
        }

        pagination.setPageCount(pageCount);

        entityList.setAll(entityDAO.search(searchTerm, sortBy, sortAsc, currentPage, PAGE_SIZE));
        entityDetailsVBox.setVisible(false);
    }

    private void showEntityDetails(Entity entity) {
        if (entity != null) {
            entityDetailsVBox.setVisible(true);
            nameLabel.setText(entity.getName());
            descriptionLabel.setText(entity.getDescription());
            createdAtLabel.setText(entity.getCreatedAt() != null ? entity.getCreatedAt().format(formatter) : "N/A");
            updatedAtLabel.setText(entity.getUpdatedAt() != null ? entity.getUpdatedAt().format(formatter) : "N/A");
        } else {
            entityDetailsVBox.setVisible(false);
        }
    }

    @FXML
    private void handleSearch() {
        pagination.setCurrentPageIndex(0);
        updateView();
    }

    @FXML
    private void handleNewEntity() {
        showEditDialog(null);
    }

    @FXML
    private void handleEditEntity() {
        Entity selectedEntity = entityListView.getSelectionModel().getSelectedItem();
        if (selectedEntity != null) {
            showEditDialog(selectedEntity);
        }
    }

    @FXML
    private void handleDeleteEntity() {
        Entity selectedEntity = entityListView.getSelectionModel().getSelectedItem();
        if (selectedEntity != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Entity");
            alert.setContentText("Are you sure you want to delete the selected entity: " + selectedEntity.getName() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                entityDAO.delete(selectedEntity.getId());
                // Go to previous page if the last item on a page is deleted
                if (entityList.size() == 1 && pagination.getCurrentPageIndex() > 0) {
                    pagination.setCurrentPageIndex(pagination.getCurrentPageIndex() - 1);
                }
                updateView();
            }
        }
    }

    private void showEditDialog(Entity entity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crudapp/EntityDialog.fxml"));
            DialogPane pane = loader.load();

            EntityDialogController controller = loader.getController();
            controller.setEntity(entity);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle(entity == null ? "New Entity" : "Edit Entity");

            final Button okButton = (Button) pane.lookupButton(pane.getButtonTypes().get(0));
            okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                if (!controller.processResult()) {
                    event.consume();
                }
            });

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    boolean isNewEntity = (entity == null);
                    if (isNewEntity) {
                        entityDAO.add(controller.getEntity());
                    } else {
                        entityDAO.update(controller.getEntity());
                    }
                    updateView();
                    if (isNewEntity) {
                        int totalItems = entityDAO.getCount(searchField.getText());
                        int lastPage = (int) Math.ceil((double) totalItems / PAGE_SIZE) - 1;
                        if (lastPage < 0) {
                            lastPage = 0;
                        }
                        pagination.setCurrentPageIndex(lastPage);
                    }
                } catch (ValidationException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation Error");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
