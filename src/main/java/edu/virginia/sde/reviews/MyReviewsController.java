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
public class MyReviewsController {

    @FXML
    private Button backtoCourseSearchButton;
    @FXML
    private TableColumn Mnemonic;
    @FXML
    private TableColumn number;
    @FXML
    private TableColumn rating;


    protected void myReviewsIntialize(){
        backtoCourseSearchButton.setOnAction(event -> openCourseSearchScene());
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
