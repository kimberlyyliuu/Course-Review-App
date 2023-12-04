package edu.virginia.sde.reviews;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MyReviewsController {

    @FXML
    private Button backToCourseSearchButton;
    @FXML
    private TableView<MyReviewsResult> tableView;
    private final User activeUser = new User("", "");
    public void setActiveUser(User user){
        activeUser.setUsername(user.getUsername());
        activeUser.setPassword(user.getPassword());
    }
    private final DatabaseDriver databaseDriver = new DatabaseDriver("course_app.sqlite");



    protected void myReviewsIntialize(){
        backToCourseSearchButton.setOnAction(event -> openCourseSearchScene());
        populateTable();

        // Set up the button action
        backToCourseSearchButton.setOnAction(event -> openCourseSearchScene());

        // Set up the click action on the table rows
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                MyReviewsResult selectedReview = tableView.getSelectionModel().getSelectedItem();
                if (selectedReview != null) {
                    openCourseReviewScene(selectedReview);
                }
            }
        });
    }

    private void populateTable() {
        try {
            databaseDriver.connect();
            int userID = databaseDriver.getUserIDByUsername(activeUser.getUsername());
            List<MyReviewsResult> reviewList = databaseDriver.getUserReviews(userID);
            ObservableList<MyReviewsResult> observableReviewList = FXCollections.observableArrayList(reviewList);
            tableView.setItems(observableReviewList);
            databaseDriver.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void openCourseReviewScene(MyReviewsResult selectedReview) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("courseReviewScreen.fxml"));
            Parent root = loader.load();

            // Pass the selected review to the next controller
            CourseReviewController controller = loader.getController();
            databaseDriver.connect();
            controller.setActiveUser(activeUser);
            controller.setData(databaseDriver.getCourseForMyReviewResult(selectedReview), activeUser);
            databaseDriver.disconnect();

            // Create a new scene
            Scene newScene = new Scene(root);

            // Stage and new scene for new user
            Stage stage = (Stage) backToCourseSearchButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("Course Review");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void openCourseSearchScene() {
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
            controller.courseSearchInitialize();
            controller.setActiveUser(activeUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
