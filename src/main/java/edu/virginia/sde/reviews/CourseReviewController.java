package edu.virginia.sde.reviews;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CourseReviewController {

    @FXML
    private Label courseTitleLabel;
    @FXML
    private Label mnemonicAndNumberLabel;
    @FXML
    private Label averageRatingLabel;
    @FXML
    private Button addReviewButton;
    @FXML
    private Button backtoCourseSearchButton;
    @FXML
    private TableView<Review> reviewsTableView;

    private User activeUser = new User("", "");
    private Course currentCourse;
    public void setActiveUser(User user){
        activeUser.setUsername(user.getUsername());
        activeUser.setPassword(user.getPassword());
    }
    private DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");

    @FXML
    protected void initialize() {
        try {
            dbDriver.connect();
            dbDriver.createTables();

            loadReviews();
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
        backtoCourseSearchButton.setOnAction(event -> openCourseSearchScene());
        addReviewButton.setOnAction(event -> openAddReviewControllerScene());
        System.out.println(activeUser.getUsername());
    }


    public void setData(Course course){
        courseTitleLabel.setText(course.getCourseName());
        mnemonicAndNumberLabel.setText(course.getMnemonic() + " " + course.getCourseNumber());
        averageRatingLabel.setText(String.valueOf("Average Rating: " + course.getAverageRating()));
        currentCourse = course;
    }


//    private void setDeleteReviewButton() throws SQLException {
//        try {
//            dbDriver.connect();
//            if ()
//
//        } catch (SQLException e) {
//            throw e;
//        } finally {
//            try {
//                dbDriver.disconnect();
//            } catch (SQLException e) {
//                throw e;
//            }
//        }
//    }

    private void openCourseSearchScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseSearch.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) backtoCourseSearchButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("Course Search");
            stage.setScene(newScene);
            stage.show();
            CourseSearchController controller = loader.getController();
            controller.courseSearchInitialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAddReviewControllerScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddReviewScreen.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) addReviewButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("Add Review");
            stage.setScene(newScene);
            stage.show();

            AddReviewController controller = loader.getController();
            controller.setActiveUser(activeUser);
            controller.setData(courseTitleLabel.getText(), mnemonicAndNumberLabel.getText(), averageRatingLabel.getText());
            controller.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadReviews() {
        try {
            var courseID = dbDriver.getCourseIDbyCourseTitleandMnemonic(currentCourse.getCourseName(), currentCourse.getMnemonic(), String.valueOf(currentCourse.getCourseNumber()));
            List<Review> reviewsList = dbDriver.getReviewsByCourseID(courseID);
            ObservableList<Review> observableReviewsList = FXCollections.observableList(reviewsList);
            reviewsTableView.getItems().clear();
            reviewsTableView.getItems().addAll(observableReviewsList);
            System.out.println(currentCourse);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}




