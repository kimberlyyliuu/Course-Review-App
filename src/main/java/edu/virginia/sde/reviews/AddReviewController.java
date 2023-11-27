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
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;


public class AddReviewController {
    @FXML
    private TextField inputRating;
    @FXML
    private TextField inputComment;

    @FXML
    private Label courseTitleLabel;
    @FXML
    private Label mnemonicAndNumberLabel;
    @FXML
    private Label averageRatingLabel;
    @FXML
    private Label errorMessage;
    @FXML
    private Button submitReviewButton;

    @FXML
    private Button backtoCourseSearchButton;
    @FXML
    private Button deleteReviewButton;

    private User activeUser = new User("", "");
    public void setActiveUser(User user){
        activeUser.setUsername(user.getUsername());
        activeUser.setPassword(user.getPassword());
    }

    private DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");

    @FXML
    protected void initialize(){
        backtoCourseSearchButton.setOnAction(event -> openCourseSearchScene());
        submitReviewButton.setOnAction(event -> {
            try {
                handleAddReview();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setData(Course course){
        courseTitleLabel.setText(course.getCourseName());
        mnemonicAndNumberLabel.setText(course.getMnemonic() + " " + course.getCourseNumber());
        averageRatingLabel.setText(String.valueOf("Average Rating: " + course.getAverageRating()));
    }


    private void handleAddReview() throws SQLException {
        // Split the mnemonicAndNumberLabel content into mnemonic and number
        String[] parts = mnemonicAndNumberLabel.getText().split("\\s+");

        var mnemonic = parts[0];
        var number = parts[1];

        var rating = Integer.parseInt(inputRating.getText());
        var userID = dbDriver.getUserIDbyusername(activeUser.getUsername());
        var courseID = dbDriver.getCourseIDbyCourseTitleandMnemonic(courseTitleLabel.getText(), mnemonic, number);
        // Agent: ChatGPT
        // Usage: Asked how to check if field is filled or not
        // Check if inputComment is not empty before using its value
        var comment = inputComment.getText().isEmpty() ? null : inputComment.getText();

        try {
            dbDriver.connect();
            dbDriver.createTables();

            if (comment != null && isValidRating(rating)) {
                Review review = new Review(userID, courseID, rating, comment);
                dbDriver.addReview(review);
                dbDriver.commit();
                inputComment.clear();
                inputRating.clear();
            } else if (comment == null) {
                Review review = new Review(userID, courseID, rating);
                dbDriver.addReview(review);
                dbDriver.commit();
                inputComment.clear();
                inputRating.clear();
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                dbDriver.disconnect();
            } catch (SQLException e) {
                throw e;
            }

        }
    }


    private boolean isValidRating(Integer ratingText){
        try {
            int rating = Integer.parseInt(String.valueOf(ratingText));

            // Check if the rating is within the valid range (1 to 5)
            if (rating >= 1 && rating <= 5) {
                return true;
            } else {
                Platform.runLater(() -> {
                    errorMessage.setText("Please make sure your rating is between 1 to 5, inclusively");
                });
                return false;
            }
        } catch (NumberFormatException e) {
            // Handle the case where the input is not a valid integer
            return false;
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
            stage.setTitle("Course Search");
            stage.setScene(newScene);
            stage.show();
            CourseSearchController controller = loader.getController();
            controller.setActiveUser(activeUser);
            controller.courseSearchInitialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
