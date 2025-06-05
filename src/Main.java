/*
Student ID: s4106619
Name: Ashwin Patrick

// Y Signup
// Y Login
// Y Read the events from database and display
// Y Events can be booked and bookings can be modified
// Y Bookings are validated
// Y Checkout is validated with confirmation code and day information
// Y Remaining seats of events is updated once an order is made
// Y User can view all orders
// Y User can export orders to file
// Y Admin GUI & admin login implemented
// Y Admin display implemented (no duplicate event titles)
// Y Event disable function implemented
// Y Event adding & deletion functions implemented
// Y Event modification function implemented
// Y Viewing orders of all users implemented
// Y User password update and encryption implemented
// Y JUnit test cases included
// N Design pattern (in addition to MVC)



Note : Can add Users in the database but admin is just a single entity. I haven't added admin into the database
hence he cannot change his username nor his password
Credentials for admin:

Username : admin
password : Admin321

I have added reset events in admin dashboard just in case if the table doesn't seem to display any event
nor if the tickets are exhausted, you can do it to make sure the values from events.dat are re-uploaded into
the database to get fresher pre-existing data from event.dat (the newly added data not from event.dat will be
destroyed however)


*/


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.MainController;

import java.io.IOException;


public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception{
        MainController mainController = new MainController(primaryStage);
        mainController.showLoginView();
    }
    public static void main(String[] args) {
        launch(args);
    }

}
