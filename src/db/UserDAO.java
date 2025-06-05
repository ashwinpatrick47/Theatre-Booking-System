package db;

import model.User;
import util.PasswordUtils;
import util.PasswordUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static void createTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE,
                    password TEXT,
                    preferredName TEXT
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, preferredName) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String encrypted = PasswordUtils.encrypt(user.getPassword());

            stmt.setString(1, user.getUsername().toLowerCase());
            stmt.setString(2, encrypted);  // Store encrypted password
            stmt.setString(3, user.getPreferredName());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static User getUser(String username, String rawPassword) {
        String encrypted = PasswordUtils.encrypt(rawPassword);

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, encrypted);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("preferredName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.toLowerCase());
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePassword(String username, String newRawPassword) {
        String encrypted = PasswordUtils.encrypt(newRawPassword);
        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, encrypted);
            stmt.setString(2, username);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }









}
