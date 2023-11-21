package edu.virginia.sde.reviews;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
        newUserButton.setOnAction(event -> handleCreateNewUser());
    }

    private void handleLogin(){
        var username = usernameField.getText();
        var password = passwordField.getText();
        var userExists = validateUser(username, password);

        if(userExists){
            openCourseReviewScene();
        }
        else{
            //error message shown about invalid username or password or create new user
        }
    }

    private void handleCreateNewUser(){
        var username = usernameField.getText();
        var password = passwordField.getText();
        var usernameExists = checkUserExists(username);

        if(usernameExists){
            //error message about username already existing
        }
        else{
            addUserToDatabase(username, password);
            openCourseReviewScene();
        }

    }

    private boolean validateUser(String username, String password){
        return true;
    }

    private boolean checkUserExists(String username){
        return true;
    }

    private void addUserToDatabase(String username, String password){

    }

    private void openCourseReviewScene(){
      var stage = (Stage) loginButton.getScene().getWindow();
      stage.close();
      //idk
    }

}
