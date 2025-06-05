package db;
import java.sql.*;

public class TestOrderTable {
    public static void main(String[] args) {
        try (Connection conn = db.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='orders'")) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
