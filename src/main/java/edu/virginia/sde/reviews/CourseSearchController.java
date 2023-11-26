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
    private ListView<Course> courseListView;
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

    private DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
    @FXML
    protected void courseSearchInitialize(){
        // Agent: ChatGPT
        // Usage: Handling disconnections correctly
        try {
            dbDriver.connect();
            dbDriver.createTables();

            loadCourses();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally{
            try{
                dbDriver.disconnect();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
        exitButton.setOnAction(event -> Platform.exit());
        searchButton.setOnAction(event1 -> {
            try {
                submitSearch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        addCourseButton.setOnAction(event -> addCourseScreen());
        myReviewsButton.setOnAction(event -> showMyReviews());
        logoutButton.setOnAction(event -> setlogoutButton());

        //Clickable courses
        courseListView.setOnMouseClicked(event -> {
            Course selectedCourse = courseListView.getSelectionModel().getSelectedItem();
            if(selectedCourse!=null){
                try {
                    handleCourseClick(selectedCourse);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void handleCourseClick(Course selectedCourse) throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("courseReviewScreen.fxml"));
            Parent root = loader.load();
            CourseReviewController controller = loader.getController();
            controller.setData(selectedCourse);

            Stage stage = (Stage) courseListView.getScene().getWindow();
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.setTitle("Course Review");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This method loads the update ListView
    private void loadCourses() throws SQLException {
        try {
            List<Course> courses = dbDriver.getAllCourses(); // Retrieve the list of courses from the database

            // Agent: ChatGPT
            // Usage: Help making courses clickable
            courseListView.setCellFactory(param -> new ListCell<Course>() {
                @Override
                protected void updateItem(Course item, boolean empty){
                    super.updateItem(item, empty);

                    if(empty || item == null){
                        setText(null);
                    }
                    else{
                        setText(item.toString());
                    }
                }
            });
            // Update the existing courseListView with the new data
            ObservableList<Course> observableCourseList = FXCollections.observableList(courses);
            courseListView.setItems(observableCourseList);

            courseListView.setOnMouseClicked(event -> {
                Course selectedCourse = courseListView.getSelectionModel().getSelectedItem();
                if(selectedCourse != null){
                    try{
                        handleCourseClick(selectedCourse);
                    }
                    catch(SQLException e){
                        throw new RuntimeException(e);
                    }
                }
            });

        } catch (SQLException e) {
            throw e;
        }
    }


    /**
     *  Adds a course to the database which should update in the ListView as well immediately
     */
    @FXML
    private void addCourseScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("courseAddScreen.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) addCourseButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("Add Course");
            stage.setScene(newScene);
            stage.show();
            AddCourseController controller = loader.getController();
            controller.addCourseInitialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the list of courses displayed in the courseListView based on the search criteria.
     * Searches by subject, number, and title, and updates the view accordingly.
     */
    private void submitSearch() throws SQLException {
        //TODO:
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
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

