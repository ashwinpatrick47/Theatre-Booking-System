package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;


public class LoginController {

    @FXML
    private Label messageLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox toggleButton;

    @FXML
    private TextField usernameField;

    private MainController mainController;
    private TextField visiblePasswordField = new TextField();

    public void initialize()
    {
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);


        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        VBox parent = (VBox) passwordField.getParent();
        int index = parent.getChildren().indexOf(passwordField);
        parent.getChildren().add(index + 1, visiblePasswordField);
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }


    @FXML
    void goToSignup(ActionEvent event) {
        if(mainController != null)
        {
            mainController.showSignupView();
        }

    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().toLowerCase();
        String password = passwordField.getText().trim();

        boolean success = mainController.login(username, password);
        if (!success) {
            messageLabel.setText("Invalid username or password.");
        }
    }


    @FXML
    void togglePasswordVisibility(ActionEvent event) {
        boolean show = toggleButton.isSelected();

        passwordField.setVisible(!show);
        passwordField.setManaged(!show);
        visiblePasswordField.setVisible(show);
        visiblePasswordField.setManaged(show);
    }

    @FXML
    void passwordFieldKeyType(KeyEvent event) {
        // placeholder
    }

    private String getPasswordInput() {
        return toggleButton.isSelected() ? visiblePasswordField.getText() : passwordField.getText();
    }

    @FXML
    private void handleExit() {
        javafx.application.Platform.exit();
    }


}
