package edu.virginia.sde.reviews;

import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

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
    private Label passRequirements;
    @FXML
    private Button backToLoginButton;
    @FXML
    private Label errorMessage;
    @FXML
    private Button exitButton;
    private User activeUser;

    private DatabaseDriver dbDriver;
    @FXML
    protected void newUserInitialize() {
        exitButton.setOnAction(event -> Platform.exit());
        createUserButton.setOnAction(event -> createNewUser());
        backToLoginButton.setOnAction(event -> {
            try {
                returnLogInScene();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }
    private void createNewUser() {
        var username = newUsernameField.getText();
        var password = newPasswordField.getText();

        try {
            dbDriver = new DatabaseDriver("course_app.sqlite");
            dbDriver.connect();
            dbDriver.createTables();

            if (dbDriver.checkUserExists(username)) {
                Platform.runLater(() -> {
                    errorMessage.setText("Username already exists");
                    try {
                        dbDriver.disconnect();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    newUserInitialize();
                });
            } else if (username == null || username.isEmpty()){
                    Platform.runLater(() -> {
                        errorMessage.setText("Username cannot be empty");
                        try {
                            dbDriver.disconnect();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        newUserInitialize();
                    });
                }
             else if (!dbDriver.checkUserExists(username) && dbDriver.isValidPassword(password)) {
                User newUser = new User(username, password);
                activeUser = new User(username, password);
                dbDriver.addUser(newUser);
                dbDriver.commit();
                dbDriver.disconnect();

                Platform.runLater(() -> {
                    errorMessage.setText("New User Created. Redirecting to login screen...");
                    // Introduce a delay before switching scenes
                    PauseTransition delay = new PauseTransition(Duration.seconds(3)); // Adjust the duration as needed
                    delay.setOnFinished(event -> {
                        try {
                            returnLogInScene();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    delay.play();
                });
            } else if (!dbDriver.checkUserExists(username) && !dbDriver.isValidPassword(password)) {
                Platform.runLater(() -> {
                    errorMessage.setText("Invalid Password Format");
                    try {
                        dbDriver.disconnect();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    newUserInitialize();
                });
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setActiveUser(User user){
        activeUser.setUsername(user.getUsername());
        activeUser.setPassword(user.getPassword());
    }

    private void returnLogInScene() throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) errorMessage.getScene().getWindow();
            stage.setScene(newScene);
            LoginController controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}