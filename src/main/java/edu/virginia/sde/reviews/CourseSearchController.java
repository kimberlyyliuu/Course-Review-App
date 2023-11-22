package edu.virginia.sde.reviews;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.lang.reflect.Field;

public class CourseSearchController {
    @FXML
    private TextField coursePneumonic;
    @FXML
    private TextField courseNumber;
    @FXML
    private TextField courseTitle;
    @FXML
    private Label pneumonicLabel;
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
    protected void courseSearchInitialize(){
        exitButton.setOnAction(event -> Platform.exit());
    }
}

