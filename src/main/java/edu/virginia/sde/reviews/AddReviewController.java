package edu.virginia.sde.reviews;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;


public class AddReviewController {
    @FXML
    private TextField inputRating;
    @FXML
    private TextArea inputComment;

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
    private Button backtoCourseReviewButton;
    private int userID;
    private int courseID;
    private Course currentCourse;
    private User activeUser = new User("", "");
    public void setActiveUser(User user){
        activeUser.setUsername(user.getUsername());
        activeUser.setPassword(user.getPassword());
    }

    private DatabaseDriver dbDriver = new DatabaseDriver("course_app.sqlite");

    @FXML
    protected void initialize(){
        backtoCourseSearchButton.setOnAction(event -> openCourseSearchScene());
        backtoCourseReviewButton.setOnAction(event -> openCourseReviewScene());
        try {
            userID = getUserID();
            courseID = getCourseID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        handleReturningReviewer(); //loads the textfields

        try {
            if (userReviewed()){
                submitReviewButton.setOnAction(event -> {
                    try {

                        handleUpdateReview();
                       // openCourseSearchScene();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else{
                submitReviewButton.setOnAction(event -> {
                    try {

                        handleAddReview();
                       // openCourseSearchScene();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


    public void setData(String courseTitle, String mnemonicAndNumber, String rating){
        courseTitleLabel.setText(courseTitle);
        mnemonicAndNumberLabel.setText(mnemonicAndNumber);
        averageRatingLabel.setText(rating);
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
            try {
                dbDriver.disconnect();
            } catch (SQLException e) {
                throw e;
            }

        }
    }

    private void handleReturningReviewer(){
        try {
            dbDriver.connect();
            if (userReviewed()){
                double rate = dbDriver.loadRatingbyUserID(userID, courseID);
                int rating = (int) rate;
                inputRating.setText(String.valueOf(rating));
                inputComment.setText(dbDriver.loadCommentbyUserID(userID, courseID));
                submitReviewButton.setText("Save Review");
                screentitle.setText("Edit Review");
            }
            dbDriver.disconnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //public void updatereview trhows SQL
    //edit review from db driver

    private void handleUpdateReview() throws SQLException{

        try{
            var ratingText = inputRating.getText();


            // Agent: ChatGPT
            // Usage: Asked how to check if field is filled or not
            // Check if inputComment is not empty before using its value
            var comment = inputComment.getText().isEmpty() ? null : inputComment.getText();


            if(!checkForDecimals(ratingText) && ratingText.matches("\\d+")) {
                var rating = Integer.parseInt(inputRating.getText());
                if (comment != null && isValidRating(rating)) {
                    dbDriver.connect();
                    dbDriver.editReview(userID, courseID,inputComment.getText(), inputRating.getText() );
                    dbDriver.updateAverageRating(courseID);
                    dbDriver.commit();
                    dbDriver.disconnect();
                    openCourseReviewScene();
                } else if (comment == null && isValidRating(rating)) {
                    dbDriver.connect();
                    dbDriver.editReview(userID, courseID,"", inputRating.getText() );
                    dbDriver.updateAverageRating(courseID);
                    dbDriver.commit();
                    dbDriver.disconnect();
                    openCourseReviewScene();
                } else if (!isValidRating(rating)) {
                    Platform.runLater(() -> {
                        errorMessage.setText("Rating must be a whole number integer between 1 and 5");
                    });
                }
            }else{
                Platform.runLater(() -> {
                    errorMessage.setText("Rating must be a whole number integer between 1 and 5");
                });
            }


        } catch (SQLException e) {
            throw e;
        } finally {
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

    private int getCourseID() throws SQLException {
        try {
            if (dbDriver.connection.isClosed()){
                dbDriver.connect();
            }
            String[] parts = mnemonicAndNumberLabel.getText().split("\\s+");
            var mnemonic = parts[0];
            var number = parts[1];

            int id =  dbDriver.getCourseIDbyCourseTitleandMnemonic(courseTitleLabel.getText(), mnemonic, number);
            dbDriver.disconnect();
            return id;
        } catch (SQLException e) {
            throw e;
        }
    }



    private void handleAddReview() throws SQLException {
        // Split the mnemonicAndNumberLabel content into mnemonic and number
        String[] parts = mnemonicAndNumberLabel.getText().split("\\s+");

        var mnemonic = parts[0];
        var number = parts[1];

        var ratingText = inputRating.getText();


        // Agent: ChatGPT
        // Usage: Asked how to check if field is filled or not
        // Check if inputComment is not empty before using its value
        var comment = inputComment.getText().isEmpty() ? null : inputComment.getText();

        try {
            dbDriver.connect();
            dbDriver.createTables();

            if(!checkForDecimals(ratingText) && ratingText.matches("\\d+")) {
                var rating = Integer.parseInt(inputRating.getText());
                if (comment != null && isValidRating(rating)) {
                    Review review = new Review(userID, courseID, rating, comment);
                    dbDriver.addReview(review);
                    dbDriver.updateAverageRating(courseID);
                    dbDriver.commit();
                    inputComment.clear();
                    inputRating.clear();
                    dbDriver.disconnect();
                    openCourseReviewScene();
                } else if (comment == null && isValidRating(rating)) {
                    Review review = new Review(userID, courseID, rating);
                    dbDriver.addReview(review);
                    dbDriver.updateAverageRating(courseID);
                    dbDriver.commit();
                    inputComment.clear();
                    inputRating.clear();
                    dbDriver.disconnect();
                    openCourseSearchScene();
                } else if (!isValidRating(rating)) {
                    Platform.runLater(() -> {
                        errorMessage.setText("Rating must be a whole number integer between 1 and 5");
                    });
                }
            }
            else{
                Platform.runLater(() -> {
                    errorMessage.setText("Rating must be a whole number integer between 1 and 5");
                });
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

    private boolean checkForDecimals(String ratingText){
        return ratingText.contains(".");
    }



    private boolean isValidRating(Integer ratingText){
        try {
            int rating = Integer.parseInt(String.valueOf(ratingText));

            //Check # is whole number integer

            //Check if the rating is within the valid range (1 to 5)
            if (rating >= 1 && rating <= 5) {
                return true;
            } else {
                Platform.runLater(() -> {
                    errorMessage.setText("Please make sure your rating is between 1 to 5, inclusively");
                });
                return false;
            }
        } catch (NumberFormatException e) {
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




    private void openCourseReviewScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("courseReviewScreen.fxml"));
            Parent root = loader.load();
            // Create a new scene
            Scene newScene = new Scene(root);
            // Stage and new scene for new user
            Stage stage = (Stage) backtoCourseReviewButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("Course Review");
            stage.setScene(newScene);
            stage.show();
            CourseReviewController controller = loader.getController();
            controller.setActiveUser(activeUser);
            dbDriver.connect();
            controller.setData(dbDriver.getCourseByCourseID(courseID), activeUser);
            dbDriver.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
