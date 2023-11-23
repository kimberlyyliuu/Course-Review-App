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

    private ListView<Course> courseListView;
    private DatabaseDriver dbDriver;

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
            handleAddCourse();
        });
    }


    private void handleAddCourse(){
        var mnemonicText = courseMnemonic.getText();
        var number = courseNumber.getText();
        var title = courseTitle.getText();

        try{
            DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
            dbDriver.connect();
            dbDriver.createTables();

            if(!dbDriver.checkCourseExists(mnemonicText, number, title)){
                dbDriver.commit();
                dbDriver.disconnect();
                Platform.runLater(() -> {
                    errorMessage.setText("Course Added!");
                    addCourseInitialize();
                });
            }else if(dbDriver.checkCourseExists(mnemonicText, number, title)){
                Platform.runLater(() -> {
                    errorMessage.setText("Course Already Exists");
                    try {
                        dbDriver.disconnect();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    addCourseInitialize();
                });
            }
        }catch (SQLException ex){
            throw new RuntimeException(ex);
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
