package edu.virginia.sde.reviews;

public class MyReviewsResult {
    private int rating;
    private String courseMnemonic;
    private int courseNumber;
    private int courseID;

    public MyReviewsResult(int rating, String courseMnemonic, int courseNumber, int courseID) {
        this.rating = rating;
        this.courseMnemonic = courseMnemonic;
        this.courseNumber = courseNumber;
        this.courseID = courseID;
    }

    public int getRating() {
        return rating;
    }

    public String getCourseMnemonic() {
        return courseMnemonic;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public int getCourseID() {
        return courseID;
    }
}