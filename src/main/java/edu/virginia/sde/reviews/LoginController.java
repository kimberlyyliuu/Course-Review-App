package edu.virginia.sde.reviews;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button newUserButton;
    @FXML
    private Button exitButton;
    @FXML
    private Label errorMessage;
    private User activeUser1 = new User("", "");;

    private DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");

    @FXML
    private void initialize(){
        exitButton.setOnAction(event -> Platform.exit());
        loginButton.setOnAction(event -> {
            try {
                handleLogin();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        newUserButton.setOnAction(event -> {
            try {
                handleCreateNewUser();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }


    /**
     * When login button is pressed, checks if user is in database. If not, an error message is shown in the application. If they are in the database and the correct password is
     * given, then the login is successful and course review scene is shown.
     */
    private void handleLogin() throws SQLException {
        // Agent: ChatGPT
        // Usage: Making sure disconnections are handled correctly
        var username = usernameField.getText();
        var password = passwordField.getText();

        try {
            dbDriver.connect();
            dbDriver.createTables();

            if (!dbDriver.checkUserExists(username)) {
                Platform.runLater(() -> {
                    errorMessage.setText("Username does not exist");
                    initialize();
                });
                dbDriver.disconnect();
            } else if (dbDriver.checkUserExists(username) && dbDriver.checkUserPassword(username,password)) {
               try {
                   dbDriver.commit();
                   activeUser1 = new User(username, password);
                   //System.out.println(activeUser.getUsername());
                   Platform.runLater(() -> {
                       errorMessage.setText("Logging In...");
                       // Introduce a delay before switching scenes
                       PauseTransition delay = new PauseTransition(Duration.seconds(2)); // Adjust the duration as needed
                       delay.setOnFinished(event -> {
                           openCourseReviewScene();
                       });
                       delay.play();

                   });
               }
               catch(SQLException e){
                   e.printStackTrace();
               }
               finally {
                   try{
                       dbDriver.disconnect();
                   }
                   catch (SQLException e){
                       e.printStackTrace();
                   }
               }
            } else if (dbDriver.checkUserExists(username) && !dbDriver.checkUserPassword(username,password)) {
                Platform.runLater(() -> {
                    errorMessage.setText("Invalid Password");
//                    try {
//                        dbDriver.disconnect();
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
                    initialize();
                });
            }
        } catch (SQLException e) {
            throw e;
        } finally{
            dbDriver.disconnect();
        }
    }


    /**
     * When create new user button is pressed, checks to see if the username is already used. If it is, a message will be shown in the application saying the username exists already.
     * If not, the new user will be created successfully, and then the course review scene will be shown.
     * @throws SQLException
     */
    //This was corrected and now new user is on another scene
    private void handleCreateNewUser() throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("New-User.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) newUserButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("Create New User");
            stage.setScene(newScene);
            stage.show();
            NewUserController controller = loader.getController();
            controller.newUserInitialize();
            System.out.println("Stage set, switching now");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openCourseReviewScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseSearch.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) errorMessage.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("Course Search");
            stage.setScene(newScene);
            stage.show();
            CourseSearchController controller = loader.getController();
            controller.setActiveUser(new User(usernameField.getText() , passwordField.getText()));
            controller.courseSearchInitialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
