package edu.virginia.sde.reviews;


import java.sql.Timestamp;


public class Review {
    private final int userID;
    private int courseID;
    private int rating;  // should be 1-5
    private String comment;
    private Timestamp timestamp;

    public Review(int userID, int courseID, int rating, String comment) {
        this.userID = userID;
        this.courseID = courseID;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = new Timestamp(System.currentTimeMillis()); // Set the timestamp to the current date and time

    }
    public Review(int userID, int courseID, int rating) {
        this.userID = userID;
        this.courseID = courseID;
        this.rating = rating;
        this.timestamp = new Timestamp(System.currentTimeMillis()); // Set the timestamp to the current date and time

    }

    //This constructor is used in DatabaseDriver when returning a list of all reviews in the database
    public Review(int userID, int courseID, int rating, String comment, Timestamp timestamp) {
        this.userID = userID;
        this.courseID = courseID;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;

    }


    public int getCourseID() {
        return courseID;
    }

    public int getUserID() {
        return userID;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
