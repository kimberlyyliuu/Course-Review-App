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
import java.sql.PreparedStatement;
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
    private Label screentitle;

    @FXML
    private Button backtoCourseSearchButton;
    @FXML
    private Button deleteReviewButton;
    private int userID;
    private int courseID;
    private User activeUser = new User("", "");
    public void setActiveUser(User user){
        activeUser.setUsername(user.getUsername());
        activeUser.setPassword(user.getPassword());
    }

    private DatabaseDriver dbDriver;

    @FXML
    protected void initialize(){
        dbDriver = new DatabaseDriver("course_app.sqlite");
        backtoCourseSearchButton.setOnAction(event -> openCourseSearchScene());
        try {
            userID = getUserID();
            handleReturningReviewer();
            setReviewButtonAction();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //handleReturningReviewer(); //loads the textfields

        try {
            if (userReviewed()){
                submitReviewButton.setOnAction(event -> {
                    try {
                        handleUpdateReview();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else{
                submitReviewButton.setOnAction(event -> {
                    try {
                        handleAddReview();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setReviewButtonAction() throws SQLException {
        try{
            if(userReviewed()){
                submitReviewButton.setOnAction(event -> {
                    try {
                        handleUpdateReview();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            else{
                submitReviewButton.setOnAction(event -> {
                    try {
                        handleAddReview();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    public void setData(Course course) throws SQLException {
        courseTitleLabel.setText(course.getCourseName());
        mnemonicAndNumberLabel.setText(course.getMnemonic() + " " + course.getCourseNumber());
        averageRatingLabel.setText("Average Rating: " + course.getAverageRating());
        setCourseID(course);
    }

    private void setCourseID(Course course) throws SQLException {
        dbDriver.connect();
        courseID = dbDriver.getCourseIDByCourse(course);
        dbDriver.disconnect();
    }


    private boolean userReviewed() throws SQLException{
        try{
            if (dbDriver.connection.isClosed()){
                dbDriver.connect();
            }
            return dbDriver.userIDAlreadyReviewedCourse(userID, courseID);
        } catch (SQLException e) {
            throw e;
        } finally {
            dbDriver.disconnect();
        }
    }

    private void handleReturningReviewer() throws SQLException {
        try {
            dbDriver.connect();
            if (userReviewed()){
                inputRating.setText(dbDriver.loadRatingbyUserID(userID, courseID));
                inputComment.setText(dbDriver.loadCommentbyUserID(userID, courseID));
                submitReviewButton.setText("Edit Review");
                screentitle.setText("Edit Review");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            dbDriver.disconnect();
        }
    }

    //public void update review throws SQL
    //edit review from db driver

    private void handleUpdateReview() throws SQLException{
        try{
            dbDriver.connect();
            dbDriver.editReview(userID, courseID,inputComment.getText(), inputRating.getText() );
            dbDriver.commit();
            Platform.runLater(() -> {
                errorMessage.setText("Review Edited!");
            });
        } catch (SQLException e) {
            throw e;
        }
        finally {
            dbDriver.disconnect();
        }
    }


    private int getUserID() throws SQLException {
        try {
            dbDriver.connect();

            int id=  dbDriver.getUserIDbyusername(activeUser.getUsername());
            dbDriver.disconnect();
            return id;
        } catch (SQLException e) {
            throw e;
        }
    }




    private void handleAddReview() throws SQLException {
        var rating = Integer.parseInt(inputRating.getText());
        // Agent: ChatGPT
        // Usage: Asked how to check if field is filled or not
        // Check if inputComment is not empty before using its value
        var comment = inputComment.getText().isEmpty() ? null : inputComment.getText();
        try{
            dbDriver.connect();
            dbDriver.createTables();

            var userID = dbDriver.getUserIDbyusername(activeUser.getUsername());
            if (comment != null && isValidRating(rating)) {
                Review review = new Review(userID, courseID, rating, comment);
                dbDriver.addReview(review);
                dbDriver.commit();
                inputComment.clear();
                inputRating.clear();
                Platform.runLater(() -> {
                    errorMessage.setText("Review Added!");
                });
            } else if (comment == null) {
                Review review = new Review(userID, courseID, rating);
                dbDriver.addReview(review);
               // dbDriver.commit();
                inputComment.clear();
                inputRating.clear();
                Platform.runLater(() -> {
                    errorMessage.setText("Review Added!");
                });
            } else if (!isValidRating(rating)) {
                Platform.runLater(() -> {
                    errorMessage.setText("Rating must be between 1 and 5");
                });
            }
        } catch (SQLException e) {
            throw e;
        }
        finally {
            try{
                dbDriver.disconnect();
            }
            catch(SQLException e){
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
