package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import model.Order;
import db.OrderDAO;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;



public class OrderHistoryController {

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> orderNumberCol;
    @FXML private TableColumn<Order, String> timestampCol;
    @FXML private TableColumn<Order, String> eventCol;
    @FXML private TableColumn<Order, Integer> qtyCol;
    @FXML private TableColumn<Order, Double> priceCol;

    @FXML
    public void initialize() {
        orderNumberCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getOrderNumber()));
        timestampCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFormattedTimestamp()));
        eventCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEventName()));
        qtyCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getTotalPrice()).asObject());

        orderTable.setItems(FXCollections.observableArrayList(db.OrderDAO.getAllOrdersReversed()));
    }

    public void loadOrdersForUser(String username) {
        orderTable.setItems(FXCollections.observableArrayList(db.OrderDAO.getOrdersForUser(username)));
    }

    @FXML
    private void handleExportOrders() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Order History");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("orders.txt");

        File file = fileChooser.showSaveDialog(orderTable.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Order order : orderTable.getItems()) {
                    writer.println("Order Number: " + order.getOrderNumber());
                    writer.println("Date & Time: " + order.getTimestamp());
                    writer.println("Event: " + order.getEventName());
                    writer.println("Quantity: " + order.getQuantity());
                    writer.println("Total Price: $" + order.getTotalPrice());
                    writer.println("------------------------------");
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION, " Export successful!");
                alert.setHeaderText(null);
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, " Failed to export file.");
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        }
    }
    @FXML
    private void handleClose() {
        orderTable.getScene().getWindow().hide();
    }


}
