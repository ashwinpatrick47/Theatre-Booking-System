package controller;

import db.OrderDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Order;

public class AllOrdersController {
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> orderNumCol;
    @FXML private TableColumn<Order, String> userCol;
    @FXML private TableColumn<Order, String> eventCol;
    @FXML private TableColumn<Order, Integer> qtyCol;
    @FXML private TableColumn<Order, Double> priceCol;
    @FXML private TableColumn<Order, String> timestampCol;


    @FXML
    public void initialize() {
        orderNumCol.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        eventCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("formattedTimestamp"));

        orderTable.setItems(FXCollections.observableArrayList(OrderDAO.getAllOrders()));
    }
}
