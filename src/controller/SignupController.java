package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import util.PasswordUtils;
import db.UserDAO;


public class SignupController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private Label messageLabel;

    private MainController mainController;

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }


    @FXML
    public void handleSignup() {
        String username = usernameField.getText().trim().toLowerCase();
        String password = passwordField.getText().trim();
        String name = nameField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            messageLabel.setText("All fields are required.");
            return;
        }

        if (UserDAO.userExists(username)) {
            messageLabel.setText("Username already exists. Choose another.");
            return;
        }

        // Create user and save
        User user = new User(username, password, name);
        boolean success = UserDAO.addUser(user);

        if (success) {
            messageLabel.setText("Signup successful! Returning to login...");
            mainController.showLoginView();
        } else {
            messageLabel.setText("Something went wrong during signup.");
        }
    }



    @FXML
    private void handleBackToLogin() {
        if (mainController != null) {
            mainController.showLoginView();
        }
    }

}
