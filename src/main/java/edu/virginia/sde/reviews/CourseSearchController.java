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
import java.util.List;

public class CourseSearchController {
    @FXML
    private TextField courseMnemonic;
    @FXML
    private TextField courseNumber;
    @FXML
    private TextField courseTitle;
    @FXML
    private TableView<Course> courseTableView;
    @FXML
    private Button exitButton;
    @FXML
    private Button addCourseButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearSearchButton;
    @FXML
    private Button myReviewsButton;
    @FXML
    private Button logoutButton;
    private final User activeUser = new User("", "");
    private final DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
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
                if (dbDriver.connection.isClosed()){
                    dbDriver.connect();
                }
                submitSearch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        clearSearchButton.setOnAction(event -> {
            try {
                if (dbDriver.connection.isClosed()){
                    dbDriver.connect();
                }
                loadCourses();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        addCourseButton.setOnAction(event -> addCourseScreen());
        myReviewsButton.setOnAction(event -> showMyReviews());
        logoutButton.setOnAction(event -> setLogoutButton());


        //Clickable courses
        courseTableView.setOnMouseClicked(event -> {
            Course selectedCourse = courseTableView.getSelectionModel().getSelectedItem();
            if(selectedCourse!=null) {
                try{
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
            controller.setActiveUser(this.activeUser);
            controller.setData(selectedCourse, activeUser);

            Stage stage = (Stage) courseTableView.getScene().getWindow();
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
        List<Course> courses = dbDriver.getAllCourses(); // Retrieve the list of courses from the database

        ObservableList<Course> observableCourseList = FXCollections.observableList(courses);
        courseTableView.setItems(observableCourseList);
        //Agent: ChatGPT
        //Usage: Asked how to display average rating column to 2 decimal places
        TableColumn<Course, Double> averageRatingColumn = (TableColumn<Course, Double>) courseTableView.getColumns().get(3); // Assuming 3 is the index of the "Average Rating" column
        averageRatingColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else if(item < 1) {
                    setText("");
                }
                else {
                    setText(String.format("%.2f", item));
                }
            }
        });
        // Agent: ChatGPT
        // Usage: Help making courses clickable
        courseTableView.setOnMouseClicked(event -> {
            Course selectedCourse = courseTableView.getSelectionModel().getSelectedItem();
            if(selectedCourse!= null){
                try{
                    handleCourseClick(selectedCourse);
                } catch(SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    /**
     *  Adds a course to the database which should update in the ListView as well immediately
     */
    @FXML
    private void addCourseScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddCourseScreen.fxml"));
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
            controller.setActiveUser(this.activeUser);
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
        String subject = courseMnemonic.getText().toUpperCase().trim();
        String number = courseNumber.getText().trim();
        String title = courseTitle.getText().trim();

        // Implement search logic based on subject, number, and title
        // Update the courseListView with the search results
        // You may need to modify loadCourses or create a new method to handle search results
        // For simplicity, assuming all courses are shown in this example
        List<Course> coursesList = dbDriver.getSearchedCourses(subject, number, title);
        ObservableList<Course> observableCourseList = FXCollections.observableList(coursesList);
        courseTableView.setItems(observableCourseList);
        dbDriver.disconnect();
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
            controller.setActiveUser(activeUser);
            controller.myReviewsInitialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setActiveUser(User user){
        activeUser.setUsername(user.getUsername());
        activeUser.setPassword(user.getPassword());
    }

    private void setLogoutButton() {
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

