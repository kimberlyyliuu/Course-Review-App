package edu.virginia.sde.reviews;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AddCourseController {
    @FXML
    private TextField courseMnemonic;
    @FXML
    private TextField courseNumber;
    @FXML
    private TextField courseTitle;
    @FXML
    private Label MnemonicLabel;
    @FXML
    private Label numberLabel;
    @FXML
    private Label titlelabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private TextField ratingTextfield;
    @FXML
    private Label commentsLabel;
    @FXML
    private TextField commentsTextfield;

    private ListView<Course> courseListView;

    private DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");

    @FXML
    private Button addButton;
    @FXML
    private Button backtoCourseSearchButton;
    @FXML
    private Label errorMessage;

    @FXML
    protected void addCourseInitialize(){
        backtoCourseSearchButton.setOnAction(event -> openCourseSearchScene());
        addButton.setOnAction(event -> {
            try {
                handleAddCourse();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private void handleAddCourse() throws SQLException {
        var mnemonicText = courseMnemonic.getText();
        var number = courseNumber.getText();
        var title = formatCourseTitle(courseTitle.getText());

        try{
            dbDriver.connect();
            dbDriver.createTables();

            if(!dbDriver.checkCourseExists(mnemonicText, number, title) && isValidMnemonic(mnemonicText) && isValidCourseNumber(number) && isValidCourseTitle(title)){
                Course newCourse = new Course(mnemonicText, Integer.parseInt(number), title, 0.0);
                dbDriver.addCourse(newCourse);

                dbDriver.commit();
                openCourseSearchScene();

            }else{
                Platform.runLater(() -> {
                    errorMessage.setText("Course Already Exists");
                    addCourseInitialize();
                });
                isValidCourseTitle(title);
                isValidMnemonic(mnemonicText);
                isValidCourseNumber(number);
            }
        }catch (SQLException e){
            throw e;
        } finally {
            try{
                dbDriver.disconnect();
            }
            catch(SQLException e){
                throw e;
            }
        }

    }

    private boolean isValidMnemonic(String mnemonicText) {
        if(mnemonicText.length() > 4 || !mnemonicText.matches("[a-zA-Z]+")){
            Platform.runLater(() -> {
                errorMessage.setText("Please make sure the entered course mnemonic is no more than 4 characters that are letters. No spaces");
            });
            return false;
        }else{
            return true;
        }
    }

    private boolean isValidCourseNumber(String courseNumber) {
        // Agent: ChatGPT
        // Usage: Asked how to check what's entered are all digits
        if(courseNumber.length() == 4) {
            if (courseNumber.matches("\\d+")) {
                return true;
            } else {
                Platform.runLater(() -> {
                    errorMessage.setText("Please make sure the entered course number is 4 digits");
                });
                return false;
            }
        }
        else{
            Platform.runLater(() -> {
                errorMessage.setText("Please make sure the entered course number is 4 digits long");
            });
            return false;
        }
    }

    private boolean isValidCourseTitle(String courseTitle) {
        if(courseTitle.length() > 50){
            Platform.runLater(() -> {
                errorMessage.setText("Please make sure the entered course name is no more than 50 characters long");
            });
            return false;
        }
        else {
            return true;
        }
    }

    private String formatCourseTitle(String courseTitle){
        // Agent: ChatGPT
        // Usage: Asked how to split string into individual words and capitalize first letter of each word
        String[] words = courseTitle.split("\\s+");
        StringBuilder reformattedTitle = new StringBuilder();

        for(String word : words){
            if(word.length() > 0){
                reformattedTitle.append(word.substring(0, 1).toUpperCase());
                if(word.length() > 1){
                    reformattedTitle.append(word.substring(1).toLowerCase());
                }
                reformattedTitle.append(" ");
            }
        }
        return reformattedTitle.toString().trim();
    }

    private void openCourseSearchScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseSearch.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) backtoCourseSearchButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("Course Reviews");
            stage.setScene(newScene);
            stage.show();
            CourseSearchController controller = loader.getController();
            controller.courseSearchInitialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
