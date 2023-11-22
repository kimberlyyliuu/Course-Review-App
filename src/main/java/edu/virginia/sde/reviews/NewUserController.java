package edu.virginia.sde.reviews;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class NewUserController {
    @FXML
    private TextField newUsernameField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Button createUserButton;
    @FXML
    private Label userAlreadyExists;
    @FXML
    private Label userCreated;
    @FXML
    private Label passRequirements;
    @FXML
    private Label invalidPassword;
    @FXML
    private Button exitButton;
    @FXML
    protected void newUserInitialize() {
        System.out.println("you made it to new user scene, initializing");
        exitButton.setOnAction(event -> Platform.exit());
        createUserButton.setOnAction(event -> createNewUser());


    }
    private void createNewUser(){
        var username = newUsernameField.getText();
        var password = newPasswordField.getText();
        try {
            DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
            dbDriver.connect();
            dbDriver.createTables();
            System.out.println("tables created");
            if (dbDriver.checkUserExists(username)) {
                userAlreadyExists.setText("Username already exists");
                System.out.println("Username already exists");
                dbDriver.disconnect();
                newUserInitialize();
            }
            else if (!dbDriver.checkUserExists(username) && dbDriver.isValidPassword(password)){
                User newUser = new User(username, password);
                dbDriver.addUser(newUser);
                dbDriver.commit();
                dbDriver.disconnect();
                userCreated.setText("New User Created");
                System.out.println("Database updated with new user");
                returnLogInScene();
            }
            else if (!dbDriver.checkUserExists(username) && !dbDriver.isValidPassword(password)){
                invalidPassword.setText("Invalid Password");
                System.out.println("Password incorrect");
                dbDriver.disconnect();
                newUserInitialize();
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    private void returnLogInScene() throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) userCreated.getScene().getWindow();
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}