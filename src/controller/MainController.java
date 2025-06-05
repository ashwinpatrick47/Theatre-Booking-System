package controller;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import db.DatabaseConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;
import model.Event;

import java.util.List;

public class MainController {

    private Stage stage;
    private User currentUser;

    public MainController(Stage stage) {
        this.stage = stage;

        db.UserDAO.createTable();
        db.EventDAO.createTable();


        try {
            db.EventDAO.ensureEnabledColumnExists();
        } catch (Exception e) {
            e.printStackTrace();
        }


        db.OrderDAO.createTable();



        if (db.EventDAO.isEventsTableEmpty()) {
            db.EventDAO.clearTable();
            db.EventDAO.importFromFile("database/events.dat");
        }
    }


    public boolean login(String username, String password) {
        if (username.equals("admin") && password.equals("Admin321")) {
            currentUser = new User("admin", "Admin321", "Admin");
            showAdminDashboard();
            return true;
        }

        User user = db.UserDAO.getUser(username, password);
        if (user != null) {
            currentUser = user;
            showDashboard();
            return true;
        }

        return false;
    }

    public void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setMainController(this);
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSignupView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Sign_In.fxml"));
            Parent root = loader.load();
            SignupController controller = loader.getController();
            controller.setMainController(this);
            stage.setScene(new Scene(root));
            stage.setTitle("Sign Up");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setMainController(this);

            List<Event> events = db.EventDAO.getAllEvents();  // only enabled events
            controller.loadUserandEvents(currentUser, events);

            stage.setScene(new Scene(root));
            stage.setTitle("User Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin_dashboard.fxml"));
            Parent root = loader.load();
            AdminDashboardController controller = loader.getController();
            controller.loadGroupedEvents();  // full event list
            controller.setMainController(this);
            controller.loadGroupedEvents();

            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addUser(User user) {
        return db.UserDAO.addUser(user);
    }

    public boolean userExists(String username) {
        return db.UserDAO.userExists(username);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
        showLoginView();  // Redirect to login on logout
    }
}
