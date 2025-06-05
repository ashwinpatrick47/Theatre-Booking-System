package controller;

import db.EventDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Event;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.List;
import java.util.Optional;
import util.DateUtils;

public class AdminDashboardController {
    private MainController mainController;
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> titleCol;
    @FXML private TableColumn<Event, String> venueCol;
    @FXML private TableColumn<Event, String> dayCol;
    @FXML private TableColumn<Event, Double> priceCol;
    @FXML private TableColumn<Event, Integer> capacityCol;
    @FXML private TableColumn<Event, String> statusCol;
    @FXML private TableColumn<Event, Void> actionCol;


    @FXML
    public void initialize() {
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        venueCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getVenue()));
        dayCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDay()));
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        capacityCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTotalTickets()).asObject());
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().isEnabled() ? "Enabled" : "Disabled"));
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button toggleButton = new Button();

            {
                toggleButton.setOnAction(e -> {
                    Event event = getTableView().getItems().get(getIndex());
                    boolean newStatus = !event.isEnabled();
                    EventDAO.setEventEnabled(event.getId(), newStatus);
                    event.setEnabled(newStatus);
                    refreshEventTable();  // Refresh to show new status
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Event event = getTableRow().getItem();
                    toggleButton.setText(event.isEnabled() ? "Disable" : "Enable");
                    setGraphic(toggleButton);
                }
            }
        });


        refreshEventTable();
    }

    private void refreshEventTable() {
        List<Event> events = EventDAO.getAllEventsAdmin();
        eventTable.setItems(FXCollections.observableArrayList(events));
    }

    @FXML
    private void handleAddEvent() {
        openAddEventDialog();
    }


    @FXML
    private void handleModifyEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an event to modify.");
            return;
        }

        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Modify Event");
        dialog.setHeaderText("Modify: " + selected.getTitle());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField venueField = new TextField(selected.getVenue());
        TextField dayField = new TextField(selected.getDay());
        TextField priceField = new TextField(String.valueOf(selected.getPrice()));
        TextField capacityField = new TextField(String.valueOf(selected.getTotalTickets()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Venue:"), 0, 0);
        grid.add(venueField, 1, 0);
        grid.add(new Label("Day:"), 0, 1);
        grid.add(dayField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Capacity:"), 0, 3);
        grid.add(capacityField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String rawVenue = venueField.getText().trim();
                    String rawDay = dayField.getText().trim();
                    if (!DateUtils.isValidDay(rawDay)) {
                        showAlert("Invalid Day", "Day must be Mon, Tue, Wed, Thu, Fri, Sat, Sun.");
                        return null;
                    }

                    String normalizedVenue = DateUtils.normalizeTitleOrVenue(rawVenue);
                    String normalizedDay = DateUtils.normalizeDay(rawDay);
                    double price = Double.parseDouble(priceField.getText().trim());
                    int capacity = Integer.parseInt(capacityField.getText().trim());

                    if (normalizedVenue.isEmpty() || normalizedDay.isEmpty()) return null;

                    // Use normalized values in new Event
                    return new Event(selected.getId(), selected.getTitle(), normalizedVenue, normalizedDay, price, selected.getTicketsSold(), capacity, selected.isEnabled());

                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Please enter valid numbers for price and capacity.");
                    return null;
                }
            }
            return null;
        });

        Optional<Event> result = dialog.showAndWait();

        result.ifPresent(updatedEvent -> {
            // Normalize all 3 fields for duplicate checking and update
            String normalizedTitle = DateUtils.normalizeTitleOrVenue(updatedEvent.getTitle());
            String normalizedVenue = DateUtils.normalizeTitleOrVenue(updatedEvent.getVenue());
            String normalizedDay = DateUtils.normalizeDay(updatedEvent.getDay());

            if (EventDAO.duplicateExistsExcludingId(updatedEvent.getId(), normalizedTitle, normalizedVenue, normalizedDay)) {
                showAlert("Duplicate", "Another event exists with the same title, venue, and day.");
            } else {
                // Save changes with normalized values
                EventDAO.modifyEvent(updatedEvent.getId(), normalizedVenue, normalizedDay, updatedEvent.getPrice(), updatedEvent.getTotalTickets());
                refreshEventTable();
            }
        });
    }


    @FXML
    private void handleDeleteEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an event to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "This will delete the event. Bookings won't be refunded. Proceed?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Deletion");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            EventDAO.deleteEventById(selected.getId());
            refreshEventTable();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setMainController(mainController);
            Stage stage = (Stage) eventTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    public void loadGroupedEvents() {
        List<Event> events = db.EventDAO.getAllEventsAdmin();
        eventTable.setItems(FXCollections.observableArrayList(events));
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }




    private void openAddEventDialog() {
        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Add Event");
        dialog.setHeaderText("Enter event details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField();
        TextField venueField = new TextField();
        TextField dayField = new TextField();
        TextField priceField = new TextField();
        TextField capacityField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Venue:"), 0, 1);
        grid.add(venueField, 1, 1);
        grid.add(new Label("Day:"), 0, 2);
        grid.add(dayField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Capacity:"), 0, 4);
        grid.add(capacityField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    // Normalize inputs right away such that there is no duplication
                    String title = DateUtils.normalizeTitleOrVenue(titleField.getText().trim());
                    String venue = DateUtils.normalizeTitleOrVenue(venueField.getText().trim());
                    String rawDay = dayField.getText().trim();
                    if (!DateUtils.isValidDay(rawDay)) {
                        showAlert("Invalid Day", "Day must be Mon, Tue, Wed, Thu, Fri, Sat, Sun");
                        return null;
                    }
                    String day = DateUtils.normalizeDay(rawDay);
                    double price = Double.parseDouble(priceField.getText().trim());
                    int capacity = Integer.parseInt(capacityField.getText().trim());

                    if (title.isEmpty() || venue.isEmpty() || day.isEmpty()) return null;

                    // Reuse normalized values directly
                    if (EventDAO.eventExists(title, venue, day)) {
                        showAlert("Duplicate", "An event with the same title, venue, and day already exists.");
                        return null;
                    } else {
                        EventDAO.addEvent(title, venue, day, price, capacity);
                        refreshEventTable();
                        return null;
                    }

                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Please enter valid numeric values for price and capacity.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }


    @FXML
    private void handleViewAllOrders() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/allOrders.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("All Orders");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleResetEvents() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "This will delete all events and restore from file. Continue?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Reset Events");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            EventDAO.clearTable();
            EventDAO.importFromFile("database/events.dat");
            refreshEventTable();
        }
    }

}
