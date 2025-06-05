package db;

import model.Event;
import util.DateUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventDAO {

    public static void createTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT,
                    venue TEXT,
                    day TEXT,
                    price REAL,
                    ticketsSold INTEGER,
                    totalTickets INTEGER,
                    enabled INTEGER DEFAULT 1,
                    UNIQUE(title, venue, day)
                )
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM events");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void importFromFile(String filepath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath));
             Connection conn = DatabaseConnection.getConnection()) {

            // Skips import if events already exist
            Statement checkStmt = conn.createStatement();
            ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) AS count FROM events");
            if (rs.next() && rs.getInt("count") > 0) {

                return;
            }

            String line;
            PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO events (title, venue, day, price, ticketsSold, totalTickets, enabled)
            VALUES (?, ?, ?, ?, ?, ?, 1)
        """);


            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    ps.setString(1, parts[0].trim());                  // title
                    ps.setString(2, parts[1].trim());                  // venue
                    ps.setString(3, parts[2].trim());                  // day
                    ps.setDouble(4, Double.parseDouble(parts[3].trim())); // price
                    ps.setInt(5, Integer.parseInt(parts[4].trim()));   // ticketsSold
                    ps.setInt(6, Integer.parseInt(parts[5].trim()));   // totalTickets
                    ps.addBatch();
                }
            }

            ps.executeBatch();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM events WHERE enabled = 1")) {

            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("venue"),
                        rs.getString("day"),
                        rs.getDouble("price"),
                        rs.getInt("ticketsSold"),
                        rs.getInt("totalTickets"),
                        rs.getInt("enabled") == 1
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    public static boolean updateTicketsSold(Event event) {
        String sql = "UPDATE events SET ticketsSold = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (event.getTicketsSold() > event.getTotalTickets()) {

                return false;
            }

            ps.setInt(1, event.getTicketsSold());
            ps.setInt(2, event.getId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public static boolean isEventsTableEmpty() {
        String sql = "SELECT COUNT(*) AS count FROM events";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() && rs.getInt("count") == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static Event getEventById(int id) {
        String sql = "SELECT * FROM events WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("venue"),
                        rs.getString("day"),
                        rs.getDouble("price"),
                        rs.getInt("ticketsSold"),
                        rs.getInt("totalTickets"),
                        rs.getInt("enabled") == 1
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void setEventEnabled(int eventId, boolean enabled) {
        String sql = "UPDATE events SET enabled = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enabled ? 1 : 0);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static List<Event> getAllEventsAdmin() {
        List<Event> events = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM events")) {

            while (rs.next()) {
                String title = DateUtils.normalizeTitleOrVenue(rs.getString("title"));
                String venue = DateUtils.normalizeTitleOrVenue(rs.getString("venue"));
                String day = DateUtils.normalizeDay(rs.getString("day"));

                events.add(new Event(
                        rs.getInt("id"),
                        title,
                        venue,
                        day,
                        rs.getDouble("price"),
                        rs.getInt("ticketsSold"),
                        rs.getInt("totalTickets"),
                        rs.getInt("enabled") == 1
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }




    public static void ensureEnabledColumnExists() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if 'enabled' column exists
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(events)");
            boolean columnExists = false;
            while (rs.next()) {
                if ("enabled".equalsIgnoreCase(rs.getString("name"))) {
                    columnExists = true;
                    break;
                }
            }

            // Add the column if it doesn't exist
            if (!columnExists) {
                stmt.executeUpdate("ALTER TABLE events ADD COLUMN enabled INTEGER DEFAULT 1");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static boolean eventExists(String title, String venue, String day) {
        title = DateUtils.normalizeTitleOrVenue(title);
        venue = DateUtils.normalizeTitleOrVenue(venue);
        day = DateUtils.normalizeDay(day);

        String sql = "SELECT COUNT(*) FROM events WHERE title=? AND venue=? AND day=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, venue);
            stmt.setString(3, day);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean duplicateExistsExcludingId(int id, String title, String venue, String day) {
        title = DateUtils.normalizeTitleOrVenue(title);
        venue = DateUtils.normalizeTitleOrVenue(venue);
        day = DateUtils.normalizeDay(day);

        String sql = "SELECT COUNT(*) FROM events WHERE title=? AND venue=? AND day=? AND id != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, venue);
            stmt.setString(3, day);
            stmt.setInt(4, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void addEvent(String title, String venue, String day, double price, int totalTickets) {
        String sql = "INSERT INTO events (title, venue, day, price, ticketsSold, totalTickets, enabled) VALUES (?, ?, ?, ?, 0, ?, 1)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, venue);
            stmt.setString(3, day);
            stmt.setDouble(4, price);
            stmt.setInt(5, totalTickets);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteEventById(int id) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void modifyEvent(int id, String venue, String day, double price, int totalTickets) {
        String sql = "UPDATE events SET venue=?, day=?, price=?, totalTickets=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, venue);
            stmt.setString(2, day);
            stmt.setDouble(3, price);
            stmt.setInt(4, totalTickets);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }









}
