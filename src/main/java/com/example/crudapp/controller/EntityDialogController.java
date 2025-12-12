package com.example.crudapp.controller;

import com.example.crudapp.model.Entity;
import com.example.crudapp.model.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class EntityDialogController {

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    private Entity entity;

    public void setEntity(Entity entity) {
        this.entity = entity;
        if (entity != null) {
            nameField.setText(entity.getName());
            descriptionArea.setText(entity.getDescription());
        } else {
            this.entity = new Entity(null, "", null, null, null); // Create a new one
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean processResult() {
        String name = nameField.getText();
        String description = descriptionArea.getText();

        if (name == null || name.trim().length() < 3 || name.trim().length() > 50) {
            showAlert("Validation Error", "Name must be between 3 and 50 characters.");
            return false;
        }

        entity.setName(name);
        entity.setDescription(description);
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(nameField.getScene().getWindow());
        alert.showAndWait();
    }
}
