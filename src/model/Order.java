package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {
    private String orderNumber;
    private LocalDateTime timestamp;
    private String EventName;
    private int quantity;
    private double totalPrice;
    private String username;  //

    public Order(String orderNumber, LocalDateTime timestamp, String EventName, int quantity, double totalPrice, String username) {
        this.orderNumber = orderNumber;
        this.timestamp = timestamp;
        this.EventName = EventName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.username = username;
    }


    public Order(String orderNumber, LocalDateTime timestamp, String eventName, int quantity, double totalPrice) {
        this(orderNumber, timestamp, eventName, quantity, totalPrice, ""); // default username
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getEventName() {
        return EventName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getUsername() {
        return username;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
