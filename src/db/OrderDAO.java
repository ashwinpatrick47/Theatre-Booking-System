package db;

import model.Order;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrderDAO {


    static {
        createTable();
    }

    private static int orderCount = getLatestOrderNumber();

    public static void createTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
            CREATE TABLE IF NOT EXISTS orders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                orderNum TEXT,
                username TEXT,
                event TEXT,
                quantity INTEGER,
                price REAL,
                datetime TEXT
            )
        """);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void saveOrder(Order order, String username) {
        String sql = "INSERT INTO orders (orderNum, username, event, quantity, price, datetime) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, order.getOrderNumber());
            stmt.setString(2, username);
            stmt.setString(3, order.getEventName());
            stmt.setInt(4, order.getQuantity());
            stmt.setDouble(5, order.getTotalPrice());
            stmt.setString(6, order.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNextOrderNumber() {
        orderCount++;
        return String.format("%04d", orderCount);
    }

    public static List<Order> getOrdersByUsername(String username) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE username = ? ORDER BY datetime DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String orderNum = rs.getString("orderNum");
                String event = rs.getString("event");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");
                LocalDateTime time = LocalDateTime.parse(
                        rs.getString("datetime"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                );
                orders.add(new Order(orderNum, time, event, qty, price, username));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    private static int getLatestOrderNumber() {
        String sql = "SELECT orderNum FROM orders ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Integer.parseInt(rs.getString("orderNum"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<Order> getAllOrdersReversed() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY datetime DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String orderNum = rs.getString("orderNum");
                String event = rs.getString("event");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");
                String username = rs.getString("username");
                LocalDateTime time = LocalDateTime.parse(
                        rs.getString("datetime"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                );
                orders.add(new Order(orderNum, time, event, qty, price, username));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static List<Order> getOrdersForUser(String username) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE username = ? ORDER BY datetime DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String orderNum = rs.getString("orderNum");
                String event = rs.getString("event");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");
                LocalDateTime time = LocalDateTime.parse(
                        rs.getString("datetime"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                );
                orders.add(new Order(orderNum, time, event, qty, price, username));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }


    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY datetime DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String orderNum = rs.getString("orderNum");
                String event = rs.getString("event");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");

                // Parse datetime
                LocalDateTime time = LocalDateTime.parse(
                        rs.getString("datetime"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                );

                String username = rs.getString("username");

                orders.add(new Order(orderNum, time, event, qty, price, username));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }



    public static void ensureOrderTableColumns() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("PRAGMA table_info(orders)");
            List<String> existingCols = new ArrayList<>();
            while (rs.next()) {
                existingCols.add(rs.getString("name"));
            }

            if (!existingCols.contains("orderNumber")) {
                stmt.executeUpdate("ALTER TABLE orders ADD COLUMN orderNumber TEXT");

            }

            if (!existingCols.contains("timestamp")) {
                stmt.executeUpdate("ALTER TABLE orders ADD COLUMN timestamp TEXT");

            }

            if (!existingCols.contains("username")) {
                stmt.executeUpdate("ALTER TABLE orders ADD COLUMN username TEXT");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void cleanUpOrdersTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Step 1: Create a new temporary table
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS orders_cleaned (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                orderNum TEXT,
                username TEXT,
                event TEXT,
                quantity INTEGER,
                price REAL,
                datetime TEXT
            )
        """);


            stmt.execute("""
            INSERT INTO orders_cleaned (orderNum, username, event, quantity, price, datetime)
            SELECT DISTINCT orderNum, username, event, quantity, price, datetime
            FROM orders
            WHERE datetime IS NOT NULL AND orderNum IS NOT NULL
        """);
            stmt.execute("DROP TABLE orders");


            stmt.execute("ALTER TABLE orders_cleaned RENAME TO orders");



        } catch (Exception e) {

            e.printStackTrace();
        }
    }








}
