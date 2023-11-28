package edu.virginia.sde.reviews;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MyReviewsController {

    @FXML
    private Button backtoCourseSearchButton;
    @FXML
    private TableView<Review> tableView;
    @FXML
    private TableColumn mnemonicColumn;
    @FXML
    private TableColumn numberColumn;
    @FXML
    private TableColumn ratingColumn;
    private User activeUser = new User("", "");
    public void setActiveUser(User user){
        activeUser.setUsername(user.getUsername());
        activeUser.setPassword(user.getPassword());
    }
    private final DatabaseDriver databaseDriver = new DatabaseDriver("course_app.sqlite");



    protected void myReviewsIntialize(){
        backtoCourseSearchButton.setOnAction(event -> openCourseSearchScene());

        // Set up the columns
        mnemonicColumn.setCellValueFactory(new PropertyValueFactory<>("mnemonic")); //I dont think this is correct, pls help
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));//I dont think this is correct, pls help
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));//I dont think this is correct, pls help

        populateTable();

        // Set up the button action
        backtoCourseSearchButton.setOnAction(event -> openCourseSearchScene());

        // Set up the click action on the table rows
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {  // Double-click
                Review selectedReview = tableView.getSelectionModel().getSelectedItem();
                if (selectedReview != null) {
                    openCourseReviewScene(selectedReview);
                }
            }
        });
    }

    private void populateTable() {
        try {
            databaseDriver.connect();
            List<Review> reviewList = databaseDriver.getReviewsByUser(activeUser);
            ObservableList<Review> observableReviewList = FXCollections.observableArrayList(reviewList);
            tableView.setItems(observableReviewList);
            databaseDriver.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void openCourseReviewScene(Review selectedReview) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("courseReviewScreen.fxml"));
            Parent root = loader.load();

            // Pass the selected review to the next controller
            CourseReviewController controller = loader.getController();
            controller.initialize();


            // Create a new scene
            Scene newScene = new Scene(root);

            // Stage and new scene for new user
            Stage stage = (Stage) backtoCourseSearchButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("Course Review");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
