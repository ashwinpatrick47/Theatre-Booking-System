package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.CartItem;

import java.util.List;

public class CartController {

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> eventCol;
    @FXML private TableColumn<CartItem, Integer> quantityCol;
    @FXML private TableColumn<CartItem, Void> actionCol;
    @FXML private TableColumn<CartItem, String> venueCol;
    @FXML private TableColumn<CartItem, String> dayCol;
    @FXML private TableColumn<CartItem, Double> priceCol;




    private ObservableList<CartItem> cartItems;
    private DashboardController dashboardController;

    public void initCart(List<CartItem> cart, DashboardController controller) {
        this.dashboardController = controller;
        this.cartItems = FXCollections.observableArrayList(cart);
        cartTable.setItems(cartItems);

        eventCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getEvent().getTitle())
        );

        quantityCol.setCellFactory(col -> new TableCell<>() {
            private final Spinner<Integer> spinner = new Spinner<>(1, 100, 1);

            {
                spinner.setEditable(true);
                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    item.setQuantity(newVal);
                });
            }



            @Override
            protected void updateItem(Integer qty, boolean empty) {
                super.updateItem(qty, empty);
                if (empty || getIndex() >= cartItems.size()) {
                    setGraphic(null);
                } else {
                    CartItem item = cartItems.get(getIndex());
                    spinner.getValueFactory().setValue(item.getQuantity());
                    setGraphic(spinner);
                }
            }
        });

        venueCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getEvent().getVenue()));

        dayCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getEvent().getDay()));

        priceCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleDoubleProperty(
                        cell.getValue().getEvent().getPrice() * cell.getValue().getQuantity()
                ).asObject());


        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button removeBtn = new Button(" Remove");

            {
                removeBtn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cartItems.remove(item);
                    dashboardController.removeFromCart(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeBtn);
            }
        });
    }

    @FXML
    private void checkoutFromCart() {
        if (dashboardController != null) {
            dashboardController.handleCheckout();
            Stage stage = (Stage) cartTable.getScene().getWindow();
            stage.close();
        }
    }

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    @FXML
    private void handleBackToDashboard() {

        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }

}
