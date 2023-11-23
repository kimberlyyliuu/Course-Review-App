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

public class CourseSearchController {
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
    private ListView<Course> courseListView;
    private DatabaseDriver dbDriver;
    @FXML
    private Button exitButton;
    @FXML
    private Button addCourseButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button myReviewsButton;
    @FXML
    private Button logoutButton;
    @FXML
    protected void courseSearchInitialize(){
        loadCourses();
        exitButton.setOnAction(event -> Platform.exit());
        addCourseButton.setOnAction(event -> {
            try {
                addCourse();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        searchButton.setOnAction(event1 -> submitSearch());
        });
        myReviewsButton.setOnAction(event -> showMyReviews());
        logoutButton.setOnAction(event -> setlogoutButton());
    }
    //This method loads the update ListView
    private void loadCourses() {
        try {
            DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
            dbDriver.connect();
            dbDriver.createTables();
//            Course test = new Course("123456", 1234, "4", 4);
//            dbDriver.addCourse(test);
//            List<Course> courses = new ArrayList<>();
//            courses = dbDriver.getAllCourses();
//            ObservableList<Course> observableCourseList = FXCollections.observableList(courses);
//            ListView<Course> courselistView = new ListView<>(observableCourseList);

            List<Course> courses = dbDriver.getAllCourses(); // Retrieve the list of courses from the database
            // Update the existing courseListView with the new data
            ObservableList<Course> observableCourseList = FXCollections.observableList(courses);
            courseListView.setItems(observableCourseList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //Adds a course to the database which should update in the ListView as well immediately
    @FXML
    private void addCourse() throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
        dbDriver.connect();
        dbDriver.createTables();
        int number = Integer.parseInt(courseNumber.getText());
        String mnemonic = courseMnemonic.getText();
        String title = courseTitle.getText();

        Course newCourse = new Course(mnemonic, number, title);
        dbDriver.addCourse(newCourse);

        // Update the ListView
        loadCourses();
    }

    /**
     * Updates the list of courses displayed in the courseListView based on the search criteria.
     * Searches by subject, number, and title, and updates the view accordingly.
     */
    private void submitSearch(){
        String subject = courseMnemonic.getText().trim();
        String number = courseNumber.getText().trim();
        String title = courseTitle.getText().trim();

        // Implement search logic based on subject, number, and title
        // Update the courseListView with the search results
        // You may need to modify loadCourses or create a new method to handle search results
        // For simplicity, assuming all courses are shown in this example

        loadCourses(); // Reload all courses for simplicity
    }

    /**
     * Navigates to the "My Reviews" scene.
     */
    @FXML
    private void showMyReviews() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("myReviews.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) myReviewsButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("My Reviews");
            stage.setScene(newScene);
            stage.show();
            MyReviewsController controller = loader.getController();
            controller.myReviewsIntialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setlogoutButton() {
        // Implement logic to log the user out and return to the Log-In Screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) logoutButton.getScene().getWindow(); //NOT SURE ABT THIS LINE
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

