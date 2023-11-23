package edu.virginia.sde.reviews;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.stage.Stage;

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

    private ListView<Course> courseListView;
    private DatabaseDriver dbDriver;

    @FXML
    private Button addButton;
    @FXML
    private Button backtoCourseSearchButton;

    @FXML
    protected void addCourseInitialize(){
        backtoCourseSearchButton.setOnAction(event -> openCourseSearchScene());
    }
    /**
     * Validates the user input for adding a new course.
     * Shows an error message if the input is invalid.
     *
     * @param subject The subject mnemonic entered by the user.
     * @param number  The course number entered by the user.
     * @param title   The course title entered by the user.
     * @return True if the input is valid, false otherwise.
     */
    private boolean validateCourseInput(String subject, String number, String title) {
        // Implement validation logic for subject, number, and title
        // Show appropriate error messages for invalid input
        // Return true if input is valid, false otherwise

        return false;
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
