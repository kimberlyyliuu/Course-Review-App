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

    private boolean validMnemonic = false, validCourseNumber = false, validCourseTitle = false;

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

            checkMnemonicReqs(mnemonicText);
            checkCourseNumberReqs(number);
            checkCourseTitleReqs(title);

            if(!dbDriver.checkCourseExists(mnemonicText, number, title) && validMnemonic && validCourseNumber && validCourseTitle){
                Course newCourse = new Course(mnemonicText, Integer.parseInt(number), title, 0.0);
                dbDriver.addCourse(newCourse);

                dbDriver.commit();

                // TODO: Figure out how to display this message in fxml--currently getting null pointers
//
//                Platform.runLater(() -> {
//                    errorMessage.setText("Course Added!");
//                    addCourseInitialize();
//                });
            }else{
                Platform.runLater(() -> {
                    errorMessage.setText("Course Already Exists");
                    addCourseInitialize();
                });
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

    private void checkMnemonicReqs(String mnemonicText) {
        if(mnemonicText.length() > 4){
            // TODO: Create displayable error message on screen saying enter viable mnemonic
            validMnemonic = false;
        }else{
            validMnemonic = true;
        }
    }

    private void checkCourseNumberReqs(String courseNumber) {
        // Agent: ChatGPT
        // Usage: Asked how to check what's entered are all digits
        if(courseNumber.length() == 4) {
            if (courseNumber.matches("\\d+")) {
                validCourseNumber = true;
            } else {
                // TODO: Create displayable error message on screen saying enter viable courseNumber (must be 4 DIGITS)
                validCourseNumber = false;
            }
        }
        else{
            // TODO: Create displayable error message on screen saying enter viable courseNumber (must be 4 digits)
            validCourseNumber = false;
        }
    }

    private  void checkCourseTitleReqs(String courseTitle) {
        if(courseTitle.length() > 50){
            // TODO: Create displayable error message on screen saying enter viable courseName (has to be less than 50);
            validCourseTitle = false;
        }
        else {
            validCourseTitle = true;
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
