package edu.virginia.sde.reviews;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private void initialize(){
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

            if(dbDriver.checkUserPassword(username, password)){
                dbDriver.disconnect();
                openCourseReviewScene();
            }
            else{
                //display error message
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
    private void handleCreateNewUser() throws SQLException {
        var username = usernameField.getText();
        var password = passwordField.getText();

        try{
            DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
            dbDriver.connect();

            if(dbDriver.checkUserExists(username)){
                //error message
            }
            else{
                User newUser = new User(username, password);

                dbDriver.addUser(newUser);
                dbDriver.disconnect();

                openCourseReviewScene();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

    }



    private void openCourseReviewScene(){
      var stage = (Stage) loginButton.getScene().getWindow();
      stage.close();
      //idk
    }

}
