package edu.virginia.sde.reviews;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    private Button addCourse;
    @FXML
    protected void courseSearchInitialize(){
        loadCourses();
        exitButton.setOnAction(event -> Platform.exit());
        addCourse.setOnAction(event -> {
            try {
                addCourse();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void loadCourses() {
        try {
            DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
            dbDriver.connect();
            dbDriver.createTables();
            List<Course> courses = new ArrayList<>();
            courses = dbDriver.getAllCourses();
            ObservableList<Course> observableCourseList = FXCollections.observableList(courses);
            ListView<Course> courselistView = new ListView<>(observableCourseList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void addCourse() throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
        dbDriver.connect();
        dbDriver.createTables();

    }
}

