package edu.virginia.sde.reviews;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CourseReviewController {

    @FXML
    private Label courseTitleLabel;

    private DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");

    @FXML
    protected void initialize() {

    }

    public void setData(Course course){
        courseTitleLabel.setText(course.getCourseName());
    }

}




