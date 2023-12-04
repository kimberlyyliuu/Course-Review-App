package edu.virginia.sde.reviews;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AddCourseController {
    @FXML
    private TextField courseMnemonic;
    @FXML
    private TextField courseNumber;
    @FXML
    private TextField courseTitle;

    private final DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
    @FXML
    private Button addButton;
    @FXML
    private Button backToCourseSearchButton;
    @FXML
    private Label errorMessage;

    private final User activeUser = new User("", "");

    public void setActiveUser(User user) {
        activeUser.setUsername(user.getUsername());
        activeUser.setPassword(user.getPassword());
    }

    @FXML
    protected void addCourseInitialize() {
        backToCourseSearchButton.setOnAction(event -> openCourseSearchScene());
        addButton.setOnAction(event -> {
            try {
                handleAddCourse();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private void handleAddCourse() throws SQLException {
        var mnemonicText = courseMnemonic.getText().toUpperCase();
        var number = courseNumber.getText();
        var title = formatCourseTitle(courseTitle.getText());

        dbDriver.connect();
        dbDriver.createTables();

        if (!dbDriver.checkCourseExists(mnemonicText, number, title) && isValidMnemonic(mnemonicText) && isValidCourseNumber(number) && isValidCourseTitle(title)) {
            Course newCourse = new Course(mnemonicText, Integer.parseInt(number), title, 0.0);
            dbDriver.addCourse(newCourse);

            dbDriver.commit();
            openCourseSearchScene();

        } else {
            Platform.runLater(() -> {
                errorMessage.setText("Course Already Exists");
                addCourseInitialize();
            });
            isValidCourseTitle(title);
            isValidMnemonic(mnemonicText);
            isValidCourseNumber(number);
        }
        dbDriver.disconnect();
    }

    private boolean isValidMnemonic(String mnemonicText) {
        if (mnemonicText.length() > 4 || !mnemonicText.matches("[a-zA-Z]+") || mnemonicText.length() < 2) {
            Platform.runLater(() -> errorMessage.setText("Please make sure the entered course mnemonic is between 2 and 4 characters and no added spaces"));
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidCourseNumber(String courseNumber) {
        // Agent: ChatGPT
        // Usage: Asked how to check what's entered are all digits
        if (courseNumber.length() == 4) {
            if (courseNumber.matches("\\d+")) {
                return true;
            } else {
                Platform.runLater(() -> errorMessage.setText("Please make sure the entered course number is 4 digits"));
                return false;
            }
        } else {
            Platform.runLater(() -> errorMessage.setText("Please make sure the entered course number is 4 digits long"));
            return false;
        }
    }

    private boolean isValidCourseTitle(String courseTitle) {
        if (courseTitle.length() > 50 || courseTitle.length() < 1) {
            Platform.runLater(() -> errorMessage.setText("Please make sure the entered course name is between 1 and 50 characters long"));
            return false;
        } else {
            return true;
        }
    }

    private String formatCourseTitle(String courseTitle) {
        // Agent: ChatGPT
        // Usage: Asked how to split string into individual words and capitalize first letter of each word
        String[] words = courseTitle.split("\\s+");
        StringBuilder reformattedTitle = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                reformattedTitle.append(word.substring(0, 1).toUpperCase());
                if (word.length() > 1) {
                    reformattedTitle.append(word.substring(1).toLowerCase());
                }
                reformattedTitle.append(" ");
            }
        }
        return reformattedTitle.toString().trim();
    }

    public void openCourseSearchScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseSearch.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) backToCourseSearchButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("Course Search");
            stage.setScene(newScene);
            stage.show();
            CourseSearchController controller = loader.getController();
            controller.setActiveUser(activeUser);
            controller.courseSearchInitialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
