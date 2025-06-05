package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class AdminController {

    @FXML
    private void handleViewOrders() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "View Orders clicked.");
        alert.showAndWait();
    }

    @FXML
    private void handleViewUsers() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "View Users clicked.");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) new javafx.scene.control.Button().getScene().getWindow();
        stage.close();
    }
}
