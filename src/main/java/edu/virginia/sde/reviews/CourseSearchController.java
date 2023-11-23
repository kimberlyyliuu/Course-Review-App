package edu.virginia.sde.reviews;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
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
    }
    //This method loads the update ListView
    private void loadCourses() {
        try {
            DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");
            dbDriver.connect();
            dbDriver.createTables();
            Course test = new Course("123456", 1234, "4", 4);
            dbDriver.addCourse(test);
            List<Course> courses = new ArrayList<>();
            courses = dbDriver.getAllCourses();
            ObservableList<Course> observableCourseList = FXCollections.observableList(courses);
            ListView<Course> courselistView = new ListView<>(observableCourseList);

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
        int number = Integer.parseInt(courseNumber.getText()); // Assuming it's an integer
        String mnemonic = courseMnemonic.getText();
        String title = courseTitle.getText();

        Course newCourse = new Course(mnemonic, number, title);
        dbDriver.addCourse(newCourse);

        // Update the ListView
        loadCourses();
    }
    //Update the ListView
    private void submitSearch(){
    }

}

