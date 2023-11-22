package edu.virginia.sde.reviews;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
    private Label userNotExist;
    @FXML
    private Label userAlreadyExists;
    @FXML
    private Label tryAgain;
    @FXML
    private Label invalidPassword;

    @FXML
    private void initialize(){
        exitButton.setOnAction(event -> Platform.exit());
        loginButton.setOnAction(event -> handleLogin());
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
    private void handleLogin(){
        var username = usernameField.getText();
        var password = passwordField.getText();

        try{
            DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
            dbDriver.connect();
            dbDriver.createTables();        //create tables if not exists
            dbDriver.commit();

            if(!dbDriver.checkUserExists(username)){
                userNotExist.setText("Username does not exist");
            }
            else if (dbDriver.checkUserPassword(username, password)){ //queries for matching pass and returns false otherwise
                dbDriver.disconnect();
                openCourseReviewScene();
            }
            else{
               invalidPassword.setText("Invalid Password");
            }
        } catch (SQLException e){
            e.printStackTrace();
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

            System.out.println("Stage set, switching now");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void openCourseReviewScene(){
      var stage = (Stage) loginButton.getScene().getWindow();
      stage.close();
      //idk
    }

}
