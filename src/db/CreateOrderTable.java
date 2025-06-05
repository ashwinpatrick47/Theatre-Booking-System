package db;

import java.sql.Connection;
import java.sql.Statement;

public class CreateOrderTable {
    public static void main(String[] args) {
        final String TABLE_NAME = "orders";

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "orderNum TEXT, "
                + "username TEXT, "
                + "event TEXT, "
                + "quantity INTEGER, "
                + "price REAL, "
                + "datetime TEXT"
                + ")";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Table 'orders' created successfully.");
        } catch (Exception e) {
            System.out.println("Failed to create table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
