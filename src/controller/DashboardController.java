package controller;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.CartItem;
import model.Event;
import model.User;
import util.PasswordUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {
    private User currentUser;
    private MainController mainController;



    @FXML private Label welcomeLabel;
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> titleCol;
    @FXML private TableColumn<Event, String> venueCol;
    @FXML private TableColumn<Event, String> dayCol;
    @FXML private TableColumn<Event, Double> priceCol;
    @FXML private TableColumn<Event, Integer> totalCol;
    @FXML private TableColumn<Event, Integer> remainingCol;
    @FXML private TableColumn<Event, Void> actionCol;

    private final List<CartItem> cart = new ArrayList<>();

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        venueCol.setCellValueFactory(new PropertyValueFactory<>("venue"));
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalTickets"));

        remainingCol.setCellValueFactory(cell -> {
            int remaining = cell.getValue().getTotalTickets() - cell.getValue().getTicketsSold();
            return new SimpleIntegerProperty(remaining).asObject();
        });

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Spinner<Integer> spinner = new Spinner<>(1, 10, 1);
            private final Button addButton = new Button("Add");
            private final HBox box = new HBox(5, spinner, addButton);

            {
                box.setAlignment(Pos.CENTER_LEFT);
                box.setPadding(new Insets(5));

                addButton.setOnAction(e -> {
                    Event event = getTableView().getItems().get(getIndex());
                    int newQty = spinner.getValue();


                    int alreadyInCart = cart.stream()
                            .filter(item -> item.getEvent().equals(event))
                            .mapToInt(CartItem::getQuantity)
                            .sum();

                    int remaining = event.getTotalTickets() - event.getTicketsSold();

                    if (alreadyInCart + newQty > remaining) {
                        showAlert(" Overbooking",
                                "Only " + remaining + " tickets available.\nYou already have " + alreadyInCart + " in your cart.");
                        return;
                    }

                    CartItem existing = cart.stream()
                            .filter(item -> item.getEvent().equals(event))
                            .findFirst()
                            .orElse(null);

                    if (existing != null) {
                        existing.setQuantity(existing.getQuantity() + newQty);
                    } else {
                        cart.add(new CartItem(event, newQty));
                    }

                    showAlert("Added to Cart", event.getTitle() + " x" + newQty + " added to your cart.");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Event event = getTableView().getItems().get(getIndex());

                List<String> weekDays = List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
                String today = java.time.LocalDate.now().getDayOfWeek().toString().substring(0, 3);
                int todayIndex = weekDays.indexOf(today.substring(0, 1).toUpperCase() + today.substring(1).toLowerCase());
                int eventDayIndex = weekDays.indexOf(event.getDay());

                boolean isDisabled = (eventDayIndex < todayIndex);

                spinner.setDisable(isDisabled);
                addButton.setDisable(isDisabled);

                setGraphic(box);
            }

        });
    }

    public void loadUserandEvents(User user, List<Event> events) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getPreferredName() + "!");
        eventTable.setItems(FXCollections.observableArrayList(events));
    }

    @FXML
    public void handleCheckout() {
        if (cart.isEmpty()) {
            showAlert("Cart is Empty", "Please add events to your cart before checkout.");
            return;
        }

        // Validate availability from DB
        for (CartItem item : cart) {
            Event freshEvent = db.EventDAO.getEventById(item.getEvent().getId());
            int available = freshEvent.getTotalTickets() - freshEvent.getTicketsSold();
            if (item.getQuantity() > available) {
                showAlert("Sold Out", "This event is currently sold out.");
                return;
            }
        }

        double totalPrice = cart.stream().mapToDouble(item -> item.getEvent().getPrice() * item.getQuantity()).sum();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Your total is $" + totalPrice + ".\nDo you want to proceed to payment?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Checkout");
        confirm.showAndWait();

        if (confirm.getResult() != ButtonType.YES) return;

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Payment Verification");
        inputDialog.setHeaderText("Enter your 6-digit confirmation code");
        inputDialog.setContentText("Code:");
        String code = inputDialog.showAndWait().orElse("");

        if (!code.trim().matches("\\d{6}")) {
            showAlert("Invalid Code", "Enter a valid 6-digit numeric code.");
            return;
        }

        List<String> weekDays = List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        String today = java.time.LocalDate.now().getDayOfWeek().toString().substring(0, 3);
        int todayIndex = weekDays.indexOf(today.substring(0, 1).toUpperCase() + today.substring(1).toLowerCase());

        for (CartItem item : cart) {
            String eventDay = item.getEvent().getDay();
            int eventIndex = weekDays.indexOf(eventDay);
            if (eventIndex < todayIndex) {
                showAlert("Invalid Booking", "You cannot book " + item.getEvent().getTitle() + " on " + eventDay);
                return;
            }
        }

        String orderNum = db.OrderDAO.getNextOrderNumber();
        LocalDateTime timestamp = LocalDateTime.now();


        for (CartItem item : cart) {
            Event cartEvent = item.getEvent();

            //  Fetch the latest event info from DB
            Event dbEvent = db.EventDAO.getEventById(cartEvent.getId());

            if (dbEvent == null) {
                showAlert("Error", "Event not found in database.");
                return;
            }

            int available = dbEvent.getTotalTickets() - dbEvent.getTicketsSold();
            if (item.getQuantity() > available) {
                showAlert("Overbooking",
                        "Only " + available + " tickets left for " + dbEvent.getTitle());
                return;
            }


            dbEvent.setTicketsSold(dbEvent.getTicketsSold() + item.getQuantity());
            db.EventDAO.updateTicketsSold(dbEvent);

            db.OrderDAO.saveOrder(
                    new model.Order(orderNum, timestamp, dbEvent.getTitle(), item.getQuantity(), item.getQuantity() * dbEvent.getPrice()),
                    currentUser.getUsername()  //  pass the username!
            );

        }


        cart.clear();

//  Refresh with new values from DB
        List<Event> updatedEvents = db.EventDAO.getAllEvents();
        eventTable.setItems(FXCollections.observableArrayList(updatedEvents));


        showAlert(" Checkout Success", "Your tickets have been booked.");
    }

    @FXML
    private void viewOrderHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/orderHistory.fxml"));
            Parent root = loader.load();
            OrderHistoryController controller = loader.getController();
            controller.loadOrdersForUser(currentUser.getUsername());  //  Pass username

            Stage stage = new Stage();
            stage.setTitle("Order History");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void openCartView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/cart.fxml"));
            Parent root = loader.load();
            CartController controller = loader.getController();
            controller.setDashboardController(this);

            Stage stage = new Stage();
            stage.setTitle("Your Cart");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CartItem> getCart() {
        return cart;
    }

    public void removeFromCart(CartItem item) {
        cart.remove(item);
    }

    @FXML
    private void openCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/cart.fxml"));
            Parent root = loader.load();

            CartController controller = loader.getController();

            //  THIS IS CRUCIAL: pass the cart list and controller reference
            controller.initCart(cart, this);

            Stage stage = new Stage();
            stage.setTitle("Your Cart");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();

            loginController.setMainController(mainController); // if you have one passed in

            Stage stage = (Stage) eventTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleChangePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your new password:");
        dialog.setContentText("New Password:");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword.length() < 4) {
                showAlert("Password too short!", "Minimum 4 characters required.");
                return;
            }

            //String encrypted = PasswordUtils.encrypt(newPassword);
            boolean updated = db.UserDAO.updatePassword(currentUser.getUsername(), newPassword);
            if (updated) {
                showAlert("Success", "Password changed successfully.");
            } else {
                showAlert("Error", "Failed to change password.");
            }
        });
    }




}
