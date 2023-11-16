package edu.virginia.sde.reviews;

import java.util.Date;

public class Review {
    private final String user;
    private String courseName;
    private double rating;
    private String comment;
    private Date timestamp;

    public Review(String user, String courseName, double rating, String comment) {
        this.user = user;
        this.courseName = courseName;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = new Date(); // Set the timestamp to the current date and time
    }

    public String getCourseName() {
        return courseName;
    }

    public String getUser() {
        return user;
    }

    public double getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
